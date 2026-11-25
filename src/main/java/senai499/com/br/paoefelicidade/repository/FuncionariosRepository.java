package senai499.com.br.paoefelicidade.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import senai499.com.br.paoefelicidade.model.FuncionariosModel;

public interface FuncionariosRepository
        extends JpaRepository<FuncionariosModel, Long> {

    Optional<FuncionariosModel> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    List<FuncionariosModel> findAllByOrderByNomeAsc();

    Optional<FuncionariosModel> findByUsuario_Id(Long usuarioId);

    @Query("""
        SELECT f FROM FuncionariosModel f
        WHERE LOWER(f.nome) LIKE LOWER(CONCAT('%', :termo, '%'))
           OR f.cpf LIKE CONCAT('%', :termo, '%')
           OR LOWER(f.cargo) LIKE LOWER(CONCAT('%', :termo, '%'))
        ORDER BY f.nome ASC
    """)
    List<FuncionariosModel> pesquisar(@Param("termo") String termo);
}