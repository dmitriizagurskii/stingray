package com.hellokoding.auth.web;

import com.hellokoding.auth.model.Post;
import com.hellokoding.auth.model.PostState;
import com.hellokoding.auth.model.SuggestedPrice;
import com.hellokoding.auth.model.User;
import com.hellokoding.auth.service.*;
import com.hellokoding.auth.validator.PostValidator;
import com.hellokoding.auth.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class PostController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private SuggestedPriceService suggestedPriceService;

    @Autowired
    private PostFileService postFileService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private PostValidator postValidator;


    @GetMapping("/createpost")
    public String showPostCreateForm(Post post, Model model) {

        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("post", post);
        return "create-post";
    }

    @PostMapping("/createpost")
    public String createPost(Model model, Post post, BindingResult result,
                             @RequestParam("files") MultipartFile[] files) {

        User user = userService.getCurrentUser();

        postValidator.validate(post, result);
        userValidator.validateBalance(user, post.getPrice(), result);

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "create-post";
        }


        if (!Arrays.stream(files).findFirst().get().isEmpty())
            for (MultipartFile mf : files) {
                post.addPostFile(postFileService.save(mf));
                postService.save(post);
            }
        try {
            post.setDeadline();
        } catch (ParseException ex) {
            return "err"; //errPage??
        }
        user.createPost(post);
        postService.save(post);
        return "redirect:/posts";
    }


    @PostMapping("/deletepost/{id}")
    public String deletePost(@PathVariable("id") Long id) {

        Post post = postService.findById(id);
        if (post == null) {
            return "no-post-err";
        }

        postService.deleteById(id);

        return "redirect:/";
    }

    @GetMapping("/posts")
    public String showMainPage(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        postService.markExpired();

        Page<Post> postPage = postService.findPaginated(PageRequest.of(currentPage - 1, pageSize));
        model.addAttribute("postPage", postPage);

        int totalPages = postPage.getTotalPages();
        if (totalPages > 1) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "posts";
    }


    @GetMapping("/changepost/{id}")
    public String showPostChangeForm(@PathVariable("id") Long id, Model model) {

        Post post = postService.findById(id);
        if (post == null) {
            return "no-post-err";
        }

        User user = userService.getCurrentUser();

        if (post.getOwner() != user || (!post.getState().equals(PostState.OPEN) && !post.getState().equals(PostState.EXPIRED)) || !post.getCandidates().isEmpty()) {
            return "permission-denied";
        }

        model.addAttribute("user", user);
        model.addAttribute("post", post);
        return "change-post";
    }

    @PostMapping("/changepost/{id}")
    public String changePost(@PathVariable("id") Long id, Post post, BindingResult result, Model model) {

        Post originalPost = postService.findById(id);
        if (post == null) {
            return "no-post-err";
        }

        User owner = originalPost.getOwner();

        if (originalPost.getOwner() != owner || (!originalPost.getState().equals(PostState.OPEN) && !originalPost.getState().equals(PostState.EXPIRED)) || !originalPost.getCandidates().isEmpty()) {
            return "permission-denied";
        }

        postValidator.validate(post, result);
        userValidator.validateBalance(owner, post.getPrice() - originalPost.getPrice(), result);
        if (result.hasErrors()) {
            model.addAttribute("user", owner);
            return "change-post";
        }

        owner.changePost(originalPost, post);
        postService.save(originalPost);

        return "redirect:/viewpost/{id}?success";
    }

    @GetMapping("/candidates/{id}")
    public String showCandidates(@PathVariable("id") Long id, Model model) {

        Post post = postService.findById(id);
        if (post == null) {
            return "no-post-err";
        }

        if (post.getOwner() != userService.getCurrentUser() || !post.getState().equals(PostState.OPEN) || post.getCandidates().isEmpty()) {
            return "permission-denied";
        }

        model.addAttribute("priceService", suggestedPriceService);
        model.addAttribute("post", post);

        return "candidates";
    }

    @PostMapping("/candidates/{id}/{username}")
    public String chooseCandidate(Model model, Post post, BindingResult result, @PathVariable("id") Long id, @PathVariable String username) {

        Post postById = postService.findById(id);
        if (post == null) {
            return "no-post-err";
        }

        if (userService.findByUsername(username) == null) {
            return "no-user-err";
        }

        User user = userService.findByUsername(username);


//todo: provide a better solution.
        post.setCandidates(postById.getCandidates());
        post.setSuggestedPrices(postById.getSuggestedPrices());
        postValidator.validateCandidate(post, user, result);
        if(result.hasErrors()){
            model.addAttribute("post", post);
            model.addAttribute("priceService", suggestedPriceService);
            return "candidates";
        }

        SuggestedPrice suggestedPrice = suggestedPriceService.getSuggestedPrice(user, postById);

        user.confirmPost(postById, suggestedPrice.getValue());
        postService.save(postById);

        return "redirect:/viewpost/{id}";
    }
}