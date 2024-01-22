package ru.heumn.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthorizationController {

    @GetMapping()
    public String login(@RequestParam(required = false) String error, Model model) {

        try {
            if (error.isEmpty()) {
                error = "Неправильный логин или пароль";
            }
        } catch (Exception ignored) {
        }

        model.addAttribute("userError", error);
        return "login";
    }
}
