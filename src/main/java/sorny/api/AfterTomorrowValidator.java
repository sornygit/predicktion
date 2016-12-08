package sorny.api;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * javax.validation Validator for ensuring a date is after tomorrow.
 */
public class AfterTomorrowValidator implements ConstraintValidator<AfterTomorrow, String> {
    @Override
    public void initialize(AfterTomorrow afterTomorrow) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isEmpty(s))
            return true;
        LocalDate date;
        try {
            date = LocalDate.parse(s, DateTimeFormatter.BASIC_ISO_DATE);
        } catch (Exception e) {
            return false;
        }
        return LocalDate.now().plusDays(1).isBefore(date);
    }
}