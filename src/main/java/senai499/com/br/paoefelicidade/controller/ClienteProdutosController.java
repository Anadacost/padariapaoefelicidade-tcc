
package senai499.com.br.paoefelicidade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import senai499.com.br.paoefelicidade.service.ProdutosService;

@Controller
public class ClienteProdutosController {

    private final ProdutosService produtosService;

    public ClienteProdutosController(ProdutosService produtosService) {
        this.produtosService = produtosService;
    }

    // ==========================================
    // CATÁLOGO DE PRODUTOS DO CLIENTE
    // ==========================================

    @GetMapping("/cliente/produtos")
    public String catalogo(Model model) {

        model.addAttribute(
                "produtos",
                produtosService.listarAtivos()
        );

        return "cliente/catalogo";
    }
}
