
package senai499.com.br.paoefelicidade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import senai499.com.br.paoefelicidade.model.PedidosModel;

@Repository
public interface PedidosRepository extends JpaRepository<PedidosModel, Long> {
}
