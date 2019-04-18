package com.hellokoding.auth.web;

import com.hellokoding.auth.model.Post;
import com.hellokoding.auth.model.PostState;
import com.hellokoding.auth.model.SuggestedPrice;
import com.hellokoding.auth.model.User;
import com.hellokoding.auth.service.PostFileService;
import com.hellokoding.auth.service.PostService;
import com.hellokoding.auth.service.SuggestedPriceService;
import com.hellokoding.auth.service.UserService;
import com.hellokoding.auth.validator.SuggestedPriceValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ViewPostController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private SuggestedPriceService suggestedPriceService;

    @Autowired
    private PostFileService postFileService;

    @Autowired
    private SuggestedPriceValidator suggestedPriceValidator;

    @GetMapping("/viewpost/{id}")
    public String showPost(@PathVariable("id") Long id, Model model) {

        Post post = postService.findById(id);
        if (post == null) {
            return "no-post-err";
        }

        if (post.getState().equals(PostState.ASSIGNED)) {
            return "redirect:/viewassignedpost/{id}";
        }

        User user = userService.findCurrentUser();
        if (user == null) {
            return "no-user-err";
        }

        model.addAttribute("user", user);
        model.addAttribute("post", post);

        if (post.getOwner() == user){
            return "view-own-post";
        }

        model.addAttribute("suggestedPrice", suggestedPriceService.getSuggestedPrice(user, post));
        return "view-post";
    }


    @PostMapping(value = "/viewpost/{id}", params = "acceptpost")
    public String acceptPost(@PathVariable("id") Long id, @RequestParam String acceptpost) {

        Post post = postService.findById(id);
        if (post == null) {
            return "no-post-err";
        }

        User user = userService.findByUsername(acceptpost);
        if (user == null) {
            return "no-user-err";
        }

        user.addPostToCandidates(post);

        postService.save(post);

        return "redirect:/viewpost/{id}";
    }

    @PostMapping(value = "/viewpost/{id}", params = "rejectpost")
    public String rejectPost(@PathVariable("id") Long id, @RequestParam String rejectpost) {

        Post post = postService.findById(id);

        if (post == null) {
            return "no-post-err";
        }

        User user = userService.findByUsername(rejectpost);
        if (user == null) {
            return "no-user-err";
        }

        if (!user.getAcceptedPosts().contains(post)) {
            return "error";
        }

        SuggestedPrice suggestedPrice = suggestedPriceService.getSuggestedPrice(user, post);
        suggestedPrice.setValue(post.getPrice());
        user.removePostFromCandidates(post);

        suggestedPriceService.save(suggestedPrice);
        postService.save(post);

        return "redirect:/viewpost/{id}";
    }

    @PostMapping(value = "/viewpost/{id}", params = "suggestprice")
    public String suggestPrice(@PathVariable("id") Long id, @RequestParam String suggestprice, @ModelAttribute SuggestedPrice price, BindingResult result) {

        Post post = postService.findById(id);
        if (post == null) {
            return "no-post-err";
        }

        User user = userService.findByUsername(suggestprice);
        if (user == null) {
            return "no-user-err";
        }

        SuggestedPrice suggestedPrice = suggestedPriceService.getSuggestedPrice(user, post);
        suggestedPrice.setValue(price.getValue());
        suggestedPriceValidator.validate(suggestedPrice, result);
        if (result.hasErrors()){
            return "redirect:/viewpost/{id}";
        }
        suggestedPriceService.save(suggestedPrice);

        return "redirect:/viewpost/{id}";
    }

    @PostMapping(value = "/viewpost/{id}", params = "addfiles")
    public String uploadFile(@PathVariable("id") Long id, @RequestParam("files") MultipartFile[] files) {

        Post post = postService.findById(id);
        if (post == null) {
            return "no-post-err";
        }

        for (MultipartFile mf: files) {
            post.addPostFile(postFileService.save(mf));
        }

        postService.save(post);
        return "redirect:/viewpost/{id}";
    }
}
