
package senai499.com.br.paoefelicidade.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // ==========================================
    // CRIPTOGRAFIA DE SENHAS
    // ==========================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ==========================================
    // CONFIGURAÇÃO DO SPRING SECURITY
    // ==========================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            LoginSuccessHandler loginSuccessHandler)
            throws Exception {

        http
                .authorizeHttpRequests(auth -> auth

                        // ==========================================
                        // PÁGINAS PÚBLICAS
                        // ==========================================

                        .requestMatchers(
                                "/",
                                "/index",
                                "/login",
                                "/cadastro",
                                "/cliente/cadastro",
                                "/acesso-negado",

                                // Arquivos CSS, JavaScript e imagens
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/images/**",
                                "/favicon.ico",
                                "/uploads/produtos/**"
                        ).permitAll()

                        // ==========================================
                        // CATÁLOGO E SERVIÇOS PÚBLICOS
                        // ==========================================

                        .requestMatchers(
                                "/catalogo",
                                "/catalogo/**",
                                "/servicos",
                                "/servicos/**"
                        ).permitAll()

                        // ==========================================
                        // ÁREA EXCLUSIVA DO ADMINISTRADOR
                        // ==========================================

                        .requestMatchers(
                                "/dashboard",
                                "/dashboard/**",
                                "/admin/funcionarios",
                                "/admin/funcionarios/**",
                                "/admin/vendas",
                                "/admin/vendas/**"
                        ).hasRole("ADMIN")

                        // ==========================================
                        // PRODUTOS
                        // ADMINISTRADOR E FUNCIONÁRIO
                        // ==========================================

                        .requestMatchers(
                                "/produtos",
                                "/produtos/**",
                                "/admin/produtos",
                                "/admin/produtos/**",
                                "/funcionario/produtos",
                                "/funcionario/produtos/**"
                        ).hasAnyRole("ADMIN", "FUNCIONARIO")

                        // ==========================================
                        // ÁREA DO FUNCIONÁRIO
                        // ==========================================

                        .requestMatchers(
                                "/funcionario",
                                "/funcionario/**"
                        ).hasAnyRole("ADMIN", "FUNCIONARIO")

                        // ==========================================
                        // PEDIDOS
                        // ADMINISTRADOR E FUNCIONÁRIO
                        // ==========================================

                        .requestMatchers(
                                "/pedidos",
                                "/pedidos/**"
                        ).hasAnyRole("ADMIN", "FUNCIONARIO")

                        // ==========================================
                        // ÁREA DO CLIENTE
                        // ==========================================

                        .requestMatchers(
                                "/cliente",
                                "/cliente/**"
                        ).hasRole("CLIENTE")

                        // ==========================================
                        // PERFIL E ALTERAÇÃO DE SENHA
                        // TODOS OS USUÁRIOS AUTENTICADOS
                        // ==========================================

                        .requestMatchers(
                                "/perfil",
                                "/perfil/**"
                        ).authenticated()
                        // ==========================================
                        // GERENCIAMENTO DE CLIENTES
                        // ADMINISTRADOR E FUNCIONÁRIO
                        // ==========================================
                        .requestMatchers(
                           "/admin/clientes",
                                    "/admin/clientes/**"
                        ).hasAnyRole("ADMIN", "FUNCIONARIO")

                        // ==========================================
                        // DEMAIS ROTAS
                        // ==========================================

                        .anyRequest().denyAll()
                )

                // ==========================================
                // LOGIN PERSONALIZADO
                // ==========================================

                .formLogin(form -> form

                        .loginPage("/login")

                        .loginProcessingUrl("/login")

                        .usernameParameter("email")

                        .passwordParameter("senha")

                        .failureUrl("/login?erro")

                        .successHandler(loginSuccessHandler)

                        .permitAll()
                )

                // ==========================================
                // LOGOUT
                // ==========================================

                .logout(logout -> logout

                        .logoutUrl("/logout")

                        .logoutSuccessUrl("/login?sair")

                        .invalidateHttpSession(true)

                        .deleteCookies("JSESSIONID")

                        .permitAll()
                )

                // ==========================================
                // ACESSO NEGADO
                // ==========================================

                .exceptionHandling(exception -> exception

                        .accessDeniedPage("/acesso-negado")
                );

        return http.build();
    }
}
