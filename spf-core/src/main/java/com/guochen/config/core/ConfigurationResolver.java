package com.guochen.config.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.jasonclawson.jackson.dataformat.hocon.HoconFactory;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.guochen.config.datamodel.JobConfig;
import com.guochen.config.util.ConfigPathFetcher;
import com.guochen.config.util.MyUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.extern.slf4j.Slf4j;


/**
 * This begins his journey to the most powerful configuration file resolver in the universe.
 *
 * Currently it provides features to:
 * 1. Read/Parse an input HOCON file to a JAVA object
 * 2. Validation support for configuration values
 * 3. Suggestions/recommendations for invalid configuration keys
 * 4. Key-path based value updates and provide merged JAVA object
 *
 * TODO: Features to support in the future:
 * 1. Multiple hierarchy file inheritance
 * 2. Cross file value reference based on key-path
 */
@Slf4j
public class ConfigurationResolver<T extends JobConfig> {
  public final static String JSON_ROOT_SIGN = "$";
  private final static int NUM_OF_RECOMMENDATIONS = 3;

  private final Class<T> _jobConfigType;
  private final String _fileContent;
  private final Map<String, String> _updates;
  private List<String> supportedConfigurations;

  /**
   * @param configFile local config file
   * @throws FileNotFoundException
   */
  public ConfigurationResolver(Class<T> jobConfigType, File configFile) throws FileNotFoundException {
    this(jobConfigType, configFile, new HashMap<>());
  }

  public ConfigurationResolver(Class<T> jobConfigType, File configFile, Map<String, String> updates)
      throws FileNotFoundException {
    this(jobConfigType, new FileInputStream(configFile), updates);
  }

  public ConfigurationResolver(Class<T> jobConfigType, String configFileContent) {
    this(jobConfigType, configFileContent, new HashMap<>());
  }

  /**
   * @param jobConfigType jobSpec
   * @param fileInputStream the input configuration file string
   * @param updates the updates based on path to override values in fileInputStream
   */
  public ConfigurationResolver(Class<T> jobConfigType, InputStream fileInputStream, Map<String, String> updates) {
    this(jobConfigType, MyUtil.streamToString(fileInputStream), updates);
  }

  /**
   * @param jobConfigType the type of the job config that the manager will work on
   * @param configFileContent
   * @param updates it is optional to have "$." as the prefix of the json path
   */
  public ConfigurationResolver(Class<T> jobConfigType, String configFileContent, Map<String, String> updates) {
    _jobConfigType = jobConfigType;
    _fileContent = configFileContent;
    _updates = updates;
  }

  /**
   * Resolve the input file and updates map into a single JobConfig object
   */
  public T resolve() {
    try {
      ObjectMapper mapper = new ObjectMapper(new HoconFactory());
      JsonNode json = mapper.readValue(_fileContent, JsonNode.class);
      ObjectMapper objMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
      String jsonStr = objMapper.writeValueAsString(json);

      if (!_updates.isEmpty()) {
        jsonStr = updateJson(jsonStr);
      }
      return objMapper.readValue(jsonStr, _jobConfigType);
    } catch (UnrecognizedPropertyException e) {
      StringBuilder pathBuilder = new StringBuilder();
      for (JsonMappingException.Reference ref : e.getPath()) {
        if (ref.getFieldName() != null) {
          pathBuilder.append(".").append(ref.getFieldName());
        } else {
          pathBuilder.append("[").append(Integer.toString(ref.getIndex())).append("]");
        }
      }
      String unknownPath = pathBuilder.deleteCharAt(0).toString();
      log.error("Unknown path: {} ", unknownPath);
      List<String> recommendations = buildRecommendations(unknownPath, NUM_OF_RECOMMENDATIONS);
      String errorMsg = String.format("Unknown config key path '%s'. Top %d recommendations:\n%s", unknownPath,
          NUM_OF_RECOMMENDATIONS, MyUtil.concatString(recommendations, "\n"));
      log.error(errorMsg);
      log.error(
          "All supported configurations are listed as below:\n" + MyUtil.concatString(supportedConfigurations, "\n"));
      throw new RuntimeException(errorMsg, e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Update json string based on a map of (json path -> updated value)
   * @param jsonStr the json string to be updated
   * @return the updated json
   */
  public String updateJson(String jsonStr) throws JsonProcessingException {
    //Update json based on the updates map
    DocumentContext inputJson = JsonPath.parse(jsonStr);

    String updateOrAddPath = null;
    try {
      for (Map.Entry<String, String> update : _updates.entrySet()) {
        updateOrAddPath = update.getKey();
        int lastDot = updateOrAddPath.lastIndexOf(".");
        String objPath;
        String key;
        if (lastDot == -1) {
          objPath = JSON_ROOT_SIGN;
          key = updateOrAddPath;
        } else {
          objPath = updateOrAddPath.substring(0, lastDot);
          key = updateOrAddPath.substring(lastDot + 1, updateOrAddPath.length());
        }
        inputJson.put(objPath, key, update.getValue());
      }
    } catch (PathNotFoundException e) {
      List<String> recommendations = buildRecommendations(updateOrAddPath, NUM_OF_RECOMMENDATIONS);
      String errorMsg = String.format("Unknown update key path '%s'. Top %d recommendations:\n%s", updateOrAddPath,
          NUM_OF_RECOMMENDATIONS, MyUtil.concatString(recommendations, "\n"));
      throw new RuntimeException(errorMsg, e);
    }

    Object merged = inputJson.read(JSON_ROOT_SIGN);
    return new ObjectMapper().writeValueAsString(merged);
  }

  /**
   * @param unknownPath the unknown configuration path based on which recommendations will be given
   * @param count specify the number of recommendations that you want to get
   * @return a list of recommended configuration keys
   */
  private List<String> buildRecommendations(String unknownPath, int count) {
    List<String> recommendations = new ArrayList<>();
    if (count <= 0 || unknownPath == null) {
      return recommendations;
    }

    TreeMap<Integer, String> relavanceMap = new TreeMap<>();
    for (String key : getSupportedConfigurations()) {
      relavanceMap.put(minimumChanges(unknownPath, key), key);
    }

    Map.Entry<Integer, String> lastRec = relavanceMap.firstEntry();
    recommendations.add(lastRec.getValue());
    for (int i = 1; i < count; ++i) {
      lastRec = relavanceMap.ceilingEntry(lastRec.getKey() + 1);
      recommendations.add(lastRec.getValue());
    }
    return recommendations;
  }

  /**
   * @return the original file content before updates
   */
  public String getOrigFileContent() {
    return _fileContent;
  }

  /**
   * The lazy Getter for supportedConfigurations
   */
  private List<String> getSupportedConfigurations() {
    if (supportedConfigurations != null) {
      return supportedConfigurations;
    }
    supportedConfigurations = ConfigPathFetcher.getConfigKeyPaths(_jobConfigType);
    return supportedConfigurations;
  }

  /**
   * @return the minimum changes(insert, delete and replace) to convert path1 to path2
   */
  static int minimumChanges(String path1, String path2) {
    int column = path1.length();
    int row = path2.length();
    int[][] dp = new int[row + 1][column + 1];

    for (int r = 1; r <= row; ++r) {
      //initialize with r steps of insertion
      dp[r][0] = r;
    }

    for (int c = 1; c <= column; ++c) {
      //initialize with c steps of deletion
      dp[0][c] = c;
    }

    for (int r = 1; r <= row; ++r) {
      for (int c = 1; c <= column; ++c) {
        char path1c = path1.charAt(c - 1);
        char path2c = path2.charAt(r - 1);
        if (path1c == path2c) {
          dp[r][c] = dp[r - 1][c - 1];
        } else {
          dp[r][c] = Math.min(dp[r - 1][c - 1] + 1, //Replace last letter of path1 to last letter of path2
              Math.min(dp[r - 1][c] + 1,   //Insert the last letter of path2
                  dp[r][c - 1] + 1)); //Delete the last letter of path1
        }
      }
    }
    return dp[row][column];
  }
}
