package com.hellokoding.auth.web;

import com.hellokoding.auth.model.Task;
import com.hellokoding.auth.model.Rating;
import com.hellokoding.auth.model.User;
import com.hellokoding.auth.service.TaskService;
import com.hellokoding.auth.service.RatingService;
import com.hellokoding.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.math.BigInteger;

@Controller
public class RatingController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private RatingService ratingService;

    @GetMapping("/rateTask/{id}")
    public String rateTask(@PathVariable("id") BigInteger id, Model model) {
        User user = userService.getCurrentUser();
        Task task = taskService.findById(id);

        if (task == null) {
            return "no-task-err";
        }


        model.addAttribute("task", task);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("ratingForm", new Rating());
        return "rating";
    }




    @PostMapping("/rateTask/{id}")
    public String postRateTaskExecutor(@PathVariable("id") BigInteger id, @ModelAttribute("ratingForm") Rating ratingForm, BindingResult bindingResult) {

        User user = userService.getCurrentUser();
        Task task = taskService.findById(id);

        if (task == null) {
            return "no-task-err";
        }

        ratingForm.setTask(task);
        ratingForm.setAuthor(user.getUsername());

        System.out.println("=====================");
        System.out.println(ratingForm.toString());
        System.out.println(ratingForm.getAuthor());
        System.out.println(ratingForm.getRating());
        System.out.println(ratingForm.getComment());
        System.out.println(ratingForm.getId());
        System.out.println("=====================");
        System.out.println(ratingForm.getTask().toString());
        System.out.println(ratingForm.getTask().getRatingList().toString());
        System.out.println("=====================");

        ratingForm.getTask().rate(ratingForm);
        System.out.println(task.getRatingList().toString());
        System.out.println("=====================");

        ratingService.save(ratingForm);
        return "redirect:/viewtask/{id}";
    }



}
