package com.example.planting.service;

import com.example.planting.model.User;
import com.example.planting.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public boolean register(User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return false; // Already exists
        }
        user.setPassword(passwordEncoder.encode(user.getPassword())); // 🔐 Encrypt
        userRepository.save(user);
        return true;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
