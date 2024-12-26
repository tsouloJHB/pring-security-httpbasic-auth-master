package com.thecodealchemist.main.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String username; // Updated to handle both email and cell number
    private String password;
}

