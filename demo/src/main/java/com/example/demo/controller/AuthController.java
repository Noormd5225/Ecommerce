package com.example.demo.controller;


import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.entity.Role;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.Jwtutil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final Jwtutil jwtutil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;




    public AuthController(AuthenticationManager authenticationManager, Jwtutil jwtutil, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtutil = jwtutil;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }



    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {


        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }


        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());


        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        newUser.setPassword(encodedPassword);


        Set<Role> roles = new HashSet<>();
        for (String roleName : registerRequest.getRoles())
        {
            Role role = roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roles.add(role);
        }
        newUser.setRoles(roles);
        userRepository.save(newUser);

        return ResponseEntity.ok("User registration completed successfully.");
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User loginRequest){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
        }
        catch (Exception e){
            System.out.println("Exception"+e);
        }
        String token=   jwtutil.generateToken(loginRequest.getUsername());
        return ResponseEntity.ok(token);
    }







}
