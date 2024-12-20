package com.thecodealchemist.main.controller;

import com.thecodealchemist.main.dto.LoginDTO;
import com.thecodealchemist.main.dto.UserDTO;
import com.thecodealchemist.main.entity.User;
import com.thecodealchemist.main.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody UserDTO userDTO) {
        // Call the register method from userService
        return userService.register(userDTO);
    }


        // Endpoint for login
        
        @PostMapping("/login")
        public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDTO loginDto) {
            System.out.println("Username: " + loginDto.getEmail() + ", Password: " + loginDto.getPassword());
        
            User user = userService.login(loginDto.getEmail(), loginDto.getPassword());
        
            // Create the response map
            Map<String, Object> response = new HashMap<>();
        
            if (user != null) {
                response.put("message", "Login successful! Welcome, " + user.getEmail() + "!");
                response.put("user", user);  // You can include user details in the response if needed
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Invalid email or password");
                return ResponseEntity.badRequest().body(response);
            }
        }
        
    // Logout endpoint (works with Spring Security)
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        // Invalidate the session or remove the authentication
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("Logout successful!");
    }    
}
