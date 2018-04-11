package eu.enhan.validation.java.jsr303;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 *
 */
public class ThresholdConstraintValidator implements ConstraintValidator<ThresholdConstraint, BusinessConfig> {

    @Override
    public boolean isValid(BusinessConfig value, ConstraintValidatorContext context) {
        if (value == null)
            return false;
        boolean valid =  value.thresholdA < value.thresholdB && value.thresholdB < value.thresholdC;
        if (!valid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("thresholdB should verify thresholdA < thresholdB < thresholdC")
                    .addPropertyNode("thresholdB")
                    .addConstraintViolation();
        }
        return valid;
    }
}
