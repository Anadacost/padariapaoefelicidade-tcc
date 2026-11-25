
package senai499.com.br.paoefelicidade.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import senai499.com.br.paoefelicidade.model.ProdutosModel;
import senai499.com.br.paoefelicidade.repository.ProdutosRepository;

@Service
public class ProdutosService {

    private final ProdutosRepository produtosRepository;

    public ProdutosService(ProdutosRepository produtosRepository) {
        this.produtosRepository = produtosRepository;
    }

    // ==========================================
    // CADASTRAR PRODUTO
    // ==========================================

    @Transactional
    public ProdutosModel cadastrar(ProdutosModel produto) {

        validarProduto(produto);

        if (produtosRepository.existsByNomeIgnoreCase(produto.getNome().trim())) {
            throw new IllegalArgumentException(
                    "Já existe um produto cadastrado com esse nome.");
        }

        produto.setNome(produto.getNome().trim());
        produto.setAtivo(true);

        return produtosRepository.save(produto);
    }

    // ==========================================
    // LISTAR TODOS OS PRODUTOS
    // ==========================================

    public List<ProdutosModel> listarTodos() {
        return produtosRepository.findAllByOrderByNomeAsc();
    }

    // ==========================================
    // LISTAR PRODUTOS ATIVOS
    // ==========================================

    public List<ProdutosModel> listarAtivos() {
        return produtosRepository.findByAtivoTrueOrderByNomeAsc();
    }

    // ==========================================
    // LISTAR PRODUTOS INATIVOS
    // ==========================================

    public List<ProdutosModel> listarInativos() {
        return produtosRepository.findByAtivoFalseOrderByNomeAsc();
    }

    // ==========================================
    // BUSCAR PRODUTO POR ID
    // ==========================================

    public ProdutosModel buscarPorId(Long id) {
        return produtosRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Produto não encontrado."));
    }

    // ==========================================
    // PESQUISAR PRODUTOS
    // ==========================================

    public List<ProdutosModel> pesquisar(String termo) {

        if (termo == null || termo.trim().isEmpty()) {
            return listarTodos();
        }

        String pesquisa = termo.trim();

        return produtosRepository
                .findByNomeContainingIgnoreCaseOrCategoriaContainingIgnoreCaseOrderByNomeAsc(
                        pesquisa, pesquisa);
    }

    // ==========================================
    // EDITAR PRODUTO
    // ==========================================

    @Transactional
    public ProdutosModel editar(Long id, ProdutosModel dados) {

        ProdutosModel produto = buscarPorId(id);

        validarProduto(dados);

        String nome = dados.getNome().trim();

        if (produtosRepository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new IllegalArgumentException(
                    "Já existe outro produto cadastrado com esse nome.");
        }

        produto.setNome(nome);
        produto.setDescricao(dados.getDescricao());
        produto.setPreco(dados.getPreco());
        produto.setCategoria(dados.getCategoria());
        produto.setQuantidadeEstoque(dados.getQuantidadeEstoque());
        produto.setImagem(dados.getImagem());
        produto.setAtivo(dados.isAtivo());

        return produtosRepository.save(produto);
    }

    // ==========================================
    // INATIVAR PRODUTO
    // ==========================================

    @Transactional
    public void inativar(Long id) {

        ProdutosModel produto = buscarPorId(id);

        if (!produto.isAtivo()) {
            throw new IllegalArgumentException(
                    "Este produto já está inativo.");
        }

        produto.setAtivo(false);

        produtosRepository.save(produto);
    }

    // ==========================================
    // ATIVAR PRODUTO
    // ==========================================

    @Transactional
    public void ativar(Long id) {

        ProdutosModel produto = buscarPorId(id);

        if (produto.isAtivo()) {
            throw new IllegalArgumentException(
                    "Este produto já está ativo.");
        }

        produto.setAtivo(true);

        produtosRepository.save(produto);
    }

    // ==========================================
    // VALIDAÇÕES
    // ==========================================

    private void validarProduto(ProdutosModel produto) {

        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "O nome do produto é obrigatório.");
        }

        if (produto.getPreco() == null ||
                produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "O preço deve ser maior que zero.");
        }

        if (produto.getCategoria() == null ||
                produto.getCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "A categoria do produto é obrigatória.");
        }

        if (produto.getQuantidadeEstoque() == null ||
                produto.getQuantidadeEstoque() < 0) {
            throw new IllegalArgumentException(
                    "A quantidade em estoque não pode ser negativa.");
        }
    }
}
