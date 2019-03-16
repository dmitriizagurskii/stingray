package com.hellokoding.auth.web;

import com.hellokoding.auth.model.Post;
import com.hellokoding.auth.model.User;
import com.hellokoding.auth.repository.PostRepository;
import com.hellokoding.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class PostController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/createPost/{username}")
    public String showPostCreateForm(@PathVariable("username") String username, Post post, Model model) {
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("post", post);
        return "create-post";
    }

    @PostMapping("/createPost/{username}")
    public String createPost(@PathVariable("username") String username, @Valid Post post, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "create-post";
        }
        if (userService.findByUsername(username) != null) {
            User user = userService.findByUsername(username);
            user.addPost(post);
        }
        else
            return "post-adding-err";
        postRepository.save(post);
        model.addAttribute("posts", postRepository.findAll());
        return "posts";

    }

    @GetMapping("/")
    public String showMainPage(Model model){
        model.addAttribute("posts", postRepository.findAll());
        return "posts";
    }

}
