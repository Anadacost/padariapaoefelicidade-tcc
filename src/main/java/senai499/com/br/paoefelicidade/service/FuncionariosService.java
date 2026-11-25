
package senai499.com.br.paoefelicidade.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import senai499.com.br.paoefelicidade.dto.CadastroFuncionarioDTO;
import senai499.com.br.paoefelicidade.model.FuncionariosModel;
import senai499.com.br.paoefelicidade.model.PerfilUsuario;
import senai499.com.br.paoefelicidade.model.UsuariosModel;
import senai499.com.br.paoefelicidade.repository.FuncionariosRepository;
import senai499.com.br.paoefelicidade.repository.UsuariosRepository;

import senai499.com.br.paoefelicidade.model.Cargo;

@Service
public class FuncionariosService {

    private final FuncionariosRepository funcionariosRepository;
    private final UsuariosRepository usuariosRepository;
    private final PasswordEncoder passwordEncoder;

    public FuncionariosService(
            FuncionariosRepository funcionariosRepository,
            UsuariosRepository usuariosRepository,
            PasswordEncoder passwordEncoder) {

        this.funcionariosRepository = funcionariosRepository;
        this.usuariosRepository = usuariosRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // ==========================================
    // CADASTRAR FUNCIONÁRIO
    // ==========================================

    @Transactional
    public FuncionariosModel cadastrar(CadastroFuncionarioDTO form) {

        validarDados(form, true);

        String cpf = form.getCpf().replaceAll("\\D", "");
        String email = form.getEmail().trim();

        if (cpf.length() != 11) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        if (funcionariosRepository.existsByCpf(cpf)) {
            throw new IllegalArgumentException(
                    "Este CPF já está cadastrado."
            );
        }

        if (usuariosRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException(
                    "Este e-mail já está cadastrado."
            );
        }

        // Criar usuário de acesso
        UsuariosModel usuario = new UsuariosModel();

        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(form.getSenha()));
        usuario.setPerfil(PerfilUsuario.FUNCIONARIO);
        usuario.setAtivo(true);

        usuariosRepository.save(usuario);

        // Criar funcionário
        FuncionariosModel funcionario = new FuncionariosModel();

        preencherDados(funcionario, form);
        funcionario.setCpf(cpf);
        funcionario.setUsuario(usuario);

        return funcionariosRepository.save(funcionario);
    }

    // ==========================================
    // LISTAR FUNCIONÁRIOS
    // ==========================================

    public List<FuncionariosModel> listar() {
        return funcionariosRepository.findAllByOrderByNomeAsc();
    }

    // ==========================================
    // BUSCAR FUNCIONÁRIO POR ID
    // ==========================================

    public FuncionariosModel buscarPorId(Long id) {

        return funcionariosRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Funcionário não encontrado."
                        )
                );
    }

    // ==========================================
    // EDITAR FUNCIONÁRIO
    // ==========================================

    @Transactional
    public FuncionariosModel editar(
            Long id,
            CadastroFuncionarioDTO form) {

        validarDados(form, false);

        FuncionariosModel funcionario = buscarPorId(id);

        String cpf = form.getCpf().replaceAll("\\D", "");

        if (cpf.length() != 11) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        funcionariosRepository.findByCpf(cpf)
                .ifPresent(outro -> {
                    if (!outro.getId().equals(id)) {
                        throw new IllegalArgumentException(
                                "Este CPF já pertence a outro funcionário."
                        );
                    }
                });

        UsuariosModel usuario = funcionario.getUsuario();

        String email = form.getEmail().trim();

        if (!usuario.getEmail().equalsIgnoreCase(email)
                && usuariosRepository.existsByEmailIgnoreCase(email)) {

            throw new IllegalArgumentException(
                    "Este e-mail já está cadastrado."
            );
        }

        usuario.setEmail(email);

        // Senha só será alterada se um novo valor for informado
        if (form.getSenha() != null && !form.getSenha().isBlank()) {

            if (form.getSenha().length() < 8) {
                throw new IllegalArgumentException(
                        "A senha deve ter pelo menos 8 caracteres."
                );
            }

            usuario.setSenha(
                    passwordEncoder.encode(form.getSenha())
            );
        }

        preencherDados(funcionario, form);
        funcionario.setCpf(cpf);

        usuariosRepository.save(usuario);

        return funcionariosRepository.save(funcionario);
    }

    // ==========================================
    // DESATIVAR FUNCIONÁRIO
    // ==========================================

    @Transactional
    public void desativar(Long id) {

        FuncionariosModel funcionario = buscarPorId(id);

        funcionario.setAtivo(false);

        funcionario.getUsuario().setAtivo(false);

        funcionariosRepository.save(funcionario);
        usuariosRepository.save(funcionario.getUsuario());
    }

    // ==========================================
    // REATIVAR FUNCIONÁRIO
    // ==========================================

    @Transactional
    public void reativar(Long id) {

        FuncionariosModel funcionario = buscarPorId(id);

        funcionario.setAtivo(true);
        funcionario.getUsuario().setAtivo(true);

        funcionariosRepository.save(funcionario);
        usuariosRepository.save(funcionario.getUsuario());
    }

    // ==========================================
    // PREENCHER DADOS DO FUNCIONÁRIO
    // ==========================================

    private void preencherDados(
            FuncionariosModel funcionario,
            CadastroFuncionarioDTO form) {

        funcionario.setNome(form.getNome().trim());
        funcionario.setTelefone(form.getTelefone().trim());
        funcionario.setCargo(form.getCargo());
        funcionario.setDataAdmissao(form.getDataAdmissao());
    }

    // ==========================================
    // VALIDAR DADOS
    // ==========================================

    private void validarDados(
            CadastroFuncionarioDTO form,
            boolean exigirSenha) {

        if (form.getNome() == null || form.getNome().isBlank()
                || form.getCpf() == null || form.getCpf().isBlank()
                || form.getTelefone() == null || form.getTelefone().isBlank()
                || form.getCargo() == null
                || form.getDataAdmissao() == null
                || form.getEmail() == null || form.getEmail().isBlank()) {

            throw new IllegalArgumentException(
                    "Preencha todos os campos obrigatórios."
            );
        }

        if (exigirSenha &&
                (form.getSenha() == null || form.getSenha().length() < 8)) {

            throw new IllegalArgumentException(
                    "A senha deve ter pelo menos 8 caracteres."
            );
        }
    }

    public List<FuncionariosModel> pesquisar(String termo) {

        if (termo == null || termo.isBlank()) {
            return funcionariosRepository.findAllByOrderByNomeAsc();
        }

        return funcionariosRepository.pesquisar(termo.trim());
    }
}
