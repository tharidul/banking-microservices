package com.bank.authservice.service;

import com.bank.authservice.dto.request.LoginRequest;
import com.bank.authservice.dto.request.RegisterRequest;
import com.bank.authservice.dto.response.AuthResponse;
import com.bank.authservice.entity.Role;
import com.bank.authservice.entity.User;
import com.bank.authservice.repository.UserRepository;
import com.bank.authservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest registerRequest){

        if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()){
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .fullName(registerRequest.getFullName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();

    }

    public AuthResponse login(LoginRequest request){

       try{
           authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(
                           request.getEmail(),
                           request.getPassword()
                   )
           );
       }catch (Exception e){
           throw new RuntimeException("Invalid credentials " + e.getMessage());
       }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new RuntimeException ("User not found"));

        String token = jwtService.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }
}
