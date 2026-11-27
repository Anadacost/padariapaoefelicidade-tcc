
package senai499.com.br.paoefelicidade.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import senai499.com.br.paoefelicidade.model.CarrinhoModel;
import senai499.com.br.paoefelicidade.model.FormaPagamento;
import senai499.com.br.paoefelicidade.model.PedidosModel;
import senai499.com.br.paoefelicidade.model.TipoRecebimento;
import senai499.com.br.paoefelicidade.service.CarrinhoService;
import senai499.com.br.paoefelicidade.service.CheckoutService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cliente/checkout")
public class CheckoutController {

    private final CarrinhoService carrinhoService;
    private final CheckoutService checkoutService;

    public CheckoutController(
            CarrinhoService carrinhoService,
            CheckoutService checkoutService) {

        this.carrinhoService = carrinhoService;
        this.checkoutService = checkoutService;
    }

    // Exibe o checkout
    @GetMapping
    public String checkout(HttpSession session, Model model) {

        System.out.println(">>> ENTROU NO CHECKOUT CONTROLLER <<<");

        Long usuarioId = (Long) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        CarrinhoModel carrinho =
                carrinhoService.buscarOuCriarCarrinho(usuarioId);

        if (carrinho.getItens().isEmpty()) {
            return "redirect:/cliente/carrinho";
        }

        // Calcula o subtotal de cada item
        Map<Long, BigDecimal> subtotais =
                carrinho.getItens().stream()
                        .collect(Collectors.toMap(
                                item -> item.getId(),
                                item -> carrinhoService.calcularSubtotal(item)
                        ));

        model.addAttribute("carrinho", carrinho);
        model.addAttribute("subtotais", subtotais);

        model.addAttribute("total",
                carrinhoService.calcularTotal(carrinho));

        model.addAttribute("tiposRecebimento",
                TipoRecebimento.values());

        return "cliente/checkout";
    }

    // Guarda a forma de recebimento
    @PostMapping("/confirmar")
    public String continuarParaPagamento(
            @RequestParam TipoRecebimento tipoRecebimento,
            HttpSession session) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        session.setAttribute(
                "tipoRecebimentoEscolhido",
                tipoRecebimento
        );

        return "redirect:/cliente/checkout/pagamento";
    }

    // Exibe a tela de pagamento
    @GetMapping("/pagamento")
    public String pagamento(HttpSession session) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        TipoRecebimento tipoRecebimento =
                (TipoRecebimento) session.getAttribute(
                        "tipoRecebimentoEscolhido");

        if (tipoRecebimento == null) {
            return "redirect:/cliente/checkout";
        }

        CarrinhoModel carrinho =
                carrinhoService.buscarOuCriarCarrinho(usuarioId);

        if (carrinho.getItens().isEmpty()) {
            return "redirect:/cliente/carrinho";
        }

        return "cliente/pagamento";
    }

    // Registra o pedido com a forma de pagamento escolhida
    @PostMapping("/pagar")
    public String pagar(
            @RequestParam FormaPagamento formaPagamento,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        TipoRecebimento tipoRecebimento =
                (TipoRecebimento) session.getAttribute(
                        "tipoRecebimentoEscolhido");

        if (tipoRecebimento == null) {
            return "redirect:/cliente/checkout";
        }

        try {

            PedidosModel pedido = checkoutService.confirmarPedido(
                    usuarioId,
                    tipoRecebimento,
                    formaPagamento
            );

            session.removeAttribute("tipoRecebimentoEscolhido");

            redirectAttributes.addFlashAttribute(
                    "pedidoId",
                    pedido.getId()
            );

            return "redirect:/cliente/checkout/confirmado";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );

            return "redirect:/cliente/checkout/pagamento";
        }
    }

    // Exibe a confirmação
    @GetMapping("/confirmado")
    public String pedidoConfirmado() {
        return "cliente/confirmacao";
    }
}
