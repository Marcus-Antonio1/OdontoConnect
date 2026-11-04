package com.odontoconnect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configura o mecanismo de criptografia utilizado para armazenar senhas.
 */
@Configuration
public class PasswordConfig {

    // Disponibiliza o BCrypt como mecanismo de hash de senhas da aplicação.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
