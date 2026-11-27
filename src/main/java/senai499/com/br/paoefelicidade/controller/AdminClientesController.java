package senai499.com.br.paoefelicidade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import senai499.com.br.paoefelicidade.dto.CadastroClienteDTO;
import senai499.com.br.paoefelicidade.model.ClientesModel;
import senai499.com.br.paoefelicidade.service.ClientesService;

@Controller
@RequestMapping("/admin/clientes")
public class AdminClientesController {

    private final ClientesService clientesService;

    public AdminClientesController(ClientesService clientesService) {
        this.clientesService = clientesService;
    }

    // ==========================================
    // LISTAR CLIENTES
    // ==========================================
    @GetMapping
    public String listar(
            @RequestParam(required = false) String busca,
            Model model) {

        model.addAttribute("clientes",
                clientesService.pesquisar(busca));

        model.addAttribute("busca", busca);

        return "admin/clientes/index";
    }

    // ==========================================
    // FORMULÁRIO DE CADASTRO
    // ==========================================
    @GetMapping("/novo")
    public String novo(Model model) {

        model.addAttribute("cliente", new CadastroClienteDTO());

        return "admin/clientes/cadastro";
    }

    // ==========================================
    // SALVAR NOVO CLIENTE
    // ==========================================
    @PostMapping("/salvar")
    public String cadastrar(
            @ModelAttribute("cliente") CadastroClienteDTO form,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            clientesService.cadastrarPeloSistema(form);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Cliente cadastrado com sucesso! Senha inicial: 123456789.");

            return "redirect:/admin/clientes";

        } catch (IllegalArgumentException e) {

            model.addAttribute("erro", e.getMessage());

            return "admin/clientes/cadastro";
        }
    }

    // ==========================================
    // ABRIR EDIÇÃO
    // ==========================================
    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model model) {

        ClientesModel cliente = clientesService.buscarPorId(id);

        CadastroClienteDTO form = new CadastroClienteDTO();

        form.setNome(cliente.getNome());
        form.setCpf(cliente.getCpf());
        form.setTelefone(cliente.getTelefone());
        form.setEmail(cliente.getUsuario().getEmail());

        model.addAttribute("cliente", form);
        model.addAttribute("idCliente", id);

        return "admin/clientes/editar";
    }

    // ==========================================
    // ATUALIZAR CLIENTE
    // ==========================================
    @PostMapping("/atualizar/{id}")
    public String atualizar(
            @PathVariable Long id,
            @ModelAttribute("cliente") CadastroClienteDTO form,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {

            clientesService.atualizar(id, form);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Cliente atualizado com sucesso!");

            return "redirect:/admin/clientes";

        } catch (IllegalArgumentException e) {

            model.addAttribute("erro", e.getMessage());
            model.addAttribute("idCliente", id);

            return "admin/clientes/editar";
        }
    }

    // ==========================================
    // INATIVAR / REATIVAR CLIENTE
    // ==========================================
    @PostMapping("/status/{id}")
    public String alterarStatus(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {

            clientesService.alterarStatus(id);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Status do cliente atualizado com sucesso!");

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage());
        }

        return "redirect:/admin/clientes";
    }
}