
package senai499.com.br.paoefelicidade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import senai499.com.br.paoefelicidade.dto.CadastroFuncionarioDTO;
import senai499.com.br.paoefelicidade.model.FuncionariosModel;
import senai499.com.br.paoefelicidade.service.FuncionariosService;
import senai499.com.br.paoefelicidade.model.Cargo;

@Controller
@RequestMapping("/admin/funcionarios")
public class FuncionariosController {

    private final FuncionariosService funcionariosService;

    public FuncionariosController(
            FuncionariosService funcionariosService) {
        this.funcionariosService = funcionariosService;
    }

    // ==========================================
    // LISTAR FUNCIONÁRIOS
    // ==========================================

    @GetMapping
    public String listar(
            @RequestParam(required = false) String busca,
            Model model) {

        model.addAttribute("funcionarios",
                funcionariosService.pesquisar(busca));

        model.addAttribute("busca", busca);

        return "admin/funcionarios/index";
    }

    @GetMapping("/detalhes/{id}")
    public String detalhes(@PathVariable Long id, Model model) {

        FuncionariosModel funcionario =
                funcionariosService.buscarPorId(id);

        model.addAttribute("funcionario", funcionario);

        return "admin/funcionarios/detalhes";
    }

    // ==========================================
    // ABRIR FORMULÁRIO DE CADASTRO
    // ==========================================
    @GetMapping("/novo")
    public String novo(Model model) {

        model.addAttribute(
                "funcionario",
                new CadastroFuncionarioDTO()
        );

        model.addAttribute("cargos", Cargo.values());

        return "admin/funcionarios/cadastro";
    }

    // ==========================================
    // CADASTRAR FUNCIONÁRIO
    // ==========================================

    @PostMapping("/salvar")
    public String cadastrar(
            @ModelAttribute("funcionario") CadastroFuncionarioDTO form,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            funcionariosService.cadastrar(form);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Funcionário cadastrado com sucesso!"
            );

            return "redirect:/admin/funcionarios";

        } catch (IllegalArgumentException e) {

            model.addAttribute("erro", e.getMessage());

            return "admin/funcionarios/cadastro";
        }
    }

    // ==========================================
    // ABRIR FORMULÁRIO DE EDIÇÃO
    // ==========================================

    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model model) {

        FuncionariosModel funcionario =
                funcionariosService.buscarPorId(id);

        CadastroFuncionarioDTO form =
                new CadastroFuncionarioDTO();

        form.setNome(funcionario.getNome());
        form.setCpf(funcionario.getCpf());
        form.setTelefone(funcionario.getTelefone());
        form.setCargo(funcionario.getCargo());
        form.setDataAdmissao(funcionario.getDataAdmissao());
        System.out.println("Data no banco: " + funcionario.getDataAdmissao());
        System.out.println("Data no formulário: " + form.getDataAdmissao());
        form.setEmail(funcionario.getUsuario().getEmail());

        // A senha fica vazia para não alterar a senha atual
        form.setSenha("");

        model.addAttribute("funcionario", form);
        model.addAttribute("id", id);
        model.addAttribute("cargos", Cargo.values());

        return "admin/funcionarios/editar";
    }

    // ==========================================
    // SALVAR EDIÇÃO
    // ==========================================

    @PostMapping("/editar/{id}")
    public String atualizar(
            @PathVariable Long id,
            @ModelAttribute("funcionario") CadastroFuncionarioDTO form,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            funcionariosService.editar(id, form);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Funcionário atualizado com sucesso!"
            );

            return "redirect:/admin/funcionarios";

        } catch (IllegalArgumentException e) {

            model.addAttribute("erro", e.getMessage());
            model.addAttribute("id", id);

            return "admin/funcionarios/editar";
        }
    }

    // ==========================================
    // DESATIVAR FUNCIONÁRIO
    // ==========================================

    @PostMapping("/desativar/{id}")
    public String desativar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        funcionariosService.desativar(id);

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Funcionário desativado com sucesso!"
        );

        return "redirect:/admin/funcionarios";
    }

    // ==========================================
    // REATIVAR FUNCIONÁRIO
    // ==========================================

    @PostMapping("/reativar/{id}")
    public String reativar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        funcionariosService.reativar(id);

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Funcionário reativado com sucesso!"
        );

        return "redirect:/admin/funcionarios";
    }
}
