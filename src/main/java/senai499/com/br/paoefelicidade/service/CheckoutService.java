
package senai499.com.br.paoefelicidade.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import senai499.com.br.paoefelicidade.model.CarrinhoModel;
import senai499.com.br.paoefelicidade.model.ClientesModel;
import senai499.com.br.paoefelicidade.model.FormaPagamento;
import senai499.com.br.paoefelicidade.model.ItensCarrinhoModel;
import senai499.com.br.paoefelicidade.model.ItensPedidoModel;
import senai499.com.br.paoefelicidade.model.PedidosModel;
import senai499.com.br.paoefelicidade.model.ProdutosModel;
import senai499.com.br.paoefelicidade.model.StatusPedidos;
import senai499.com.br.paoefelicidade.model.TipoRecebimento;
import senai499.com.br.paoefelicidade.repository.CarrinhoRepository;
import senai499.com.br.paoefelicidade.repository.ClientesRepository;
import senai499.com.br.paoefelicidade.repository.PedidosRepository;
import senai499.com.br.paoefelicidade.repository.ProdutosRepository;

@Service
public class CheckoutService {

    private final CarrinhoRepository carrinhoRepository;
    private final ClientesRepository clientesRepository;
    private final PedidosRepository pedidosRepository;
    private final ProdutosRepository produtosRepository;

    public CheckoutService(
            CarrinhoRepository carrinhoRepository,
            ClientesRepository clientesRepository,
            PedidosRepository pedidosRepository,
            ProdutosRepository produtosRepository) {

        this.carrinhoRepository = carrinhoRepository;
        this.clientesRepository = clientesRepository;
        this.pedidosRepository = pedidosRepository;
        this.produtosRepository = produtosRepository;
    }

    @Transactional
    public PedidosModel confirmarPedido(
            Long usuarioId,
            TipoRecebimento tipoRecebimento,
            FormaPagamento formaPagamento) {

        if (tipoRecebimento == null) {
            throw new IllegalArgumentException(
                    "Selecione uma forma de recebimento.");
        }

        if (formaPagamento == null) {
            throw new IllegalArgumentException(
                    "Selecione uma forma de pagamento.");
        }

        ClientesModel cliente = clientesRepository
                .findByUsuario_Id(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cadastro de cliente não encontrado."));

        CarrinhoModel carrinho = carrinhoRepository
                .findByCliente_Id(cliente.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Carrinho não encontrado."));

        if (carrinho.getItens().isEmpty()) {
            throw new IllegalArgumentException(
                    "Seu carrinho está vazio.");
        }

        PedidosModel pedido = new PedidosModel();

        pedido.setCliente(cliente);
        pedido.setTipoRecebimento(tipoRecebimento);
        pedido.setFormaPagamento(formaPagamento);
        pedido.setStatus(StatusPedidos.PENDENTE);

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (ItensCarrinhoModel itemCarrinho : carrinho.getItens()) {

            ProdutosModel produto = produtosRepository
                    .findById(itemCarrinho.getProduto().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Um produto do carrinho não foi encontrado."));

            Integer quantidade = itemCarrinho.getQuantidade();

            if (!produto.isAtivo()) {
                throw new IllegalArgumentException(
                        "O produto " + produto.getNome()
                                + " não está mais disponível.");
            }

            if (produto.getQuantidadeEstoque() == null
                    || produto.getQuantidadeEstoque() < quantidade) {

                throw new IllegalArgumentException(
                        "Estoque insuficiente para o produto: "
                                + produto.getNome());
            }

            BigDecimal precoUnitario = produto.getPreco();

            BigDecimal subtotal = precoUnitario.multiply(
                    BigDecimal.valueOf(quantidade));

            ItensPedidoModel itemPedido = new ItensPedidoModel();

            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(quantidade);
            itemPedido.setPrecoUnitario(precoUnitario);
            itemPedido.setSubtotal(subtotal);

            pedido.getItens().add(itemPedido);

            valorTotal = valorTotal.add(subtotal);

            // Baixa do estoque
            produto.setQuantidadeEstoque(
                    produto.getQuantidadeEstoque() - quantidade);

            produtosRepository.save(produto);
        }

        pedido.setValorTotal(valorTotal);

        // Salva o pedido e seus itens relacionados
        PedidosModel pedidoSalvo = pedidosRepository.save(pedido);

        // Esvazia o carrinho após a confirmação
        carrinho.getItens().clear();
        carrinhoRepository.save(carrinho);

        return pedidoSalvo;
    }
}
