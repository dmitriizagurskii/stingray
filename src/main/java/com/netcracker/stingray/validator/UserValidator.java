package com.netcracker.stingray.validator;

import com.netcracker.stingray.model.User;
import com.netcracker.stingray.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
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

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");
        if (user.getUsername().length() < 6) {
            errors.rejectValue("username", "Short.userForm.username");
        }
        if (user.getUsername().length() > 32) {
            errors.rejectValue("username", "Long.userForm.username");
        }
        if (userService.findByUsername(user.getUsername()) != null) {
            errors.rejectValue("username", "Duplicate.userForm.username");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
        if (!user.getPassword().matches("^(?=.*[0-9])(?=.*([a-z]|[A-Z]))(?=.*[@#$%^&+=]).*$")) {
            errors.rejectValue("password", "Strength.userForm.password");
        }
        if (user.getPassword().length() < 8) {
            errors.rejectValue("password", "Short.userForm.password");
        }

        if (!user.getPasswordConfirm().equals(user.getPassword())) {
            errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
        }
    }
}
