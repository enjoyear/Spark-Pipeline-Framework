package com.guochen.config.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;


@Documented
@Constraint(validatedBy = EnumValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@ReportAsSingleViolation
public @interface EnumValid {

  /**
   * Provide the underlying enum class for validation
   */
  Class<? extends Enum<?>> enumClass();

  /**
   * Determine whether null values are acceptable
   * True if you want NULL to pass the validation
   * False otherwise
   */
  boolean nullable() default false;

  /**
   * A default error message. But it is overridden in a dynamic way by {@link EnumValidator}
   */
  String message() default "Input not valid based the enum value list";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}