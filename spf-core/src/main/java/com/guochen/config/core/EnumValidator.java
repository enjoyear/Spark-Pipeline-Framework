package com.guochen.config.core;

import com.guochen.config.util.MyUtil;
import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class EnumValidator implements ConstraintValidator<EnumValid, String> {
  private List<String> enumValues;
  private String allEnumsString;
  private boolean allowNull;

  @Override
  public void initialize(EnumValid constraintAnnotation) {
    allowNull = constraintAnnotation.nullable();
    enumValues = new ArrayList<>();
    Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();
    for (Enum<?> enumVal : enumClass.getEnumConstants()) {
      enumValues.add(enumVal.toString().toUpperCase());
    }
    allEnumsString = MyUtil.concatString(enumValues, ",");
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      if (allowNull) {
        return true;
      }
    } else if (enumValues.contains(value.toUpperCase())) {
      return true;
    }

    context.disableDefaultConstraintViolation();
    String errorMessage =
        String.format("Your schema output type is not a valid Hive type. Supported types are: %s", allEnumsString);
    context.buildConstraintViolationWithTemplate(errorMessage).addConstraintViolation();
    return false;
  }
}
