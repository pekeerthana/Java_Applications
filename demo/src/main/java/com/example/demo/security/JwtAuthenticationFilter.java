package com.example.demo.security;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.demo.entity.User;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.respository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


@Component
public class JwtAuthenticationFilter  extends OncePerRequestFilter{

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository){
            this.jwtUtil= jwtUtil;
            this.userRepository = userRepository;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request,HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");
        System.out.println("Header "+authHeader);
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);
            if(jwtUtil.tokenValidation(token)){
                String email = jwtUtil.extractEmail(token);

                //fetch user
                User user = userRepository.findByEmailWithRoles(email).orElseThrow(() -> new UserNotFoundException("User not found with email: "+ email));

                Set<GrantedAuthority> authorities = new HashSet<>();
                for(var role : user.getRoles()){

                    authorities.add(new SimpleGrantedAuthority(role.getName()));
                    for(var permission : role.getPermissions()){
                        authorities.add(new SimpleGrantedAuthority(permission.getName()));
                    }
                }

                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                System.out.println("authentication " + authentication);
                if(SecurityContextHolder.getContext().getAuthentication() == null){
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                System.out.println("username "+authentication.getName());
            }
        }

        filterChain.doFilter(request, response);

    }
    
			
}
