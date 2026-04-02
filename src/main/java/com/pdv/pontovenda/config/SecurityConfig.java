package com.pdv.pontovenda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

/**
 * Configuracao de seguranca do sistema PDV.
 *
 * Rotas publicas: /login, /actuator/health, /actuator/info
 * Rotas protegidas: todas as demais (requerem autenticacao)
 *
 * Autenticacao suportada:
 * - Form Login (navegador web) com pagina /login personalizada
 * - HTTP Basic (chamadas REST/API via curl ou ferramentas externas)
 * - Remember-me (cookie persistente para sessoes longas)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/actuator/health", "/actuator/info").permitAll()
                // H2 console acessivel apenas em desenvolvimento (perfil h2)
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            // Permite frames do H2 console (bloqueados por padrao pelo Spring Security)
            .headers(headers -> headers
                .addHeaderWriter(new XFrameOptionsHeaderWriter(
                    XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
            )
            // H2 console usa POST sem CSRF token — desabilitado apenas para esse path
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("pdv-remember-me-key")
                .tokenValiditySeconds(86400)
            )
            .httpBasic(basic -> {});

        return http.build();
    }
}
