package com.guochen.config.core;

import java.lang.annotation.*;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigKey {

  /**
   * Special value to indicate that all handlers should use the default
   * name, e.g. field or method name for the property.
   */
  public final static String USE_DEFAULT_NAME = "CONFIG_KEY_USE_DEFAULT_NAME";

  /**
   * default name comes from method or field name
   */
  String configName() default USE_DEFAULT_NAME;
}