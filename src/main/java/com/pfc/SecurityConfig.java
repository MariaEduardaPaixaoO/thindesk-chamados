package com.pfc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")) // A API REST usa autenticação HTTP Basic
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/registrar", "/error", "/css/**", "/js/**", "/images/**").permitAll() // Permitir acesso ao login, cadastro e arquivos estáticos
                .anyRequest().authenticated() // Exigir autenticação para qualquer outra página
            )
            .formLogin(form -> form
                .loginPage("/login") // Definir a URL da página de login personalizada
                .defaultSuccessUrl("/dashboard", true) // Redirecionar para o dashboard após login bem-sucedido
                .permitAll()
            )
            .httpBasic(Customizer.withDefaults())
            .exceptionHandling(ex -> ex
                .defaultAuthenticationEntryPointFor( // A API responde 401 em vez de redirecionar para o login
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    request -> request.getRequestURI().startsWith(request.getContextPath() + "/api/"))
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout") // Redirecionar para o login após logout
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
