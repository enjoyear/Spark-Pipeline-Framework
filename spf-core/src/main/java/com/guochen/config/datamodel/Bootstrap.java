package com.guochen.config.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Pattern;
import lombok.Getter;


@Getter
public class Bootstrap {
  /**
   * the source table to be converted to the bucketed-and-sorted output table
   */
  @JsonProperty(required = true)
  @Pattern(regexp = "^([a-z]+\\:\\/\\/\\/)?([a-z_0-9])*\\.([a-z_0-9])*$", //break line to suppress style check warning
      message = "the bootstrap source should have the pattern [chenguo:///]<db_name>.<table_name>")
  private String source;

  /**
   * default to false
   * If false, the code would detect the output_table to decide whether to run bootstrap.
   * Set to true to enforce a bootstrap run. This value can only be set to true through Azkaban UI.
   */
  @JsonProperty
  private boolean force = false;
}
