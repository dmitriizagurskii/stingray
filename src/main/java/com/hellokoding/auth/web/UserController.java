package com.hellokoding.auth.web;

import com.hellokoding.auth.model.Post;
import com.hellokoding.auth.model.User;
import com.hellokoding.auth.service.SecurityService;
import com.hellokoding.auth.service.UserService;
import com.hellokoding.auth.validator.PaymentValidator;
import com.hellokoding.auth.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

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
        model.addAttribute("user", userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()));

        return "profile";
    }

    @GetMapping("/user/{username}")
    public String userPage(@PathVariable String username, Model model) {
        model.addAttribute("user", userService.findByUsername(username));
        return "user";
    }

    @GetMapping("/top-up-balance")
    public String topUpBalance(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("userPayForm", userService.findByUsername(username));
        return "top-up";
    }

    @PostMapping("/top-up-balance")
    public String topUpBalanceProceededProfile(@ModelAttribute("userPayForm") User userPayForm, BindingResult bindingResult,  Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user =  userService.findByUsername(username);


        paymentValidator.validate(userPayForm, bindingResult);
        if (bindingResult.hasErrors()) {
            return "top-up";
        }

        userService.topUpBalance(user, Integer.valueOf(userPayForm.getSumBuff()));

        model.addAttribute("user", user);
        model.addAttribute("posts", user.getCreatedPosts());

        return "redirect:/profile";
    }

    @GetMapping("/withdraw")
    public String withdraw(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        model.addAttribute("userWithdrawForm", userService.findByUsername(username));
        return "withdraw";
    }

    @PostMapping("/withdraw")
    public String withdrawProceededProfile(@ModelAttribute("userWithdrawForm") User userWithdrawForm, BindingResult bindingResult,  Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user =  userService.findByUsername(username);


        paymentValidator.withdrawValid(userWithdrawForm, bindingResult, user);
        if (bindingResult.hasErrors()) {
            return "withdraw";
        }

        userService.withdraw(user, Integer.valueOf(userWithdrawForm.getSumBuff()));

        model.addAttribute("user", user);
        model.addAttribute("posts", user.getCreatedPosts());

        return "redirect:/profile";
    }

}
