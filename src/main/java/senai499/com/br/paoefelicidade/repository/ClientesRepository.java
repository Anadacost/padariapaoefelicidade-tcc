package senai499.com.br.paoefelicidade.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import senai499.com.br.paoefelicidade.model.ClientesModel;


@Repository
public interface ClientesRepository extends JpaRepository<ClientesModel, Long> {

    Optional<ClientesModel> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    Optional<ClientesModel> findByUsuario_Id(Long usuarioId);
}