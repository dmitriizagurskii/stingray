package com.hellokoding.auth.web;

import com.hellokoding.auth.model.Post;
import com.hellokoding.auth.model.SuggestedPrice;
import com.hellokoding.auth.model.User;
import com.hellokoding.auth.service.PostService;
import com.hellokoding.auth.service.SuggestedPriceService;
import com.hellokoding.auth.service.UserService;
import com.hellokoding.auth.validator.PostValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class PostController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostValidator postValidator;

    @Autowired
    private PostService postService;

    @Autowired
    private SuggestedPriceService suggestedPriceService;

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
    public String createPost(Post post, BindingResult result, Model model) {
        postValidator.validate(post, result);

        if (result.hasErrors()) {
            return "create-post";
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return "no-user-err";
        }

        user.createPost(post);
        postService.save(post);

        return "redirect:/posts";
    }

    @GetMapping("/posts")
    public String showMainPage(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        Page<Post> postPage = postService.findPaginated(PageRequest.of(currentPage - 1, pageSize));
        model.addAttribute("postPage", postPage);
        System.out.println(postPage.toString()+"\n\n\n\n\n");

        int totalPages = postPage.getTotalPages();
        if (totalPages > 1){
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "posts";
    }

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

        model.addAttribute("suggestedPrice", suggestedPriceService.getSuggestedPrice(user, post));
        model.addAttribute("user", user);
        model.addAttribute("post", post);
        System.out.println(suggestedPriceService.getSuggestedPrice(user, post).getValue() + "\nINSIDE\n\n\n\n");
        return "view-post";
    }


    @PostMapping(value = "/viewpost/{id}", params = "delete")
    public String deletePost(@PathVariable("id") Long id) {

        Post post = postService.findById(id);
        if (post == null) {
            return "no-post-err";
        }

        User owner = post.getOwner();
        userService.topUpBalance(owner, post.getPrice());
        owner.setReserved(owner.getReserved() - post.getPrice());

        suggestedPriceService.deleteAll(post.getSuggestedPrices());
        postService.deleteById(id);
        return "redirect:/";
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

    @GetMapping("/changepost/{id}")
    public String showPostChangeForm(@PathVariable("id") Long id, Model model) {

        Post post = postService.findById(id);
        if (post == null) {
            return "no-post-err";
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return "no-user-err";
        }

        if (post.getOwner() != user) {
            return "permission-denied";
        }

        if (post.isConfirmed()) {
            return "redirect:/viewconfirmedpost/{id}";
        }

        model.addAttribute("user", user);
        model.addAttribute("post", post);
        return "change-post";
    }

    @PostMapping("/changepost/{id}")
    public String changePost(@PathVariable("id") Long id, @Valid Post post, BindingResult result) {

        // TODO: 3/21/19 Solve how to validate the post here properly.
        // Надо сравнивать price не с остатком на счете, а с balance+post.getPrice(). Потому что на этот пост уже зарезервированы бабки.
        //postValidator.validate(post, result);

        if (result.hasErrors()) {
            return "change-post";
        }

        Post originalPost = postService.findById(id);
        if (post == null) {
            return "no-post-err";
        }

        User owner = originalPost.getOwner();
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

        if (post.isConfirmed()) {
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

        user.confirmPost(post);
        postService.save(post);

        return "redirect:/viewpost/{id}";
    }

    @GetMapping("/viewconfirmedpost/{id}")
    public String viewAcceptedPost(@PathVariable("id") Long id, Model model) {

        Post post = postService.findById(id);
        if (post == null) {
            return "no-post-err";
        }

        if (!post.isConfirmed()) {
            return "redirect:/viewpost/{id}";
        }

        model.addAttribute("post", post);

        return "viewconfirmedpost";
    }
}