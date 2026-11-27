
package senai499.com.br.paoefelicidade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import senai499.com.br.paoefelicidade.model.CarrinhoModel;
import senai499.com.br.paoefelicidade.service.CarrinhoService;

@Controller
@RequestMapping("/cliente/carrinho")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    public CarrinhoController(CarrinhoService carrinhoService) {
        this.carrinhoService = carrinhoService;
    }

    @GetMapping
    public String visualizarCarrinho(
            HttpSession session,
            Model model) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        CarrinhoModel carrinho =
                carrinhoService.buscarOuCriarCarrinho(usuarioId);

        model.addAttribute("carrinho", carrinho);

        model.addAttribute("total",
                carrinhoService.calcularTotal(carrinho));

        // Calcula o subtotal de cada produto
        model.addAttribute("subtotais",
                carrinho.getItens().stream()
                        .collect(java.util.stream.Collectors.toMap(
                                item -> item.getId(),
                                item -> carrinhoService.calcularSubtotal(item)
                        )));

        return "cliente/carrinho";
    }

    @PostMapping("/adicionar/{produtoId}")
    public String adicionarProduto(
            @PathVariable Long produtoId,
            @RequestParam(defaultValue = "1") Integer quantidade,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        try {
            carrinhoService.adicionarProduto(
                    usuarioId, produtoId, quantidade);

            redirectAttributes.addFlashAttribute(
                    "sucesso", "Produto adicionado ao carrinho!");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute(
                    "erro", e.getMessage());
        }

        return "redirect:/cliente/carrinho";
    }

    @PostMapping("/atualizar/{itemId}")
    public String atualizarQuantidade(
            @PathVariable Long itemId,
            @RequestParam Integer quantidade,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        try {
            carrinhoService.atualizarQuantidade(
                    usuarioId, itemId, quantidade);

            redirectAttributes.addFlashAttribute(
                    "sucesso", "Quantidade atualizada!");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute(
                    "erro", e.getMessage());
        }

        return "redirect:/cliente/carrinho";
    }

    @PostMapping("/remover/{itemId}")
    public String removerProduto(
            @PathVariable Long itemId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        try {
            carrinhoService.removerProduto(usuarioId, itemId);

            redirectAttributes.addFlashAttribute(
                    "sucesso", "Produto removido do carrinho!");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute(
                    "erro", e.getMessage());
        }

        return "redirect:/cliente/carrinho";
    }
}
