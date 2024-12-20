package com.thecodealchemist.main.service;

import com.thecodealchemist.main.dto.UserDTO;
import com.thecodealchemist.main.entity.User;
import com.thecodealchemist.main.model.AuthenticatedUser;
import com.thecodealchemist.main.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;


@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;



      public ResponseEntity<Map<String, Object>> register(UserDTO userDTO) {
        Map<String, Object> response = new HashMap<>();

        // Check if the email already exists
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            response.put("message", "Email is already in use");
            return ResponseEntity.badRequest().body(response);
        }

        // Check if the cell number already exists
        if (userRepository.findByCellNumber(userDTO.getCellNumber()) != null) {
            response.put("message", "Cell number is already in use");
            return ResponseEntity.badRequest().body(response);
        }

        // If no existing email or cell number, proceed with registration
        User u = new User();
        u.setUsername(userDTO.getEmail());
        u.setEmail(userDTO.getEmail());
        u.setPassword(u.hashPassword(userDTO.getPassword()));
        u.setFirstName(userDTO.getFirstName());
        u.setLastName(userDTO.getLastName());
        u.setCellNumber(userDTO.getCellNumber());
        u.setProvince(userDTO.getProvince());
        u.setSuburb(userDTO.getSuburb());
        u.setCity(userDTO.getCity());
        u.setStreetNumber(userDTO.getStreetNumber());
        u.setStreetName(userDTO.getStreetName());
        u.setIdNumber(userDTO.getIdNumber());
        u.setDateOfBirth(userDTO.getDateOfBirth());
        u.setBalance(500L);

        // Save the user
        userRepository.save(u);

        // Prepare success response
        response.put("message", "User created successfully");
        response.put("user", u);
        return ResponseEntity.ok(response);
    }

        // Login functionality
        public User login(String username, String password) {
            User account;
        
            // Check if username is an email address
            if (username.contains("@")) {
                account = userRepository.findByEmail(username);
            } else {
                account = userRepository.findByEmail(username);
            }
        
            if (account != null) {
                // Verify the password using the verifyPassword method
                    
                if (verifyPassword(password, account.getPassword())) {
                    return account;
                }
            }
        
            return null;
        }
        public boolean verifyPassword(String enteredPassword, String storedHash) {
            User user = new User();  // Create a new Account object
            return user.hashPassword(enteredPassword).equals(storedHash);  // Call hashPassword() on Account instance

        }    
    // Display error message for invalid login
    public String handleInvalidLogin() {
        return "Please enter correct username or password";
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByUsername(username);
        return new AuthenticatedUser(u);
    }
}
