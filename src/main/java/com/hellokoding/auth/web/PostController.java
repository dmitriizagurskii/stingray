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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
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

        User user = userService.findCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("post", post);
        return "create-post";
    }

    @PostMapping("/createpost")
    public String createPost(Model model, Post post, BindingResult result,
                             @RequestParam("files") MultipartFile[] files) {

        User user = userService.findCurrentUser();
        if (user == null) {
            return "no-user-err";
        }

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

        postService.deleteExpired();

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

        User user = userService.findCurrentUser();
        if (user == null) {
            return "no-user-err";
        }

        if (post.getOwner() != user) {
            return "permission-denied";
        }

        if (post.getState().equals(PostState.ASSIGNED)) {
            return "redirect:/viewconfirmedpost/{id}";
        }

        model.addAttribute("user", user);
        model.addAttribute("post", post);
        return "change-post";
    }

    @PostMapping("/changepost/{id}")
    public String changePost(@PathVariable("id") Long id, @Valid Post post, BindingResult result, Model model) {

        Post originalPost = postService.findById(id);
        if (post == null) {
            return "no-post-err";
        }

        User owner = originalPost.getOwner();

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

        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals(post.getOwner().getUsername())) {
            return "permission-denied";
        }

        if (post.getState().equals(PostState.ASSIGNED)) {
            return "redirect:/viewconfirmedpost/{id}";
        }

        model.addAttribute("priceService", suggestedPriceService);
        model.addAttribute("post", post);

        return "candidates";
    }

    @PostMapping("/candidates/{id}/{username}")
    public String chooseCandidate(@PathVariable("id") Long id, @PathVariable String username) {

        Post post = postService.findById(id);
        if (post == null) {
            return "no-post-err";
        }

        if (userService.findByUsername(username) == null) {
            return "no-user-err";
        }

        User user = userService.findByUsername(username);
        SuggestedPrice suggestedPrice = suggestedPriceService.getSuggestedPrice(user, post);

        user.confirmPost(post, suggestedPrice.getValue());
        postService.save(post);

        return "redirect:/viewpost/{id}";
    }

    @GetMapping("/viewconfirmedpost/{id}")
    public String viewAcceptedPost(@PathVariable("id") Long id, Model model) {

        Post post = postService.findById(id);
        if (post == null) {
            return "no-post-err";
        }

        if (!post.getState().equals(PostState.ASSIGNED)) {
            return "redirect:/viewpost/{id}";
        }
        User currentUser = userService.findCurrentUser();
        model.addAttribute("user", currentUser);
        model.addAttribute("post", post);

        return "viewconfirmedpost";
    }


}