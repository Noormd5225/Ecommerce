package com.example.demo.controller;


import com.example.demo.security.Jwtutil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class UserController {
    @Autowired
    private Jwtutil jwtutil;


    @Value("${role.admin}")
    private String roleAdmin;
    @Value("${role.user}")
    private String roleUser;


    @GetMapping("/protected-data")
    public ResponseEntity<String> getProtectedDate(@RequestHeader("Authorization") String token){
        if(token!=null&&token.startsWith("Bearer")){
            String jwtToken=token.substring(7);

            try
            {
                if(jwtutil.isTokenValid(jwtToken)){
                    String username=jwtutil.extractUsername(jwtToken);

                    Set<String> roles=jwtutil.extractRoles(jwtToken);

                    if(roles.contains(roleAdmin)){
                        return ResponseEntity.ok("Welcome "+username+" here is the "+roles+" specific data for admin");
                    }
                    else if(roles.contains(roleUser)){
                        return ResponseEntity.ok("Welcome "+username+" here is the "+roles+" specific data for user");
                    }
                    else{
                        return ResponseEntity.status(403).body("Access Denied.....");
                    }
                }
            }
            catch (Exception e){
                    return  ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header missing....");
    }










}
