
package senai499.com.br.paoefelicidade.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import senai499.com.br.paoefelicidade.model.ItensPedidoModel;

@Repository
public interface ItensPedidoRepository extends JpaRepository<ItensPedidoModel, Long> {

    List<ItensPedidoModel> findByPedido_Id(Long pedidoId);
}
