package com.example.jwt_demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter { // OncePerRequestFilter dan türediği için her HTTP isteğinde bir kez çalışır

    // Token çözmek, Token geçerli mi diye kontrol etmek, Kullanıcı adını almak gibi işlevler sunar
    @Autowired
    private JwtUtils jwtUtils;

    // Tokendan alınan kullanıcı adına göre kullanıcı bilgilerini almamızı sağlar
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // HTTP isteğindeki "Authorization" başlığını alır
        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String username;

        // Eğer header yoksa veya "Bearer " ile başlamıyorsa filtre işlemini geç (Burayı geçemezse isteği alamayız)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // token değişkenine sadece token alınır. "Bearer " kısmı substring ile atlanmış olur
        token = authHeader.substring(7);
        // token içindeki username bilgisine jwtUtils de bulunan extractUsername metotu ile ulaşıp username değişkenine atarız
        username = jwtUtils.extractUsername(token);

        // if bloğuna girebilmek için, username bilgisini almış olmak ve henüz güvenlik bağlamında kimlik doğrulama yapılmamış olması gerekir
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // token içindeki username e göre kullanıcını UserDetails bilgileri(şifre, rol, vb.) alınır
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // token geçerli mi kontrol eder, geçerliyse Authentication nesnesi oluşturur.
            if (jwtUtils.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // şifre burada gerekmediği için null
                        userDetails.getAuthorities() // kullanıcının rollerini verir
                );
                // ek güvenlik detayları istekten alınır (Örn: IP adresi)
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Spring security bu kullanıcıyı artık giriş yapmış gibi kabul eder
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // işlem bitince diğer filtrelere devam edilir. (Örn: controller'a ulaşması için)
        filterChain.doFilter(request, response);

    }
}
