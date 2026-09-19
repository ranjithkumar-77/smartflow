package com.smartflow.smartflow.service;

import com.smartflow.smartflow.entity.User;
import com.smartflow.smartflow.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User details are required");
        }

        String name = user.getName() == null ? "" : user.getName().trim();
        String email = user.getEmail() == null ? "" : user.getEmail().trim().toLowerCase();
        String phone = user.getPhone() == null ? "" : user.getPhone().trim();
        String password = user.getPassword() == null ? "" : user.getPassword();
        String role = user.getRole() == null ? "" : user.getRole().trim();

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }

        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists. Please use a different email.");
        }

        if (phone.isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }

        if (!role.equals("CUSTOMER") && !role.equals("TECHNICIAN")) {
            throw new IllegalArgumentException("Role must be CUSTOMER or TECHNICIAN");
        }

        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);

        String hashedPassword = passwordEncoder.encode(password);
        user.setPassword(hashedPassword);

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {
            String message = ex.getMostSpecificCause() != null
                    ? ex.getMostSpecificCause().getMessage()
                    : ex.getMessage();

            if (message != null && message.toLowerCase().contains("email")) {
                throw new IllegalArgumentException("Email already exists. Please use a different email.");
            }

            if (message != null && message.toLowerCase().contains("phone")) {
                throw new IllegalArgumentException("Phone number already exists. Please use a different phone number.");
            }

            throw new IllegalArgumentException("This account details already exist. Please use different information.");
        }
    }

    public User loginUser(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        String normalizedEmail = email.trim();

        if (!normalizedEmail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return user;
    }
}
