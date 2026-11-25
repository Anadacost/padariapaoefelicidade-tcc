
package senai499.com.br.paoefelicidade.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import senai499.com.br.paoefelicidade.model.PerfilUsuario;
import senai499.com.br.paoefelicidade.model.UsuariosModel;
import senai499.com.br.paoefelicidade.repository.UsuariosRepository;
import senai499.com.br.paoefelicidade.service.UsuariosService;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner criarAdministrador(
            UsuariosRepository usuariosRepository,
            UsuariosService usuariosService) {

        return args -> {

            String email = "adminfelicidade@gmail.com";

            if (usuariosRepository.existsByEmailIgnoreCase(email)) {
                return;
            }

            UsuariosModel admin = new UsuariosModel();

            admin.setEmail(email);
            admin.setSenha("123456789");
            admin.setPerfil(PerfilUsuario.ADMIN);
            admin.setAtivo(true);

            usuariosService.cadastrar(admin);

            System.out.println(
                    "Administrador inicial cadastrado."
            );
        };
    }
}
