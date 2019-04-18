package com.hellokoding.auth.web;

import com.hellokoding.auth.model.User;
import com.hellokoding.auth.service.PostService;
import com.hellokoding.auth.service.SecurityService;
import com.hellokoding.auth.service.UserService;
import com.hellokoding.auth.validator.PaymentValidator;
import com.hellokoding.auth.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private PaymentValidator paymentValidator;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("userForm", new User());

        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("userForm") User userForm, BindingResult bindingResult, Model model) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        //userForm.addRole("USER");
        userService.save(userForm);

        securityService.autoLogin(userForm.getUsername(), userForm.getPasswordConfirm());

        model.addAttribute("success", null);
        return "redirect:/profile";
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("error", "Your username or password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        if (model.containsAttribute("success")){
            model.addAttribute("message", true);
        } else model.addAttribute("message", false);


        User user = userService.findCurrentUser();
        postService.markExpired(user.getCreatedPosts());
        postService.markExpired(user.getAssignedPosts());

        model.addAttribute("user", user);


        return "profile";
    }

    @GetMapping("/user/{username}")
    public String userPage(@PathVariable String username, Model model) {
        model.addAttribute("user", userService.findByUsername(username));
        return "user";
    }

    @GetMapping("/top-up-balance")
    public String topUpBalance(Model model) {
        model.addAttribute("userPayForm", userService.findCurrentUser());
        return "top-up";
    }

    @PostMapping("/top-up-balance")
    public String topUpBalanceProceededProfile(@ModelAttribute("userPayForm") User userPayForm, BindingResult bindingResult,  Model model) {
        User user =  userService.findCurrentUser();

        paymentValidator.validate(userPayForm, bindingResult);
        if (bindingResult.hasErrors()) {
            return "top-up";
        }

        userService.topUpBalance(user, Integer.valueOf(userPayForm.getSumBuff()));

        return "redirect:/profile";
    }

    @GetMapping("/withdraw")
    public String withdraw(Model model) {
        model.addAttribute("userWithdrawForm", userService.findCurrentUser());
        return "withdraw";
    }

    @PostMapping("/withdraw")
    public String withdrawProceededProfile(@ModelAttribute("userWithdrawForm") User userWithdrawForm, BindingResult bindingResult,  Model model) {
        User user =  userService.findCurrentUser();

        paymentValidator.withdrawValid(userWithdrawForm, bindingResult, user);
        if (bindingResult.hasErrors()) {
            return "withdraw";
        }

        userService.withdraw(user, Integer.valueOf(userWithdrawForm.getSumBuff()));

        return "redirect:/profile";
    }

    @GetMapping("/viewcreatedposts")
    public String viewCreatedPosts( Model model) {
        User user =  userService.findCurrentUser();
        model.addAttribute("user", user);
        return "view-created-posts";
    }

    @GetMapping("/viewassignedposts")
    public String viewAcceptedPosts( Model model) {
        User user =  userService.findCurrentUser();
        model.addAttribute("user", user);
        return "view-assigned-posts";
    }
}
