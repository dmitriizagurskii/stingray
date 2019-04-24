package com.hellokoding.auth.validator;

import com.hellokoding.auth.model.Post;
import com.hellokoding.auth.model.User;
import com.hellokoding.auth.service.DateService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

@Component
public class PostValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Post.class.equals(clazz);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Post post = (Post) o;

//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subject", "NotEmpty");
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "NotEmpty");
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "text", "NotEmpty");
//        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "price", "NotEmpty");

        if (post.getDescription().length() > 100) {
            errors.rejectValue("description", "Long.post.description");
        }

        try {
            Date deadline = DateService.convertToCalendar(post.getDate()).getTime();
            Date now = Calendar.getInstance().getTime();
            if (deadline.before(now)) {
                errors.rejectValue("date", "Invalid.post.deadline");
            }
            if ((deadline.getTime()-now.getTime())/1000/60/60/24 > 180) {
                errors.rejectValue("date", "Far.post.deadline");
            }
        } catch (Exception e) {
            errors.rejectValue("date", "Invalid.post.deadline");
        }

//        if (post.getText().length() < 100) {
//            errors.rejectValue("description", "Short.post.text");
//        }
    }

    public void validateCandidate(Post post, User candidate, Errors errors){
        if(!post.getCandidates().contains(candidate))
            errors.rejectValue("candidates", "No.post.candidate");
    }
}