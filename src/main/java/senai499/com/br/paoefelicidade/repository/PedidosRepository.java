
package senai499.com.br.paoefelicidade.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import senai499.com.br.paoefelicidade.model.PedidosModel;

@Repository
public interface PedidosRepository extends JpaRepository<PedidosModel, Long> {

    @EntityGraph(attributePaths = {
            "itens",
            "itens.produto"
    })
    List<PedidosModel> findByCliente_IdOrderByDataPedidoDesc(Long clienteId);

}
