
package senai499.com.br.paoefelicidade.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import senai499.com.br.paoefelicidade.model.CarrinhoModel;
import senai499.com.br.paoefelicidade.model.ClientesModel;
import senai499.com.br.paoefelicidade.model.ItensCarrinhoModel;
import senai499.com.br.paoefelicidade.model.ProdutosModel;
import senai499.com.br.paoefelicidade.repository.CarrinhoRepository;
import senai499.com.br.paoefelicidade.repository.ClientesRepository;
import senai499.com.br.paoefelicidade.repository.ProdutosRepository;

@Service
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ClientesRepository clientesRepository;
    private final ProdutosRepository produtosRepository;

    public CarrinhoService(
            CarrinhoRepository carrinhoRepository,
            ClientesRepository clientesRepository,
            ProdutosRepository produtosRepository) {
        this.carrinhoRepository = carrinhoRepository;
        this.clientesRepository = clientesRepository;
        this.produtosRepository = produtosRepository;
    }

    @Transactional
    public CarrinhoModel buscarOuCriarCarrinho(Long usuarioId) {

        ClientesModel cliente = clientesRepository.findByUsuario_Id(usuarioId)
                .orElseThrow(() -> new RuntimeException(
                        "Cadastro de cliente não encontrado."));

        return carrinhoRepository.findByCliente_Id(cliente.getId())
                .orElseGet(() -> {
                    CarrinhoModel carrinho = new CarrinhoModel();
                    carrinho.setCliente(cliente);
                    return carrinhoRepository.save(carrinho);
                });
    }

    @Transactional
    public CarrinhoModel adicionarProduto(
            Long usuarioId, Long produtoId, Integer quantidade) {

        if (quantidade == null || quantidade < 1) {
            throw new IllegalArgumentException("Quantidade inválida.");
        }

        CarrinhoModel carrinho = buscarOuCriarCarrinho(usuarioId);

        ProdutosModel produto = produtosRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException(
                        "Produto não encontrado."));

        if (!produto.isAtivo()) {
            throw new IllegalArgumentException(
                    "Este produto não está disponível.");
        }

        ItensCarrinhoModel item = carrinho.getItens().stream()
                .filter(i -> i.getProduto().getId().equals(produtoId))
                .findFirst()
                .orElse(null);

        if (item == null) {
            item = new ItensCarrinhoModel();
            item.setCarrinho(carrinho);
            item.setProduto(produto);
            item.setQuantidade(quantidade);
            carrinho.getItens().add(item);
        } else {
            item.setQuantidade(item.getQuantidade() + quantidade);
        }

        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public CarrinhoModel atualizarQuantidade(
            Long usuarioId, Long itemId, Integer quantidade) {

        if (quantidade == null || quantidade < 1) {
            throw new IllegalArgumentException("Quantidade inválida.");
        }

        CarrinhoModel carrinho = buscarOuCriarCarrinho(usuarioId);

        ItensCarrinhoModel item = carrinho.getItens().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Item não encontrado no carrinho."));

        item.setQuantidade(quantidade);

        return carrinhoRepository.save(carrinho);
    }

    @Transactional
    public CarrinhoModel removerProduto(Long usuarioId, Long itemId) {

        CarrinhoModel carrinho = buscarOuCriarCarrinho(usuarioId);

        boolean removido = carrinho.getItens()
                .removeIf(item -> item.getId().equals(itemId));

        if (!removido) {
            throw new RuntimeException("Item não encontrado no carrinho.");
        }

        return carrinhoRepository.save(carrinho);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularSubtotal(ItensCarrinhoModel item) {
        return item.getProduto().getPreco()
                .multiply(BigDecimal.valueOf(item.getQuantidade()));
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotal(CarrinhoModel carrinho) {
        return carrinho.getItens().stream()
                .map(this::calcularSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
