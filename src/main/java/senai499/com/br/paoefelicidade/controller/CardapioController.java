
package senai499.com.br.paoefelicidade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import senai499.com.br.paoefelicidade.service.ProdutosService;

@Controller
public class CardapioController {

    private final ProdutosService produtosService;

    public CardapioController(ProdutosService produtosService) {
        this.produtosService = produtosService;
    }

    @GetMapping("/cardapio")
    public String listarProdutos(Model model) {

        model.addAttribute(
                "produtos",
                produtosService.listarAtivos()
        );

        return "cardapio/index";
    }
}
