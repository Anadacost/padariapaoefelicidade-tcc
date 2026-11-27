package senai499.com.br.paoefelicidade.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import senai499.com.br.paoefelicidade.model.ItensCarrinhoModel;

@Repository
public interface ItensCarrinhoRepository extends JpaRepository<ItensCarrinhoModel, Long> {

    Optional<ItensCarrinhoModel> findByCarrinho_IdAndProduto_Id(
            Long carrinhoId,
            Long produtoId
    );
}