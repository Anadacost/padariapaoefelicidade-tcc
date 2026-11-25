package senai499.com.br.paoefelicidade.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import senai499.com.br.paoefelicidade.model.UsuariosModel;
import senai499.com.br.paoefelicidade.repository.UsuariosRepository;

@Service
public class UsuarioDetailsService
        implements UserDetailsService {

    private final UsuariosRepository usuariosRepository;

    public UsuarioDetailsService(
            UsuariosRepository usuariosRepository) {

        this.usuariosRepository = usuariosRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        UsuariosModel usuario = usuariosRepository
                .findByEmailIgnoreCase(email.trim())
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuário não encontrado"
                        )
                );

        if (usuario.getPerfil() == null) {
            throw new UsernameNotFoundException(
                    "Usuário sem perfil"
            );
        }

        return User.builder()

                .username(usuario.getEmail())

                .password(usuario.getSenha())

                .roles(usuario.getPerfil().name())

                .disabled(!usuario.isAtivo())

                .build();
    }
}