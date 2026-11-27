
package senai499.com.br.paoefelicidade.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "itens_carrinho",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_carrinho_produto",
                        columnNames = {"carrinho_id", "produto_id"}
                )
        }
)
public class ItensCarrinhoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrinho_id", nullable = false)
    private CarrinhoModel carrinho;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutosModel produto;

    @Column(nullable = false)
    private Integer quantidade = 1;

    public ItensCarrinhoModel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CarrinhoModel getCarrinho() {
        return carrinho;
    }

    public void setCarrinho(CarrinhoModel carrinho) {
        this.carrinho = carrinho;
    }

    public ProdutosModel getProduto() {
        return produto;
    }

    public void setProduto(ProdutosModel produto) {
        this.produto = produto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
