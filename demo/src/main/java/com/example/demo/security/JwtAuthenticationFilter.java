package com.example.demo.security;

import com.example.demo.service.CustomUserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final Jwtutil jwtutil;
    private  final CustomUserDetailService customUserDetailService;

    public JwtAuthenticationFilter(Jwtutil jwtutil, CustomUserDetailService customUserDetailService) {
        this.jwtutil = jwtutil;
        this.customUserDetailService = customUserDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {


        String token =request.getHeader("Authorization");
        if(token!=null&&token.startsWith("Bearer")){
            token=token.substring(7);
            String username=jwtutil.extractUsername(token);
            if(username!=null&& SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails=customUserDetailService.loadUserByUsername(username);
                if(jwtutil.isTokenValid(token)){
                    UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());



                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }


                   }
        }
        filterChain.doFilter(request,response);

    }
}
