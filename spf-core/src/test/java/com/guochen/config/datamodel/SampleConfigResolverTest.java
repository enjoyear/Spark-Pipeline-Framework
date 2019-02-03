package com.guochen.config.datamodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.guochen.config.core.ConfigurationResolver;
import com.guochen.config.core.HiveOutputTypeConfigEnum;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SampleConfigResolverTest {

  @Test
  public void testValidTestFile() throws FileNotFoundException {
    ConfigurationResolver<SampleConfig> parser =
        new ConfigurationResolver<SampleConfig>(SampleConfig.class,
            new File(getClass().getResource("/configs/example.hocon").getFile()));
    SampleConfig sampleConfig = parser.resolve();

    Assert.assertEquals(sampleConfig.getDescription(), "test ingestion flow");
    Assert.assertEquals(sampleConfig.isDebugMode(), true);
    Assert.assertEquals(sampleConfig.getSortColumns(), new String[]{"a", "b"});
    Assert.assertEquals(sampleConfig.getDeltaColumns(), new String[]{"last_modified"});
    Assert.assertEquals(sampleConfig.getDistributeColumns(), new String[]{"b"});

    Assert.assertEquals(sampleConfig.getSchemaOverrides().length, 1);
    Assert.assertEquals(sampleConfig.getSchemaOverrides()[0].getColumnName(), "source column name");
    Assert.assertEquals(sampleConfig.getSchemaOverrides()[0].getDerivedColumnName(), "derived column name");
    Assert.assertEquals(sampleConfig.getSchemaOverrides()[0].getType(), HiveOutputTypeConfigEnum.TIMESTAMP);
    Assert.assertEquals(sampleConfig.getSchemaOverrides()[0].getUdf(), "the.full.class.path");
    Map<String, Object> udfArgs = sampleConfig.getSchemaOverrides()[0].getUdfArgs();
    Assert.assertEquals(udfArgs.get("format"), "yyyy-MM-dd");
    Assert.assertEquals(udfArgs.get("timeZone"), "xxx");
    Assert.assertEquals(sampleConfig.getSchemaOverrides()[0].isCollectStats(), false);
    Assert.assertEquals(sampleConfig.getSchemaOverrides()[0].getComment(), "comment");
    Assert.assertEquals(sampleConfig.getDerivedColumns().length, 2);
    Assert.assertEquals(sampleConfig.getDerivedColumns()[0].getColumnName(), "source1");
    Assert.assertEquals(sampleConfig.getDerivedColumns()[0].getType(), HiveOutputTypeConfigEnum.INT);
    Assert.assertEquals(sampleConfig.getDerivedColumns()[1].getColumnName(), "source2");

    Assert.assertEquals(sampleConfig.getNumOfBuckets(), 256);
    Assert.assertEquals(sampleConfig.getBootstrap().getSource(), "chenguo:///db_name.source_table");
    Assert.assertEquals(sampleConfig.getBootstrap().isForce(), false);
    Assert.assertEquals(sampleConfig.getExcludedColumns(), new String[]{"excludeCol"});
    Assert.assertEquals(sampleConfig.getOutputTable(), "db_name.output_table");
    Assert.assertEquals(sampleConfig.getOutputHdfs(), "/the/HDFS/location/for/the/output_table");

    Assert.assertEquals(sampleConfig.getKafka().getTopic(), "topic1");
    Assert.assertEquals(sampleConfig.getKafka().getGroupId(), "Spark_kafka_test");
    Assert.assertEquals(sampleConfig.getKafka().getBootstrapServers(),
        "bootstrap-servers.stg.chenguo.com:16637");
    Assert.assertEquals(sampleConfig.getKafka().getSchemaRegistryUrl(),
        "http://schemaReg.chenguo.com:10252/schemaRegistry/schemas");
    Assert.assertEquals(sampleConfig.getKafka().getClientId(), "clientId");
    Assert.assertEquals(sampleConfig.getKafka().getStartOffset(), 0);

    List<String> validationErrors = sampleConfig.getValidationErrors();
    validationErrors.forEach(System.out::println);
    Assert.assertEquals(validationErrors.size(), 0);
  }

  @Test
  public void testValidConfigFile2WithUpdate() throws FileNotFoundException {
    ConfigurationResolver<SampleConfig> parser = new ConfigurationResolver<>(SampleConfig.class,
        new File(getClass().getResource("/configs/sample.hocon").getFile()));
    SampleConfig sampleConfig = parser.resolve();

    Assert.assertEquals(sampleConfig.isDebugMode(), false);
    Assert.assertEquals(sampleConfig.getSchemaOverrides()[1].isCollectStats(), true);
    Assert.assertEquals(sampleConfig.getBootstrap().isForce(), false);
    Assert.assertEquals(sampleConfig.getKafka().getStartOffset(), 0);

    List<String> validationErrors = sampleConfig.getValidationErrors();
    validationErrors.forEach(System.out::println);
    Assert.assertEquals(validationErrors.size(), 0);

    HashMap<String, String> updates = new HashMap<>();
    updates.put("schemaOverrides[1].collectStats", "false");
    updates.put("bootstrap.force", "true");
    updates.put("kafka.startOffset", "100");
    updates.put("numOfBuckets", "88");

    ConfigurationResolver<SampleConfig> parser2 = new ConfigurationResolver<>(SampleConfig.class,
        new File(getClass().getResource("/configs/sample.hocon").getFile()), updates);
    SampleConfig sampleConfig2 = parser2.resolve();

    Assert.assertEquals(sampleConfig2.getSchemaOverrides()[1].isCollectStats(), false);
    Assert.assertEquals(sampleConfig2.getBootstrap().isForce(), true);
    Assert.assertEquals(sampleConfig2.getKafka().getStartOffset(), 100);
    Assert.assertEquals(sampleConfig2.getNumOfBuckets(), 88);

    List<String> validationErrors2 = sampleConfig2.getValidationErrors();
    validationErrors2.forEach(System.out::println);
    Assert.assertEquals(validationErrors2.size(), 0);
  }

  @Test
  public void testJsonUpdates() throws JsonProcessingException {
    String jsonString =
        "{\"k1\":\"v1\", \"array1\": [{\"pos\":\"0\", \"val\":\"v0\"},{\"pos\":1, \"val\":\"v1\"}], \"k2\":\"v2\"}";

    HashMap<String, String> updates = new HashMap<>();
    updates.put("$.k1", "new k1");
    updates.put("array1[0].pos", "100");
    updates.put("newKey", "newValue");
    updates.put("$.array1[1].val", "updated v1");

    String updated =
        new ConfigurationResolver<>(SampleConfig.class, jsonString, updates).updateJson(jsonString);
    Assert.assertEquals(updated,
        "{\"k1\":\"new k1\",\"array1\":[{\"pos\":\"100\",\"val\":\"v0\"},{\"pos\":1,\"val\":\"updated v1\"}],\"k2\":\"v2\",\"newKey\":\"newValue\"}");
  }
}
