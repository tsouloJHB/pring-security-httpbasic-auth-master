package com.thecodealchemist.main.service;

import com.thecodealchemist.main.dto.UserDTO;
import com.thecodealchemist.main.entity.User;
import com.thecodealchemist.main.model.AuthenticatedUser;
import com.thecodealchemist.main.repository.UserRepository;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
        long accountNumber = generateAccountNumber();
        String expiryDate = generateExpiryDate();
        
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
        u.setAccountNumber(accountNumber);
        u.setExpiryDate(expiryDate); 
        

        // Save the user
        userRepository.save(u);

        // Prepare success response
        response.put("message", "User created successfully");
        response.put("user", u);
        return ResponseEntity.ok(response);
    }

    // Method to generate a 12-digit random account number
    private long generateAccountNumber() {
        Random rand = new Random();
        // Ensure the number is exactly 12 digits
        return 100000000000L + (long)(rand.nextDouble() * 900000000000L);
    }
    // Method to generate expiry date in MM/YY format (current month and next year)
    private String generateExpiryDate() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;  // Get current month (1-12)
        int currentYear = calendar.get(Calendar.YEAR);         // Get current year
        int nextYear = currentYear + 2;                          // Get next year

        // Format month and year to MM/YY
        String month = String.format("%02d", currentMonth);  // Ensure month is 2 digits
        String year = String.format("%02d", nextYear % 100);  // Get last two digits of next year

        return month + "/" + year;
    }

        // Login functionality
        public User login(String username, String password) {
            User account;
        
            // Check if username is an email address
            if (username.contains("@")) {
                account = userRepository.findByEmail(username); // Email login
            } else {
                account = userRepository.findByCellNumber(username); // Cell number login
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
