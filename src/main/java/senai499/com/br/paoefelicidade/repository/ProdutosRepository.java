
package senai499.com.br.paoefelicidade.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import senai499.com.br.paoefelicidade.model.ProdutosModel;

@Repository
public interface ProdutosRepository extends JpaRepository<ProdutosModel, Long> {

    // Buscar produto pelo nome
    Optional<ProdutosModel> findByNomeIgnoreCase(String nome);

    // Verificar se já existe um produto com esse nome
    boolean existsByNomeIgnoreCase(String nome);

    // Verificar nome duplicado ao editar, ignorando o próprio produto
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    // Listar todos os produtos
    List<ProdutosModel> findAllByOrderByNomeAsc();

    // Listar somente produtos ativos
    List<ProdutosModel> findByAtivoTrueOrderByNomeAsc();

    // Listar somente produtos inativos
    List<ProdutosModel> findByAtivoFalseOrderByNomeAsc();

    // Pesquisar por nome ou categoria
    List<ProdutosModel> findByNomeContainingIgnoreCaseOrCategoriaContainingIgnoreCaseOrderByNomeAsc(
            String nome, String categoria);
}
