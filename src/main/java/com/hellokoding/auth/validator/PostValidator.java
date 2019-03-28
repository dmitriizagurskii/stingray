package com.hellokoding.auth.validator;

import com.hellokoding.auth.model.Post;
import com.hellokoding.auth.model.User;
import com.hellokoding.auth.repository.PostRepository;
import com.hellokoding.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    public void postValidate(Object object, Errors errors, User owner) {
        Post post = (Post) object;
        validate(object, errors);
        if (post.getPrice()> owner.getBalance()) {
            errors.rejectValue("price", "High.post.price", "low balance");
        }

    }

}