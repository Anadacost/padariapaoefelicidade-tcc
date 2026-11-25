
package senai499.com.br.paoefelicidade.config;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import senai499.com.br.paoefelicidade.model.UsuariosModel;
import senai499.com.br.paoefelicidade.repository.UsuariosRepository;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UsuariosRepository usuariosRepository;

    public LoginSuccessHandler(UsuariosRepository usuariosRepository) {
        this.usuariosRepository = usuariosRepository;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        String email = authentication.getName();

        UsuariosModel usuario = usuariosRepository
                .findByEmailIgnoreCase(email)
                .orElseThrow();

        HttpSession session = request.getSession();

        session.setAttribute("usuarioId", usuario.getId());
        session.setAttribute("nomeUsuario", usuario.getEmail());
        session.setAttribute("perfil", usuario.getPerfil());

        session.setMaxInactiveInterval(30 * 60);

        String destino = switch (usuario.getPerfil()) {
            case ADMIN -> "/dashboard";
            case FUNCIONARIO -> "/funcionario/painel";
            case CLIENTE -> "/cliente/painel";
        };

        response.sendRedirect(
                request.getContextPath() + destino
        );
    }
}
