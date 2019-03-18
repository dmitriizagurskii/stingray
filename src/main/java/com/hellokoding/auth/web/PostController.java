package com.hellokoding.auth.web;

import com.hellokoding.auth.model.Post;
import com.hellokoding.auth.model.User;
import com.hellokoding.auth.repository.PostRepository;
import com.hellokoding.auth.service.UserService;
import com.hellokoding.auth.validator.PostValidator;
import jdk.nashorn.internal.runtime.logging.Logger;
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
    private PostValidator postValidator;

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/createPost/{username}")
    public String showPostCreateForm(@PathVariable("username") String username, Post post, Model model) {

        if (userService.findByUsername(username) == null) {
            return "no-user-err";
        }

        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("post", post);
        return "create-post";
    }

    @PostMapping("/createPost/{username}")
    public String createPost(@PathVariable("username") String username, @Valid Post post, BindingResult result, Model model) {
        postValidator.validate(post, result);

        if (result.hasErrors()) {
            return "create-post";
        }

        if (userService.findByUsername(username) == null) {
            return "no-user-err";
        }

        User user = userService.findByUsername(username);
        user.addPost(post);

        postRepository.save(post);
        return "redirect:/posts";
    }

    @GetMapping("/posts")
    public String showMainPage(Model model) {
        model.addAttribute("posts", postRepository.findAll());
        return "posts";
    }

    @GetMapping("/viewpost/{id}")
    public String showPost(@PathVariable("id") Long id, Model model) {
        if (!postRepository.findById(id).isPresent()) {
            return "no-post-err";
        }
        model.addAttribute("post", postRepository.findById(id).get());
        return "view-post";
    }


    @GetMapping("/deletepost/{id}")
    public String deletePost(@PathVariable("id") Long id) {
        if (!postRepository.findById(id).isPresent()) {
            return "no-post-err";
        }

        postRepository.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/changepost/{id}")
    public String showPostChangeForm(@PathVariable("id") Long id, Model model) {

        if (!postRepository.findById(id).isPresent()) {
            return "no-post-err";
        }
        model.addAttribute("post", postRepository.findById(id).get());
        return "change-post";
    }

    @PostMapping("/changepost/{id}")
    public String changePost(@PathVariable("id") Long id, @Valid Post post, BindingResult result) {

        postValidator.validate(post, result);
        if (result.hasErrors()) {
            return "change-post";
        }

        if (!postRepository.findById(id).isPresent()) {
            return "no-post-err";
        }

        Post originalPost = postRepository.findById(id).get();
        originalPost.changeAllAttributes(post);
        postRepository.save(originalPost);

        return "redirect:/posts?success";
    }

    @GetMapping("/acceptpost/{id}/{username}")
    public String acceptPost(@PathVariable("id") Long id, @PathVariable("username") String username) {

        if (!postRepository.findById(id).isPresent()) {
            return "no-post-err";
        }

        if (userService.findByUsername(username) == null) {
            return "no-user-err";
        }

        Post post = postRepository.findById(id).get();
        User user = userService.findByUsername(username);
        user.addPostToCandidates(post);
        postRepository.save(post);
        userService.save(user);

        return "redirect:/viewpost/{id}";
    }

    @GetMapping("/rejectpost/{id}/{username}")
    public String rejectPost(@PathVariable("id") Long id, @PathVariable("username") String username) {

        if (!postRepository.findById(id).isPresent()) {
            return "no-post-err";
        }

        if (userService.findByUsername(username) == null) {
            return "no-user-err";
        }

        Post post = postRepository.findById(id).get();
        User user = userService.findByUsername(username);
        if (!user.getCandidatePosts().contains(post)) {
            return "error";
        }

        user.deletePostFromCandidates(post);
        postRepository.save(post);
        userService.save(user);

        return "redirect:/viewpost/{id}";
    }
}