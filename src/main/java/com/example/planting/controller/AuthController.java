package com.example.planting.controller;

import com.example.planting.model.User;
import com.example.planting.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // 🔐 Show Register Page
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // 📥 Register New User
    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user, Model model) {
        boolean success = userService.register(user);
        if (!success) {
            model.addAttribute("error", "Email already exists!");
            return "register";
        }
        return "redirect:/login?success";
    }

    // 🔑 Custom Login Page
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
