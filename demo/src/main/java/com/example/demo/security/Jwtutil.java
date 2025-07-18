package com.example.demo.security;


import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Jwtutil {

    private static final SecretKey secretKey= Keys.secretKeyFor(SignatureAlgorithm.HS512);


    private final int jwtExpirationMs=86400000;

    private UserRepository userRepository;
    public Jwtutil(UserRepository userRepository){
        this.userRepository=userRepository;
    }





    public String generateToken(String username){
        Optional<User> user=userRepository.findByUsername(username);
                Set< Role > roles=user.get().getRoles();



                return Jwts.builder().setSubject(username).claim("role",roles.stream().map(role -> role.getName()).collect(Collectors.joining(","))).setIssuedAt(new Date()).setExpiration(new Date(new Date().getTime()+jwtExpirationMs)).signWith(secretKey).compact();
    }


    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
    }


    public Set<String> extractRoles(String token) {
        String roles = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class); // match the claim key

        return Set.of(roles.split(",")); // split comma-separated roles
    }



    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey) // secretKey should be SecretKey type
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }







}
