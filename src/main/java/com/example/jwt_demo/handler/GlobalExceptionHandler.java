package com.example.jwt_demo.handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Bu sınıf, projedeki bütün @RestController lar için ortak istisna yönetimi sunar
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Eğer bir controller veya security katmanı BadCredentialsException fırlatırsa bu metot devreye girer
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials() {
        return ResponseEntity
                // HTTP 401 durum kodu ile cevap hazırlar
                .status(HttpStatus.UNAUTHORIZED)
                // cevap gövdesine mesaj ekler
                .body("Geçersiz kimlik bilgileri");
    }

    // JWT imza doğrulaması başarısız olduğunda çağırılır
    @ExceptionHandler({SignatureException.class})
    public ResponseEntity<String> handleJwtSignatureException() {
        return ResponseEntity
                // HTTP 403, istek anlaşıldı ama yetkili değil
                .status(HttpStatus.FORBIDDEN)
                .body("Geçersiz JWT imzası");
    }

    @ExceptionHandler({ExpiredJwtException.class})
    public ResponseEntity<String> handleJwtExpiredException() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN) // HTTP 403
                .body("JWT süresi dolmuş");
    }
}
