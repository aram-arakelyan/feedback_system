package com.example.feedback.utility;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHasher {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("password1: " + encoder.encode("password1"));
        System.out.println("password2: " + encoder.encode("password2"));
        System.out.println("password3: " + encoder.encode("password3"));
    }
}
