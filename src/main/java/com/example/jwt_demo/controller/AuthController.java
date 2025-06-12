package com.example.jwt_demo.controller;

import com.example.jwt_demo.dto.AuthRequest;
import com.example.jwt_demo.dto.AuthResponse;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.repository.UserRepository;
import com.example.jwt_demo.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // UsernamePasswordAuthenticationToken nesnesini alıp doğrulamayı yapar
    @Autowired
    private AuthenticationManager authenticationManager;

    // JWT oluşturma (generatedToken) ve doğrulama metotları barındırır
    @Autowired
    private JwtUtils jwtUtils;

    // Kullanıcıyı veri tabanına kayıt etmek için
    @Autowired
    private UserRepository userRepository;

    // şifreleri hash'lemek için (örn: BCryptPasswordEncoder)
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        // kullanıcının göndermiş olduğu düz metin şifreyi (user.getPassword()) alır ve passwordEncoder ile hash'ler
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Kullanıcı kaydedildi");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {

        // Kimlik doğrulama AuthenticationManager ile yaparız
        // new UsernamePasswordAuthenticationToken(...) Kullanıcının girdiği kullanıcı adı ve şifreyi içeren bir token nesnesi oluşturur
        // authenticate(...) Bu token'ı alıp doğrular (bizim UserDetailsServiceImpl ve PasswordEncoder aracılığıyla)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // Eğer doğrulama başarılı ise
        if (authentication.isAuthenticated()){
            // authentication.getPrincipal() Doğrulanan kullanıcıya ait UserDetails nesnesini döner
            // jwtUtils.generatedToken(...) Bu UserDetails üzerinden JWT oluşturur (payload'a username, roller vs. eklenir)
            String token = jwtUtils.generateToken(
                    (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal()
            );
            // oluşan token'ı AuthResponse DTO'sı içinde sarıp 200 OK olarak döner
            return ResponseEntity.ok(new AuthResponse(token));
        } else {
            throw new UsernameNotFoundException("Geçersiz kullanıcı adı/şifre!");
        }
    }
}
