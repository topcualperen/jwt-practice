package com.example.jwt_demo.runner;

import com.example.jwt_demo.model.User;
import com.example.jwt_demo.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner { // Uygulama tam açıldıktan hemen sonra run(...) metotdunu çalıştırır

    // Kullanıcıları veritabanına sorgulayıp kaydetmek için
    private final UserRepository userRepository;
    // kullanıcı şifrelerini encode (hash) etmek için
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // run(...) CommandLineRunner'ın tek metodu. Uygulama başlar başlamaz burası çalışır
    @Override
    public void run(String... args) {
        // Admin kullanıcısı
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);
        }

        // Normal kullanıcı
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole("ROLE_USER");
            userRepository.save(user);
        }
    }
}
