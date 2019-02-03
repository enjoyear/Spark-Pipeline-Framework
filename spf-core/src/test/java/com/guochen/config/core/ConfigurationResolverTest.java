package com.guochen.config.core;

import org.junit.Assert;
import org.junit.Test;


public class ConfigurationResolverTest {
  @Test
  public void testMinimumChanges() {
    Assert.assertEquals(ConfigurationResolver.minimumChanges("", "ab"), 2);
    Assert.assertEquals(ConfigurationResolver.minimumChanges("ab", ""), 2);
    Assert.assertEquals(ConfigurationResolver.minimumChanges("a", "ab"), 1);
    Assert.assertEquals(ConfigurationResolver.minimumChanges("ab", "a"), 1);
    Assert.assertEquals(ConfigurationResolver.minimumChanges("acd", "ab"), 2);
    Assert.assertEquals(ConfigurationResolver.minimumChanges("ab", "acd"), 2);
    Assert.assertEquals(ConfigurationResolver.minimumChanges("abcdef", "acde"), 2);
    Assert.assertEquals(ConfigurationResolver.minimumChanges("abcdef", "acdeg"), 2);
    Assert.assertEquals(ConfigurationResolver.minimumChanges("abcdef", "acdegg"), 3);
    Assert.assertEquals(ConfigurationResolver.minimumChanges("zabcdef", "acdegg"), 4);
    Assert.assertEquals(ConfigurationResolver.minimumChanges("zabcdef", "abcefg"), 3);
  }
}
