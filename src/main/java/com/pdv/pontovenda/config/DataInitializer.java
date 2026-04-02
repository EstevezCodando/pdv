package com.pdv.pontovenda.config;

import com.pdv.pontovenda.entity.Usuario;
import com.pdv.pontovenda.repository.UsuarioRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Inicializa usuarios de desenvolvimento com senhas BCrypt.
 * Executado apenas no perfil 'h2' (desenvolvimento local).
 */
@Component
@Profile("h2")
public class DataInitializer implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (usuarioRepository.count() == 0) {
            usuarioRepository.save(new Usuario(null, "Administrador", "admin@pdv.com",
                    passwordEncoder.encode("admin123"), "ADMIN", true));
            usuarioRepository.save(new Usuario(null, "Operador PDV", "operador@pdv.com",
                    passwordEncoder.encode("op123"), "OPERADOR", true));
            usuarioRepository.save(new Usuario(null, "Maria Silva", "maria@pdv.com",
                    passwordEncoder.encode("maria123"), "OPERADOR", true));
        }
    }
}
