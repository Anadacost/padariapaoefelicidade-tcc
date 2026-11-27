
package senai499.com.br.paoefelicidade.service;

import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Sort;
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

    private static final String SENHA_PADRAO = "123456789";

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

    // ==========================================
    // AUTOCADASTRO DO CLIENTE
    // ==========================================

    @Transactional
    public void cadastrar(CadastroClienteDTO form) {

        validarCampos(form, true);

        if (!form.getSenha().equals(form.getConfirmarSenha())) {
            throw new IllegalArgumentException(
                    "As senhas não coincidem.");
        }

        if (form.getSenha().length() < 8) {
            throw new IllegalArgumentException(
                    "A senha deve possuir pelo menos 8 caracteres.");
        }

        salvarCliente(form, form.getSenha());
    }

    // ==========================================
    // CADASTRO PELO ADMINISTRADOR OU FUNCIONÁRIO
    // ==========================================

    @Transactional
    public void cadastrarPeloSistema(CadastroClienteDTO form) {

        validarCampos(form, false);

        salvarCliente(form, SENHA_PADRAO);
    }

    // ==========================================
    // VALIDAÇÃO DOS DADOS
    // ==========================================

    private void validarCampos(
            CadastroClienteDTO form,
            boolean validarSenha) {

        if (form.getNome() == null || form.getNome().isBlank()
                || form.getCpf() == null || form.getCpf().isBlank()
                || form.getTelefone() == null || form.getTelefone().isBlank()
                || form.getEmail() == null || form.getEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "Preencha todos os campos obrigatórios.");
        }

        if (validarSenha &&
                (form.getSenha() == null || form.getSenha().isBlank()
                        || form.getConfirmarSenha() == null
                        || form.getConfirmarSenha().isBlank())) {

            throw new IllegalArgumentException(
                    "Informe e confirme a senha.");
        }
    }

    // ==========================================
    // SALVAR CLIENTE E USUÁRIO
    // ==========================================

    private void salvarCliente(
            CadastroClienteDTO form,
            String senha) {

        String cpf = form.getCpf().replaceAll("\\D", "");
        String email = form.getEmail().trim().toLowerCase(Locale.ROOT);

        if (cpf.length() != 11) {
            throw new IllegalArgumentException(
                    "O CPF deve conter 11 números.");
        }

        if (clientesRepository.existsByCpf(cpf)) {
            throw new IllegalArgumentException(
                    "Este CPF já está cadastrado.");
        }

        if (usuariosRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException(
                    "Este e-mail já está cadastrado.");
        }

        UsuariosModel usuario = new UsuariosModel();

        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setPerfil(PerfilUsuario.CLIENTE);
        usuario.setAtivo(true);

        usuariosRepository.save(usuario);

        ClientesModel cliente = new ClientesModel();

        cliente.setNome(form.getNome().trim());
        cliente.setCpf(cpf);
        cliente.setTelefone(form.getTelefone().trim());
        cliente.setUsuario(usuario);
        cliente.setAtivo(true);

        clientesRepository.save(cliente);
    }

    // ==========================================
    // LISTAR E PESQUISAR CLIENTES
    // ==========================================

    @Transactional(readOnly = true)
    public List<ClientesModel> pesquisar(String busca) {

        List<ClientesModel> clientes = clientesRepository.findAll(
                Sort.by(Sort.Direction.ASC, "nome"));

        if (busca == null || busca.isBlank()) {
            return clientes;
        }

        String termo = busca.trim().toLowerCase(Locale.ROOT);
        String numeros = termo.replaceAll("\\D", "");

        return clientes.stream()
                .filter(cliente -> {

                    String nome = cliente.getNome() == null
                            ? "" : cliente.getNome().toLowerCase(Locale.ROOT);

                    String cpf = cliente.getCpf() == null
                            ? "" : cliente.getCpf();

                    String telefone = cliente.getTelefone() == null
                            ? "" : cliente.getTelefone();

                    String email = cliente.getUsuario() == null
                            ? "" : cliente.getUsuario().getEmail()
                            .toLowerCase(Locale.ROOT);

                    return nome.contains(termo)
                            || email.contains(termo)
                            || (!numeros.isEmpty() && cpf.contains(numeros))
                            || (!numeros.isEmpty()
                            && telefone.replaceAll("\\D", "").contains(numeros));
                })
                .toList();
    }

    // ==========================================
// BUSCAR CLIENTE PARA EDIÇÃO
// ==========================================
    @Transactional(readOnly = true)
    public ClientesModel buscarPorId(Long id) {

        return clientesRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Cliente não encontrado."));
    }


    // ==========================================
// EDITAR CLIENTE
// ==========================================
    @Transactional
    public void atualizar(Long id, CadastroClienteDTO form) {

        if (form.getNome() == null || form.getNome().isBlank()
                || form.getCpf() == null || form.getCpf().isBlank()
                || form.getEmail() == null || form.getEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "Preencha os campos obrigatórios.");
        }

        ClientesModel cliente = clientesRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Cliente não encontrado."));

        String cpf = form.getCpf().replaceAll("\\D", "");
        String email = form.getEmail().trim().toLowerCase();

        if (cpf.length() != 11) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        if (clientesRepository.existsByCpfAndIdNot(cpf, id)) {
            throw new IllegalArgumentException(
                    "Este CPF já está cadastrado.");
        }

        UsuariosModel usuario = cliente.getUsuario();

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "Este cliente não possui usuário vinculado.");
        }

        if (usuariosRepository.existsByEmailIgnoreCaseAndIdNot(
                email, usuario.getId())) {

            throw new IllegalArgumentException(
                    "Este e-mail já está cadastrado.");
        }

        cliente.setNome(form.getNome().trim());
        cliente.setCpf(cpf);
        cliente.setTelefone(form.getTelefone());

        usuario.setEmail(email);

        clientesRepository.save(cliente);
        usuariosRepository.save(usuario);
    }

    // ==========================================
    // INATIVAR OU REATIVAR CLIENTE
    // ==========================================
    @Transactional
    public void alterarStatus(Long id) {

        ClientesModel cliente = clientesRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Cliente não encontrado."));

        boolean novoStatus = !cliente.isAtivo();

        cliente.setAtivo(novoStatus);

        // Atualiza também o usuário para bloquear/liberar o login
        if (cliente.getUsuario() != null) {
            cliente.getUsuario().setAtivo(novoStatus);
            usuariosRepository.save(cliente.getUsuario());
        }

        clientesRepository.save(cliente);
    }
}
