
package senai499.com.br.paoefelicidade.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import senai499.com.br.paoefelicidade.dto.CadastroClienteDTO;
import senai499.com.br.paoefelicidade.model.ClientesModel;
import senai499.com.br.paoefelicidade.model.PerfilUsuario;
import senai499.com.br.paoefelicidade.model.UsuariosModel;
import senai499.com.br.paoefelicidade.repository.ClientesRepository;
import senai499.com.br.paoefelicidade.repository.UsuariosRepository;

@Service
public class ClientesService {

    private final ClientesRepository clientesRepository;
    private final UsuariosRepository usuariosRepository;
    private final PasswordEncoder passwordEncoder;

    public ClientesService(
            ClientesRepository clientesRepository,
            UsuariosRepository usuariosRepository,
            PasswordEncoder passwordEncoder) {

        this.clientesRepository = clientesRepository;
        this.usuariosRepository = usuariosRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void cadastrar(CadastroClienteDTO form) {

        // VALIDAÇÃO DOS CAMPOS
        if (form.getNome() == null || form.getNome().isBlank()
                || form.getCpf() == null || form.getCpf().isBlank()
                || form.getTelefone() == null || form.getTelefone().isBlank()
                || form.getEmail() == null || form.getEmail().isBlank()
                || form.getSenha() == null || form.getSenha().isBlank()
                || form.getConfirmarSenha() == null || form.getConfirmarSenha().isBlank()) {

            throw new IllegalArgumentException(
                    "Preencha todos os campos obrigatórios.");
        }

        // NORMALIZAÇÃO
        String cpf = form.getCpf().replaceAll("\\D", "");
        String email = form.getEmail().trim().toLowerCase();

        // VALIDAÇÃO DO CPF
        if (cpf.length() != 11) {
            throw new IllegalArgumentException(
                    "O CPF deve conter 11 números.");
        }

        if (clientesRepository.existsByCpf(cpf)) {
            throw new IllegalArgumentException(
                    "Este CPF já está cadastrado.");
        }

        // VALIDAÇÃO DO E-MAIL
        if (usuariosRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException(
                    "Este e-mail já está cadastrado.");
        }

        // VALIDAÇÃO DA SENHA
        if (form.getSenha().length() < 8) {
            throw new IllegalArgumentException(
                    "A senha deve possuir pelo menos 8 caracteres.");
        }

        if (!form.getSenha().equals(form.getConfirmarSenha())) {
            throw new IllegalArgumentException(
                    "As senhas não coincidem.");
        }

        // CRIAÇÃO DO USUÁRIO
        UsuariosModel usuario = new UsuariosModel();

        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(form.getSenha()));
        usuario.setPerfil(PerfilUsuario.CLIENTE);
        usuario.setAtivo(true);

        usuariosRepository.save(usuario);

        // CRIAÇÃO DO CLIENTE
        ClientesModel cliente = new ClientesModel();

        cliente.setNome(form.getNome().trim());
        cliente.setCpf(cpf);
        cliente.setTelefone(form.getTelefone().trim());
        cliente.setUsuario(usuario);
        cliente.setAtivo(true);

        clientesRepository.save(cliente);
    }
}
