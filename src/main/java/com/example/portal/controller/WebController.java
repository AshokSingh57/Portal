package com.example.portal.controller;

import com.example.portal.client.ProvisionerClient;
import com.example.portal.dto.*;
import com.example.portal.exception.ProvisionerUnavailableException;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    private final ProvisionerClient provisionerClient;

    public WebController(ProvisionerClient provisionerClient) {
        this.provisionerClient = provisionerClient;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login(HttpSession session, Model model,
                        @RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        @RequestParam(required = false) String registered) {
        if (session.getAttribute("authToken") != null) {
            return "redirect:/dashboard";
        }
        if (error != null) {
            model.addAttribute("error", error);
        }
        if (logout != null) {
            model.addAttribute("success", "You have been successfully logged out.");
        }
        if (registered != null) {
            model.addAttribute("success", "Registration successful! Please sign in.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String register(HttpSession session) {
        if (session.getAttribute("authToken") != null) {
            return "redirect:/dashboard";
        }
        return "register";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String email,
                              @RequestParam String password,
                              HttpSession session, Model model) {
        try {
            LoginRequest request = new LoginRequest(email, password);
            AuthResponse response = provisionerClient.login(request);

            if (response != null && response.isSuccess()) {
                session.setAttribute("authToken", response.getToken());
                session.setAttribute("userId", response.getUserId());
                session.setAttribute("userEmail", response.getEmail());
                session.setAttribute("userFullName", response.getFullName());
                session.setAttribute("userRole", response.getRole());
                return "redirect:/dashboard";
            } else {
                String msg = (response != null && response.getMessage() != null)
                        ? response.getMessage() : "Login failed";
                model.addAttribute("error", msg);
                return "login";
            }
        } catch (ProvisionerUnavailableException e) {
            model.addAttribute("error", "Service unavailable. Please try again later.");
            return "login";
        }
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam String email,
                                 @RequestParam String fullName,
                                 @RequestParam String password,
                                 Model model) {
        try {
            RegisterRequest request = new RegisterRequest(email, fullName, password);
            AuthResponse response = provisionerClient.register(request);

            if (response != null && response.isSuccess()) {
                return "redirect:/login?registered";
            } else {
                String msg = (response != null && response.getMessage() != null)
                        ? response.getMessage() : "Registration failed";
                model.addAttribute("error", msg);
                return "register";
            }
        } catch (ProvisionerUnavailableException e) {
            model.addAttribute("error", "Service unavailable. Please try again later.");
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        populateModelFromSession(session, model);
        return "dashboard";
    }

    @GetMapping("/admin")
    public String admin(HttpSession session, Model model) {
        populateModelFromSession(session, model);
        return "admin";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }

    @GetMapping("/oauth2/callback")
    public String oauth2Callback(@RequestParam String token, HttpSession session, Model model) {
        try {
            TokenValidationResponse validation = provisionerClient.validateToken(token);
            if (validation != null && validation.isSuccess()) {
                session.setAttribute("authToken", token);
                session.setAttribute("userId", validation.getUserId());
                session.setAttribute("userEmail", validation.getEmail());
                session.setAttribute("userFullName", validation.getFullName());
                session.setAttribute("userRole", validation.getRole());
                return "redirect:/dashboard";
            } else {
                return "redirect:/login?error=OAuth2 authentication failed";
            }
        } catch (ProvisionerUnavailableException e) {
            return "redirect:/login?error=Service unavailable";
        }
    }

    private void populateModelFromSession(HttpSession session, Model model) {
        model.addAttribute("fullName", session.getAttribute("userFullName"));
        model.addAttribute("email", session.getAttribute("userEmail"));
        model.addAttribute("role", session.getAttribute("userRole"));
        model.addAttribute("userId", session.getAttribute("userId"));
    }
}
