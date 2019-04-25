package com.hellokoding.auth.web;

import com.hellokoding.auth.model.Post;
import com.hellokoding.auth.model.Rating;
import com.hellokoding.auth.model.User;
import com.hellokoding.auth.service.PostService;
import com.hellokoding.auth.service.RatingService;
import com.hellokoding.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RatingController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private RatingService ratingService;

    @GetMapping("/ratePost/{id}")
    public String ratePost(@PathVariable("id") Long id, Model model) {
        User user = userService.findCurrentUser();
        Post post = postService.findById(id);

        if (post == null) {
            return "no-post-err";
        }


        model.addAttribute("post", post);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("ratingForm", new Rating());
        return "rating";
    }




    @PostMapping("/ratePost/{id}")
    public String postRatePostExecutor(@PathVariable("id") Long id, @ModelAttribute("ratingForm") Rating ratingForm, BindingResult bindingResult) {

        User user = userService.findCurrentUser();
        Post post = postService.findById(id);

        if (post == null) {
            return "no-post-err";
        }

        ratingForm.setPost(post);
        ratingForm.setAuthor(user.getUsername());

        System.out.println("=====================");
        System.out.println(ratingForm.toString());
        System.out.println(ratingForm.getAuthor());
        System.out.println(ratingForm.getRatingNum());
        System.out.println(ratingForm.getComment());
        System.out.println(ratingForm.getId());
        System.out.println("=====================");
        System.out.println(ratingForm.getPost().toString());
        System.out.println(ratingForm.getPost().getRatingList().toString());
        System.out.println("=====================");

        ratingForm.getPost().rate(ratingForm);
        System.out.println(post.getRatingList().toString());
        System.out.println("=====================");

        ratingService.save(ratingForm);
        return "redirect:/posts";
    }



}
