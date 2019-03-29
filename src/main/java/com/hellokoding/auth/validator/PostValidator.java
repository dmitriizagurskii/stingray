package com.hellokoding.auth.validator;

import com.hellokoding.auth.model.Post;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class PostValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Post.class.equals(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Post post = (Post) o;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subject", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "text", "NotEmpty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "price", "NotEmpty");

        if (post.getDescription().length() > 100) {
            errors.rejectValue("description", "Long.post.description");
        }

//        if (post.getText().length() < 100) {
//            errors.rejectValue("description", "Short.post.text");
//        }
    }
}