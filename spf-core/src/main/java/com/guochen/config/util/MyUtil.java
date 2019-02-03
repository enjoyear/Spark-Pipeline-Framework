package com.guochen.config.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class MyUtil {
  private MyUtil() {

  }

  /**
   * @return a String concatenated by separator
   */
  public static String concatString(List<String> inputs, String separator) {
    StringBuilder concatenated = new StringBuilder();
    for (String input : inputs) {
      concatenated.append(input).append(separator);
    }
    concatenated.setLength(concatenated.length() - 1);
    return concatenated.toString();
  }

  /**
   * convert an InputStream to String
   */
  public static String streamToString(InputStream inputFileStream) {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputFileStream))) {
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
        sb.append('\n');
      }
      return sb.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * validate that distributeColumns must be part of sortColumns
   */
  public static void validateDistributedColumns(ArrayList<String> errors, String[] sortColumns, String[] distributeColumns) {
    Set<String> set = new HashSet<>(Arrays.asList(sortColumns));
    StringBuilder sb = new StringBuilder();
    for (String distributeColumn : distributeColumns) {
      if (!set.contains(distributeColumn)) {
        sb.append(String.format("Distributed column '%s' must be included in the sortColumns '%s'. ", distributeColumn,
            Arrays.toString(sortColumns)));
      }
    }
    String s = sb.toString().trim();
    if (!s.isEmpty()) {
      errors.add(s);
    }
  }
}
