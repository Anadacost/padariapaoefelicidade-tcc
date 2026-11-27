
package senai499.com.br.paoefelicidade.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import senai499.com.br.paoefelicidade.model.ClientesModel;
import senai499.com.br.paoefelicidade.repository.ClientesRepository;
import senai499.com.br.paoefelicidade.repository.PedidosRepository;

@Controller
@RequestMapping("/cliente/pedidos")
public class PedidosController {

    private final PedidosRepository pedidosRepository;
    private final ClientesRepository clientesRepository;

    public PedidosController(
            PedidosRepository pedidosRepository,
            ClientesRepository clientesRepository) {

        this.pedidosRepository = pedidosRepository;
        this.clientesRepository = clientesRepository;
    }

    @GetMapping
    public String listarPedidos(HttpSession session, Model model) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return "redirect:/login";
        }

        ClientesModel cliente = clientesRepository
                .findByUsuario_Id(usuarioId)
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado.")
                );

        model.addAttribute(
                "pedidos",
                pedidosRepository.findByCliente_IdOrderByDataPedidoDesc(
                        cliente.getId()
                )
        );

        return "cliente/pedidos";
    }
}
