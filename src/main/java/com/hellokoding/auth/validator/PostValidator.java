package com.hellokoding.auth.validator;

import com.hellokoding.auth.model.Post;
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
        // TODO: 3/24/19 How to get current user (future owner of the post) without userService or SecurityContextHolder.
//        Here you need to check if user has enough money to create the post, but the post doesn't have an owner yet.
//        if (post.getPrice()>post.getOwner().getBalance()) {
//            errors.rejectValue("price", "High.post.price");
//        }

        /*if (post.getText().length() < 100) {
            errors.rejectValue("description", "Short.post.text");
        }*/
    }

}