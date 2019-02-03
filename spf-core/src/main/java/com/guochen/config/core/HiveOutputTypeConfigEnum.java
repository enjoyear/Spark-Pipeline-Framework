package com.guochen.config.core;

/**
 * The config value enum for the Hive output column types
 * https://cwiki.apache.org/confluence/display/Hive/LanguageManual+Types
 * These types will be casted to Spark SQL types
 * https://spark.apache.org/docs/latest/api/java/org/apache/spark/sql/types/package-summary.html
 *   <li>SMALLINT : ShortType</li>
 *   <li>INT      : IntegerType</li>
 *   <li>TINYINT  : ByteType</li>
 *   <li>BIGINT   : LongType</li>
 */
public enum HiveOutputTypeConfigEnum {
  TINYINT, SMALLINT, INT, BIGINT,   //Integers
  FLOAT, DOUBLE,                    //Decimals
  STRING,                           //Strings
  BOOLEAN, BINARY,                  //Miscs
  TIMESTAMP, DATE;                  //Time, Date
}
