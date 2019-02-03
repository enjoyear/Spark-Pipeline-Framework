package com.guochen.config.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


/**
 * A utility class that fetches the configuration paths for your json object
 */
public class ConfigPathFetcher {
  private ConfigPathFetcher() {

  }

  /**
   * Derive the configurations keys based on JsonProperty annotations
   * @return a list of configuration key paths
   */
  public static List<String> getConfigKeyPaths(Class<?> type) {
    List<String> supportedConfigurations = new ArrayList<>();
    Field[] fields = type.getDeclaredFields();

    for (Field field : fields) {
      JsonProperty annotation = field.getAnnotation(JsonProperty.class);
      if (annotation == null) {
        continue;
      }

      String fieldName = field.getName();
      if (!annotation.value().equals(JsonProperty.USE_DEFAULT_NAME)) {
        fieldName = annotation.value();
      }

      supportedConfigurations.add(fieldName);
      if (isBuiltInType(field)) {
        continue;
      }

      Class<?> customizedType;
      if (field.getType().isArray()) {
        fieldName += "[?].";
        customizedType = field.getType().getComponentType();
      } else {
        fieldName += ".";
        customizedType = field.getType();
      }

      List<String> nextLevelPaths = getConfigKeyPaths(customizedType);
      for (String subPath : nextLevelPaths) {
        supportedConfigurations.add(fieldName + subPath);
      }
    }
    return supportedConfigurations;
  }

  private static boolean isBuiltInType(Field field) {
    return field.getType().isPrimitive() || field.getType() == String.class;
  }
}
