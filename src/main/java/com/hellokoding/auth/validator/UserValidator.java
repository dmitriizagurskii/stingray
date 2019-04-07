package com.hellokoding.auth.validator;

import com.hellokoding.auth.model.User;
import com.hellokoding.auth.service.UserService;
//import com.sun.xml.internal.bind.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
//import org.springframework.validation.ValidationUtils;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    @Autowired
    private UserService userService;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
//        if (user.getUsername().length() < 6) {
//            errors.rejectValue("username", "Short.userForm.username");
//        }
//        if (user.getUsername().length() > 32) {
//            errors.rejectValue("username", "Long.userForm.username");
//        }
        if (userService.findByUsername(user.getUsername()) != null) {
            errors.rejectValue("username", "Duplicate.userForm.username");
        }

//        if (!user.getPassword().matches("^(?=.*[0-9])(?=.*([a-z]|[A-Z]))(?=.*[@#$%^&+=!~`()\\[\\]{}*]).*$")) {
//            errors.rejectValue("password", "Strength.userForm.password");
//        }
//        if (user.getPassword().length() < 8) {
//            errors.rejectValue("password", "Short.userForm.password");
//        }
//
        if (!user.getPasswordConfirm().equals(user.getPassword())) {
            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
        }
    }

    public void validateBalance(User user, Integer sum, Errors errors) {
        if (sum != null)
            if (user.getBalance() < sum) {
                errors.rejectValue("price", "Low.user.balance");
            }
    }
}
