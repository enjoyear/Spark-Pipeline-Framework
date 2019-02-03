package com.guochen.config.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guochen.config.datamodel.SampleConfig;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ConfigPathFetcherTest {
  @Test
  public void testGetConfigKeyPaths() {
    List<String> keys = ConfigPathFetcher.getConfigKeyPaths(SampleConfig.class);

    List<String> expected = new ArrayList<>();
    expected.add("description");
    expected.add("debugMode");
    expected.add("sortColumns");
    expected.add("deltaColumns");
    expected.add("distributeColumns");
    expected.add("excludedColumns");
    expected.add("schemaOverrides");
    expected.add("schemaOverrides[?].columnName");
    expected.add("schemaOverrides[?].derivedColumnName");
    expected.add("schemaOverrides[?].udf");
    expected.add("schemaOverrides[?].udfArgs");
    expected.add("schemaOverrides[?].type");
    expected.add("schemaOverrides[?].transformExpression");
    expected.add("schemaOverrides[?].comment");
    expected.add("schemaOverrides[?].collectStats");
    expected.add("derivedColumns");
    expected.add("derivedColumns[?].columnName");
    expected.add("derivedColumns[?].derivedColumnName");
    expected.add("derivedColumns[?].udf");
    expected.add("derivedColumns[?].udfArgs");
    expected.add("derivedColumns[?].type");
    expected.add("derivedColumns[?].transformExpression");
    expected.add("derivedColumns[?].comment");
    expected.add("derivedColumns[?].collectStats");
    expected.add("numOfBuckets");
    expected.add("bootstrap");
    expected.add("bootstrap.source");
    expected.add("bootstrap.force");
    expected.add("outputTable");
    expected.add("outputHdfs");
    expected.add("kafka");
    expected.add("kafka.topic");
    expected.add("kafka.groupId");
    expected.add("kafka.bootstrapServers");
    expected.add("kafka.schemaRegistryUrl");
    expected.add("kafka.clientId");
    expected.add("kafka.startFromBeginning");
    expected.add("kafka.startOffset");

    Assert.assertEquals(keys, expected);
  }

  static class A {
    int i;

    @JsonProperty
    String string;

    long[] longArray;
    B notIncluded = null;

    @JsonProperty
    B b;

    C c;

    @JsonProperty
    C[] cArray;

    static class B {
      @JsonProperty
      int bi;

      String str;

      @JsonProperty
      BB bb;

      static class BB {
        @JsonProperty
        double d;

        @JsonProperty
        BBB bbb;

        static class BBB {
          float f;
          @JsonProperty
          C.C2 c2;
        }
      }
    }

    static class C {
      int ci;
      @JsonProperty
      String cStr;
      @JsonProperty
      C2 c2;
      @JsonProperty
      C2[] c2Array;

      static class C2 {
        @JsonProperty
        float bi;
        @JsonProperty
        Date date;
        String c2str;
      }
    }
  }

  @Test
  public void testGetConfigKeyPaths2() {
    List<String> keys = ConfigPathFetcher.getConfigKeyPaths(A.class);

    List<String> expected = new ArrayList<>();
    expected.add("string");
    expected.add("b");
    expected.add("b.bi");
    expected.add("b.bb");
    expected.add("b.bb.d");
    expected.add("b.bb.bbb");
    expected.add("b.bb.bbb.c2");
    expected.add("b.bb.bbb.c2.bi");
    expected.add("b.bb.bbb.c2.date");
    expected.add("cArray");
    expected.add("cArray[?].cStr");
    expected.add("cArray[?].c2");
    expected.add("cArray[?].c2.bi");
    expected.add("cArray[?].c2.date");
    expected.add("cArray[?].c2Array");
    expected.add("cArray[?].c2Array[?].bi");
    expected.add("cArray[?].c2Array[?].date");

    Assert.assertEquals(keys, expected);
  }
}