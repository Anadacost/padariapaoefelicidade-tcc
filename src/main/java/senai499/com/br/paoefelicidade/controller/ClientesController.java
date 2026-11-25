
package senai499.com.br.paoefelicidade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import senai499.com.br.paoefelicidade.dto.CadastroClienteDTO;
import senai499.com.br.paoefelicidade.service.ClientesService;

@Controller
public class ClientesController {

    private final ClientesService clientesService;

    public ClientesController(ClientesService clientesService) {
        this.clientesService = clientesService;
    }

    // TELA DE CADASTRO
    @GetMapping("/cliente/cadastro")
    public String cadastro(Model model) {

        System.out.println("ENTROU NO CADASTRO DE CLIENTE!");

        model.addAttribute("cliente", new CadastroClienteDTO());

        return "cliente/cadastro";
    }

    // SALVAR CADASTRO
    @PostMapping("/cliente/cadastro")
    public String cadastrar(
            @ModelAttribute("cliente") CadastroClienteDTO form,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {

            clientesService.cadastrar(form);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Cadastro realizado com sucesso! Faça login para continuar.");

            return "redirect:/login";

        } catch (IllegalArgumentException e) {

            model.addAttribute("erro", e.getMessage());

            return "cliente/cadastro";
        }
    }
}
