package senai499.com.br.paoefelicidade.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import senai499.com.br.paoefelicidade.model.CarrinhoModel;

@Repository
public interface CarrinhoRepository extends JpaRepository<CarrinhoModel, Long> {

    Optional<CarrinhoModel> findByCliente_Id(Long clienteId);
}