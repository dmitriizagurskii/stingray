package com.hellokoding.auth.web;

import com.hellokoding.auth.model.Post;
import com.hellokoding.auth.model.User;
import com.hellokoding.auth.repository.PostRepository;
import com.hellokoding.auth.service.UserService;
import com.hellokoding.auth.validator.PostValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static java.lang.Math.abs;

@Controller
public class PostController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostValidator postValidator;

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/createpost")
    public String showPostCreateForm(Post post, Model model) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (userService.findByUsername(username) == null) {
            return "no-user-err";
        }

        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("post", post);
        return "create-post";
    }

    @PostMapping("/createpost")
    public String createPost(@Valid Post post, BindingResult result, Model model) {
        postValidator.validate(post, result);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (result.hasErrors()) {
            return "create-post";
        }

        if (userService.findByUsername(username) == null) {
            return "no-user-err";
        }

        User user = userService.findByUsername(username);
        String errMessage;
        int diff = user.getBalance() - post.getPrice();
        if (diff < 0) {
            errMessage = "the price is too high, you need to top up your balance on " + abs(diff);
            model.addAttribute("errmsg", errMessage);
            return "create-post";
        }

        user.createPost(post);
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

        Post post = postRepository.findById(id).get();

        if (post.isConfirmed()) {
            return "redirect:/viewconfirmedpost/{id}";
        }

        model.addAttribute("username", SecurityContextHolder.getContext().getAuthentication().getName());
        model.addAttribute("post", post);

        return "view-post";
    }


    @PostMapping(value = "/viewpost/{id}", params = "delete")
    public String deletePost(@PathVariable("id") Long id) {
        if (!postRepository.findById(id).isPresent()) {
            return "no-post-err";
        }

        postRepository.deleteById(id);
        return "redirect:/";
    }

    @PostMapping(value = "/viewpost/{id}", params = "acceptpost")
    public String acceptPost(@PathVariable("id") Long id, @RequestParam String acceptpost) {

        if (!postRepository.findById(id).isPresent()) {
            return "no-post-err";
        }

        if (userService.findByUsername(acceptpost) == null) {
            return "no-user-err";
        }

        Post post = postRepository.findById(id).get();
        User user = userService.findByUsername(acceptpost);

        user.addPostToCandidates(post);

        postRepository.save(post);

        return "redirect:/viewpost/{id}";
    }

    @PostMapping(value = "/viewpost/{id}", params = "rejectpost")
    public String rejectPost(@PathVariable("id") Long id, @RequestParam  String rejectpost) {
        if (!postRepository.findById(id).isPresent()) {
            return "no-post-err";
        }

        if (userService.findByUsername(rejectpost) == null) {
            return "no-user-err";
        }

        Post post = postRepository.findById(id).get();
        User user = userService.findByUsername(rejectpost);

        if (!user.getCandidatePosts().contains(post)) {
            return "error";
        }

        user.removePostFromCandidates(post);
        post.removeCandidate(user);
        postRepository.save(post);

        return "redirect:/viewpost/{id}";
    }

    @GetMapping("/changepost/{id}")
    public String showPostChangeForm(@PathVariable("id") Long id, Model model) {

        if (!postRepository.findById(id).isPresent()) {
            return "no-post-err";
        }

        Post post = postRepository.findById(id).get();


        if (SecurityContextHolder.getContext().getAuthentication().getName() != post.getOwner().getUsername()){
            return "permission-denied";
        }

        if (post.isConfirmed()) {
            return "redirect:/viewconfirmedpost/{id}";
        }

        model.addAttribute("post", post);
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

    @GetMapping("/candidates/{id}")
    public String showCandidates(@PathVariable("id") Long id, Model model) {

        if (!postRepository.findById(id).isPresent()) {
            return "no-post-err";
        }

        Post post = postRepository.findById(id).get();
        if (SecurityContextHolder.getContext().getAuthentication().getName() != post.getOwner().getUsername()){
            return "permission-denied";
        }
        if (post.isConfirmed()) {
            return "redirect:/viewconfirmedpost/{id}";
        }

        model.addAttribute("post", post);

        return "candidates";
    }

    @PostMapping("/candidates/{id}/{username}")
    public String chooseCandidate(@PathVariable("id") Long id, @PathVariable String username) {

        if (!postRepository.findById(id).isPresent()) {
            return "no-post-err";
        }

        if (userService.findByUsername(username) == null) {
            return "no-user-err";
        }

        Post post = postRepository.findById(id).get();
        User user = userService.findByUsername(username);

        user.confirmPost(post);
        postRepository.save(post);

        return "redirect:/viewpost/{id}";
    }

    @GetMapping("/viewconfirmedpost/{id}")
    public String viewAcceptedPost(@PathVariable("id") Long id, Model model) {
        if (!postRepository.findById(id).isPresent()) {
            return "no-post-err";
        }

        Post post = postRepository.findById(id).get();

        if (!post.isConfirmed()) {
            return "redirect:/viewpost/{id}";
        }

        model.addAttribute("post", post);

        return "viewconfirmedpost";
    }
}