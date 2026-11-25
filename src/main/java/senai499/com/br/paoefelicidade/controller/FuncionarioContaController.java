
package senai499.com.br.paoefelicidade.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import senai499.com.br.paoefelicidade.model.FuncionariosModel;
import senai499.com.br.paoefelicidade.model.UsuariosModel;
import senai499.com.br.paoefelicidade.repository.FuncionariosRepository;
import senai499.com.br.paoefelicidade.repository.UsuariosRepository;
import senai499.com.br.paoefelicidade.dto.AlterarSenhaForm;
import senai499.com.br.paoefelicidade.service.UsuariosService;

@Controller
public class FuncionarioContaController {

    private final FuncionariosRepository funcionariosRepository;
    private final UsuariosRepository usuariosRepository;
    private final UsuariosService usuariosService;

    public FuncionarioContaController(
            FuncionariosRepository funcionariosRepository,
            UsuariosRepository usuariosRepository,
            UsuariosService usuariosService) {

        this.funcionariosRepository = funcionariosRepository;
        this.usuariosRepository = usuariosRepository;
        this.usuariosService = usuariosService;
    }

    // =====================================
    // MEU PERFIL
    // =====================================

    @GetMapping("/funcionario/perfil")
    public String perfil(Authentication authentication, Model model) {

        UsuariosModel usuario = usuariosRepository
                .findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        FuncionariosModel funcionario = funcionariosRepository
                .findByUsuario_Id(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado."));

        model.addAttribute("funcionario", funcionario);
        model.addAttribute("email", usuario.getEmail());

        return "funcionario/perfil";
    }

    @PostMapping("/funcionario/perfil")
    public String atualizarPerfil(
            @RequestParam String nome,
            @RequestParam String telefone,
            @RequestParam String email,
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        UsuariosModel usuario = usuariosRepository
                .findByEmailIgnoreCase(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        FuncionariosModel funcionario = funcionariosRepository
                .findByUsuario_Id(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado."));

        String novoEmail = email.trim();

        if (novoEmail.isBlank()) {
            redirectAttributes.addFlashAttribute(
                    "erro", "O e-mail não pode ficar vazio.");

            return "redirect:/funcionario/perfil";
        }

        // Verifica se o e-mail foi alterado
        boolean emailAlterado = !usuario.getEmail().equalsIgnoreCase(novoEmail);

        // Verifica se o novo e-mail já pertence a outra conta
        if (emailAlterado &&
                usuariosRepository.existsByEmailIgnoreCase(novoEmail)) {

            redirectAttributes.addFlashAttribute(
                    "erro", "Este e-mail já está cadastrado.");

            return "redirect:/funcionario/perfil";
        }

        // Atualiza somente os campos permitidos
        funcionario.setNome(nome.trim());
        funcionario.setTelefone(telefone.trim());

        funcionariosRepository.save(funcionario);

        if (emailAlterado) {

            usuario.setEmail(novoEmail);
            usuariosRepository.save(usuario);

            // Encerra a sessão para novo login com o e-mail atualizado
            new SecurityContextLogoutHandler()
                    .logout(request, response, authentication);

            return "redirect:/login?emailAlterado";
        }

        redirectAttributes.addFlashAttribute(
                "sucesso", "Seus dados foram atualizados com sucesso!");

        return "redirect:/funcionario/perfil";
    }

    // =====================================
    // ALTERAR SENHA
    // =====================================

    @GetMapping("/funcionario/senha")
    public String senha(Model model) {
        model.addAttribute("alterarSenhaForm", new AlterarSenhaForm());
        return "funcionario/senha";
    }

    @PostMapping("/funcionario/senha")
    public String alterarSenha(
            @ModelAttribute("alterarSenhaForm") AlterarSenhaForm form,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {

            usuariosService.alterarSenha(authentication.getName(), form);

            redirectAttributes.addFlashAttribute(
                    "sucesso", "Senha alterada com sucesso!");

            return "redirect:/funcionario/senha";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "erro", e.getMessage());

            return "redirect:/funcionario/senha";
        }
    }
}
