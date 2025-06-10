package com.example.jwt_demo.config;

import com.example.jwt_demo.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // /api/auth/** ile başlayan isteklere izin ver (permitAll), yeni giriş, kayıt gibi auth endpoint'leri açık olsun
                // Yukarıdakilerin dışındaki tüm istekler ise kimlik doğrulaması (.authenticated()) gerektirsin
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                // oturum yönetimini STATELESS olarak ayarlar
                // her istek kendi başına JWT ile doğrulanacak; server'da oturum tutulmayacak
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Kendi jwtAuthFilter ını, Spring'in UsernamePasswordAuthenticationFilter'ından önce çalışacak şekilde ekler
                // Böylece her istekten önce JWT kontrolü yapılır
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        // Tüm ayarları tamamladıktan sonra SecurityFilterChain nesnesini oluşturur ve bean olarak döner
        return http.build();
    }

    // Şifreleri BCrypt ile hash'lemek ve doğrulamak için bir bean sağlar
    // Speing security AuthenticationManager bu encoder'ı otomatik kullanır
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Varsayılan Spring boot yapılandırmasından aldığı AuthenticationManager'ı bean olarak kayıt eder
    // Böylece başka sınıflarda (Örn: auth controller'daki login endpointinde) AuthenticationManager'ı @Autowired ile kullanabiliriz
    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
}
