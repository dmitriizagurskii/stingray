package com.hellokoding.auth.web;

import com.hellokoding.auth.model.Post;
import com.hellokoding.auth.model.SuggestedPrice;
import com.hellokoding.auth.model.User;
import com.hellokoding.auth.service.PostFileService;
import com.hellokoding.auth.service.PostService;
import com.hellokoding.auth.service.SuggestedPriceService;
import com.hellokoding.auth.service.UserService;
import com.hellokoding.auth.validator.PostValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/viewpost/{id}")
    public String showPost(@PathVariable("id") Long id, Model model) {

        Post post = postService.findById(id);
        if (post == null) {
            return "no-post-err";
        }

        if (post.isConfirmed()) {
            return "redirect:/viewconfirmedpost/{id}";
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return "no-user-err";
        }

        model.addAttribute("user", user);
        model.addAttribute("post", post);

        if (post.getOwner() == user){
            return "view-ownpost";
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

        if (userService.findByUsername(acceptpost) == null) {
            return "no-user-err";
        }

        User user = userService.findByUsername(acceptpost);

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

        if (userService.findByUsername(rejectpost) == null) {
            return "no-user-err";
        }

        User user = userService.findByUsername(rejectpost);

        if (!user.getCandidatePosts().contains(post)) {
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
    public String suggestPrice(@PathVariable("id") Long id, @RequestParam String suggestprice, @RequestParam Integer price) {

        Post post = postService.findById(id);
        if (post == null) {
            return "no-post-err";
        }

        if (userService.findByUsername(suggestprice) == null) {
            return "no-user-err";
        }

        User user = userService.findByUsername(suggestprice);

        SuggestedPrice suggestedPrice = suggestedPriceService.getSuggestedPrice(user, post);
        suggestedPrice.setValue(price);

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
            post.addPostFile(postFileService.save(postFileService.getPostFile(mf)));
        }

        postService.save(post);
        return "redirect:/viewpost/{id}";
    }
}
