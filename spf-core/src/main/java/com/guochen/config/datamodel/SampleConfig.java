package com.guochen.config.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guochen.config.util.MyUtil;
import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;


@Getter
public class SampleConfig implements JobConfig {
  /**
   * the description for your ingestion flow
   */
  @JsonProperty
  private String description;

  @JsonProperty
  private boolean debugMode;

  /**
   * the columns which output table will be sorted by, this is a superset of unique columns(PK), which should be positioned last
   */
  @JsonProperty(required = true)
  private String[] sortColumns;

  /**
   * the columns based on which table merger decides to keep as latest version
   */
  @JsonProperty(required = true)
  private String[] deltaColumns;

  /**
   * the columns which output table will be distributed by
   * distributeColumns must be part of sortColumns
   */
  @JsonProperty(required = true)
  private String[] distributeColumns;

  /**
   * the columns to be excluded from the source (The base table and the delta table)
   */
  @JsonProperty
  private String[] excludedColumns;

  /**
   * the updated schema for certain columns that overrides the default one
   */
  @Valid
  @JsonProperty
  private SchemaOverride[] schemaOverrides;

  /**
   * the extra derived columns on top of original schema
   */
  @Valid
  @JsonProperty
  private SchemaOverride[] derivedColumns;

  /**
   * the number of buckets for the bucketed-and-sorted output table
   */
  @Min(value = 0, message = "num_of_buckets must be a positive number")
  @JsonProperty(required = true)
  private int numOfBuckets;

  /**
   * the bootstrap configuration
   */
  @Valid
  @JsonProperty(required = true)
  private Bootstrap bootstrap;

  /**
   * the name of output table
   */
  @Pattern(regexp = "^([a-z]+:\\/\\/\\/)?([a-z_0-9])*\\.([a-z_0-9])*$", //break line to suppress style check warning
      message = "the output table should have the pattern [chenguo:///]<db_name>.<table_name>")
  @JsonProperty(required = true)
  private String outputTable;

  /**
   * the location of output table
   */
  @Pattern(regexp = "(hdfs:\\/\\/)?\\/[a-zA-Z0-9_-]+(\\/[a-zA-Z0-9_-]+)*", message = "a valid output hdfs path should be like /*/*...")
  @JsonProperty(required = true)
  private String outputHdfs;

  /**
   * the configuration for Kafka connection
   */
  @Valid
  @JsonProperty(required = true)
  private Kafka kafka;

  @Getter
  public static class Kafka {
    @JsonProperty(required = true)
    private String topic;

    /**
     * the Kafka consumer group id
     */
    @JsonProperty(required = true)
    private String groupId;

    /**
     * the Kafka bootstrap servers, separated by commas
     */
    @Pattern(regexp = "[0-9a-z.-]*?:[0-9]+(,[0-9a-z.-]*?:[0-9]+)*", //break line to suppress style check warning
        message = "bootstrap_servers should be a string of a list of <host>:<port> separated by commas")
    @JsonProperty(required = true)
    private String bootstrapServers;

    /**
     * the Kafka schema registry url
     */
    @JsonProperty(required = true)
    private String schemaRegistryUrl;

    /**
     * the Kafka schema registry url
     */
    @JsonProperty(required = true)
    private String clientId;

    /**
     * Consume the delta table topic from the beginning of the queue.
     * The number can only be set through the UI.
     */
    @JsonProperty
    private String startFromBeginning = "false";

    /**
     * Set the Kafka offset for the delta table topic.
     */
    @JsonProperty
    private long startOffset = 0;
  }

  @Override
  public List<String> getValidationErrors() {
    List<String> validationErrors = JobConfig.super.getValidationErrors();
    validationErrors.addAll(otherValidations());
    return validationErrors;
  }

  private List<String> otherValidations() {
    ArrayList<String> errors = new ArrayList<>();
    validateDistributedColumns(errors);
    validateUDFExistence(errors);
    return errors;
  }

  /**
   * TODO:
   * To be completed after the dataset-compaction-library provides a list of valid
   * 1. Spark built-in UDFs
   * 2. Customized UDFs
   */
  private void validateUDFExistence(ArrayList<String> errors) {

  }

  /**
   * validate that distributeColumns must be part of sortColumns
   */
  private void validateDistributedColumns(ArrayList<String> errors) {
    MyUtil.validateDistributedColumns(errors, sortColumns, distributeColumns);
  }
}
