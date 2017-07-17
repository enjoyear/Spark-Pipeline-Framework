#Spark Pipeline Framework

##Spark Cluster
###Configurations

* Cluster Configuration

| Key | Type | Default | Description|
|---|---|---|---|---|
| master | string | Mandatory | Specify the master URL |
| app_name | string | Mandatory | Provide the spark application name |
| enable\_hive\_support | boolean | false | Specify whether to load Hive classes for Hive support |

* Spark Configuration
Put ONLY spark related configurations here in this section.

##Spark Ingestion

###FileIngestionJob
####Configurations

* Source

| Key | Type | Default | Description|
|---|---|---|---|---|
| location | string | Mandatory | Provide the input location |

* Converter
* Checker
* Publisher

| Key | Type | Default | Description|
|---|---|---|---|---|
| format | string | Mandatory | Specify the output data format.<ul><li>avro : com.databricks.spark.avro</li><li>orc : orc</li></ul>|
| mode| enum<ul><li>`overwrite`</li><li>`append`</li><li>`ignore`</li><li>`error`</li></ul>| `error` | Specifies the behavior when data or table already exists<ul><li>`overwrite`: overwrite the existing data.</li><li>`append`: append the data.</li><li>`ignore`: ignore the operation (i.e. no-op).</li><li>`error`: default option, throw an exception at runtime.</li></ul>|
| location | string | Mandatory | Provide the output location |
