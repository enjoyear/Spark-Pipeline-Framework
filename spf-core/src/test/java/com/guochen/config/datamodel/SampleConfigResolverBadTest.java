package com.guochen.config.datamodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.guochen.config.core.ConfigurationResolver;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class SampleConfigResolverBadTest {
  @Test
  public void testErrorMessagesForBadConfig() throws FileNotFoundException {
    File configFile = new File(getClass().getResource("/configs/bad/bad-config.hocon").getFile());
    ConfigurationResolver<SampleConfig>
        parser = new ConfigurationResolver(SampleConfig.class, configFile);
    SampleConfig sampleConfig = parser.resolve();
    List<String> validationErrors = sampleConfig.getValidationErrors();
    Collections.sort(validationErrors);
    for (String validationError : validationErrors) {
      System.out.println(validationError);
    }

    Assert.assertEquals(validationErrors.size(), 7);
    Assert.assertEquals(validationErrors.get(0),
        "Distributed column 'x' must be included in the sortColumns '[a, b]'. Distributed column 'y' must be included in the sortColumns '[a, b]'.");
    Assert.assertEquals(validationErrors.get(1),
        "Property 'bootstrap.source' with value 'db_name$source_table' doesn't pass the validation check: the bootstrap source should have the pattern [chenguo:///]<db_name>.<table_name>");
    Assert.assertEquals(validationErrors.get(2),
        "Property 'derivedColumns[0].type' with value 'bad_type' doesn't pass the validation check: Your schema output type is not a valid Hive type. Supported types are: TINYINT,SMALLINT,INT,BIGINT,FLOAT,DOUBLE,STRING,BOOLEAN,BINARY,TIMESTAMP,DATE");
    Assert.assertEquals(validationErrors.get(3),
        "Property 'kafka.bootstrapServers' with value 'bootstrap-servers.stg.chenguo.com:16637$bootstrap-servers.stg.chenguo.com:166327' doesn't pass the validation check: bootstrap_servers should be a string of a list of <host>:<port> separated by commas");
    Assert.assertEquals(validationErrors.get(4),
        "Property 'outputHdfs' with value 'the HDFS location for the output_table' doesn't pass the validation check: a valid output hdfs path should be like /*/*...");
    Assert.assertEquals(validationErrors.get(5),
        "Property 'outputTable' with value 'dali://db_name.output_table' doesn't pass the validation check: the output table should have the pattern [chenguo:///]<db_name>.<table_name>");
    Assert.assertEquals(validationErrors.get(6),
        "Property 'schemaOverrides[0].udf' with value 'class-path' doesn't pass the validation check: provide a full class path for the UDF");
  }

  @Rule
  public ExpectedException expectedEx1 = ExpectedException.none();

  @Test
  public void testBadUpdates() throws JsonProcessingException {
    expectedEx1.expect(RuntimeException.class);
    expectedEx1.expectMessage("Unknown update key path '$.array[0].pos'. Top 3 recommendations:\nkafka.groupId\nkafka\nkafka.clientId");
    String jsonString =
        "{\"k1\":\"v1\", \"array1\": [{\"pos\":\"0\", \"val\":\"v0\"},{\"pos\":1, \"val\":\"v1\"}], \"k2\":\"v2\"}";

    HashMap<String, String> updates = new HashMap<>();
    updates.put("$.k1", "new k1");
    updates.put("$.array[0].pos", "100");

    String updated = new ConfigurationResolver<>(SampleConfig.class, jsonString, updates).updateJson(jsonString);
    Assert.assertEquals(updated,
        "{\"k1\":\"new k1\",\"array1\":[{\"pos\":\"100\",\"val\":\"v0\"},{\"pos\":1,\"val\":\"updated v1\"}],\"k2\":\"v2\"}");
  }

  @Test
  public void testRecommendationsForBadConfig() throws FileNotFoundException {
    expectedEx1.expect(RuntimeException.class);
    expectedEx1.expectMessage("Unknown config key path 'schemaOverrides[0].derived1ColumnName'. Top 3 recommendations:\nschemaOverrides[?].derivedColumnName\nschemaOverrides[?].columnName\nderivedColumns[?].derivedColumnName");
    File configFile = new File(getClass().getResource("/configs/bad/bad-config-test-recommendations.hocon").getFile());
    ConfigurationResolver<SampleConfig>
        parser = new ConfigurationResolver<SampleConfig>(SampleConfig.class, configFile);
    SampleConfig sampleConfig = parser.resolve();
  }
}
