package com.guochen.config.datamodel;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;


/**
 * Empty place holder interface
 */
public interface JobConfig {
  default List<String> getValidationErrors() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    Validator validator = factory.getValidator();
    Set<ConstraintViolation<JobConfig>> constraintViolations = validator.validate(this);
    return constraintViolations.stream()
        .map(vio -> String.format("Property '%s' with value '%s' doesn't pass the validation check: %s",
            vio.getPropertyPath(), vio.getInvalidValue(), vio.getMessage()))
        .collect(Collectors.toList());
  }
}
