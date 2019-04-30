package com.hellokoding.auth.validator;

import com.hellokoding.auth.model.SuggestedPrice;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class SuggestedPriceValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return SuggestedPrice.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SuggestedPrice suggestedPrice = (SuggestedPrice) target;
        if(suggestedPrice.getValue() > suggestedPrice.getCandidateTask().getPrice()){
            errors.rejectValue("value", "Big.suggestedPrice.value");
        }
        if(suggestedPrice.getValue() < 0) {
            errors.rejectValue("value", "Negative.suggestedPrice.value");
        }
    }
}
