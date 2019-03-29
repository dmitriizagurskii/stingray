package com.hellokoding.auth.validator;

import com.hellokoding.auth.model.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class PaymentValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        User user = (User) obj;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors,"sumBuff", "Not empty");
        if (!user.getSumBuff().matches("^([1-9])[0-9]*")) {
            errors.rejectValue("sumBuff", "Strength.userForm.sumBuff", "Invalid format.");
        }

    }

    public void withdrawValid(Object obj, Errors errors, User currentUser) {
        User user = (User) obj;
        validate(obj, errors);
        if (!errors.hasErrors()) {
            if (currentUser.getBalance() < Integer.valueOf(user.getSumBuff())) {
                errors.rejectValue("sumBuff", "", "Not enough funds.");
            }

        }
    }
}

