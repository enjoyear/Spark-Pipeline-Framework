package com.guochen.config.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guochen.config.core.EnumValid;
import com.guochen.config.core.HiveOutputTypeConfigEnum;
import java.util.Map;
import javax.validation.constraints.Pattern;
import lombok.Getter;


@Getter
public class SchemaOverride {

  /**
   * the source column name to override
   */
  @JsonProperty(required = true)
  private String columnName;

  /**
   * default to column_name if not provided
   */
  @JsonProperty
  private String derivedColumnName;

  /**
   * There are two cases for UDFs
   * 1. If it's a single word, then we need to further validate against build-in UDFs
   * 2. If it's a full class path, we need to validate that this class exists
   */
  @Pattern(regexp = "[a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)*", message = "provide a full class path for the UDF")
  @JsonProperty("udf")
  private String udf;

  /**
   * the args map for the constructor of the UDF
   */
  @JsonProperty
  private Map<String, Object> udfArgs;

  /**
   * this specifies the Hive output type
   */
  @EnumValid(enumClass = HiveOutputTypeConfigEnum.class, nullable = true)
  @JsonProperty
  private String type;

  public HiveOutputTypeConfigEnum getType() {
    if (type == null) {
      return null;
    }
    return HiveOutputTypeConfigEnum.valueOf(type.toUpperCase());
  }

  /**
   * set to transform the column inline, e.g. transformExpression: [field]/1000, regexp_extract([field], '(\d+):(\d+)', 2)
   */
  @JsonProperty
  private String transformExpression;

  @JsonProperty
  private String comment;

  /**
   * default to true for all sortColumns, delta_columns and distributeColumns; otherwise false
   */
  @JsonProperty
  private boolean collectStats;
}
