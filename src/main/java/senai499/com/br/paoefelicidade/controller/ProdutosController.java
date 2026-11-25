
package senai499.com.br.paoefelicidade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import senai499.com.br.paoefelicidade.model.ProdutosModel;
import senai499.com.br.paoefelicidade.service.ProdutosService;
import org.springframework.web.multipart.MultipartFile;
import senai499.com.br.paoefelicidade.service.ArquivoUploadService;

@Controller
public class ProdutosController {

    private final ProdutosService produtosService;
    private final ArquivoUploadService arquivoUploadService;

    public ProdutosController(
            ProdutosService produtosService,
            ArquivoUploadService arquivoUploadService) {

        this.produtosService = produtosService;
        this.arquivoUploadService = arquivoUploadService;
    }

    // ==========================================
    // LISTAR PRODUTOS
    // ==========================================

    @GetMapping("/produtos")
    public String listar(
            @RequestParam(required = false) String busca,
            Model model) {

        model.addAttribute("produtos", produtosService.pesquisar(busca));
        model.addAttribute("busca", busca);

        return "produtos/index";
    }

    // ==========================================
    // ABRIR CADASTRO
    // ==========================================

    @GetMapping("/produtos/cadastro")
    public String cadastro(Model model) {

        model.addAttribute("produto", new ProdutosModel());

        return "produtos/cadastro";
    }

    // ==========================================
    // SALVAR PRODUTO
    // ==========================================
    @PostMapping("/produtos/salvar")
    public String salvar(
            @ModelAttribute("produto") ProdutosModel produto,
            @RequestParam("arquivoImagem") MultipartFile arquivoImagem,
            RedirectAttributes redirectAttributes) {

        try {

            String caminhoImagem =
                    arquivoUploadService.salvarImagem(arquivoImagem);

            if (caminhoImagem != null) {
                produto.setImagem(caminhoImagem);
            }

            produtosService.cadastrar(produto);

            redirectAttributes.addFlashAttribute(
                    "sucesso", "Produto cadastrado com sucesso!");

            return "redirect:/produtos";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("erro", e.getMessage());

            return "redirect:/produtos/cadastro";
        }
    }

    // ==========================================
    // DETALHES DO PRODUTO
    // ==========================================

    @GetMapping("/produtos/detalhes/{id}")
    public String detalhes(
            @PathVariable Long id,
            Model model) {

        model.addAttribute("produto", produtosService.buscarPorId(id));

        return "produtos/detalhes";
    }

    // ==========================================
    // ABRIR EDIÇÃO
    // ==========================================

    @GetMapping("/produtos/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model model) {

        model.addAttribute("produto", produtosService.buscarPorId(id));

        return "produtos/editar";
    }

    // ==========================================
    // ATUALIZAR PRODUTO
    // ==========================================

    @PostMapping("/produtos/atualizar/{id}")
    public String atualizar(
            @PathVariable Long id,
            @ModelAttribute("produto") ProdutosModel dados,
            @RequestParam("arquivoImagem") MultipartFile arquivoImagem,
            RedirectAttributes redirectAttributes) {

        try {

            // Busca o produto que já existe no banco
            ProdutosModel produtoAtual = produtosService.buscarPorId(id);

            // Mantém a imagem antiga caso nenhuma nova seja enviada
            if (arquivoImagem != null && !arquivoImagem.isEmpty()) {

                String caminhoImagem =
                        arquivoUploadService.salvarImagem(arquivoImagem);

                dados.setImagem(caminhoImagem);

            } else {

                dados.setImagem(produtoAtual.getImagem());
            }

            // Atualiza os dados do produto
            produtosService.editar(id, dados);

            redirectAttributes.addFlashAttribute(
                    "sucesso", "Produto atualizado com sucesso!");

            return "redirect:/produtos";

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("erro", e.getMessage());

            return "redirect:/produtos/editar/" + id;
        }
    }

    // ==========================================
    // INATIVAR PRODUTO
    // ==========================================

    @PostMapping("/produtos/inativar/{id}")
    public String inativar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            produtosService.inativar(id);

            redirectAttributes.addFlashAttribute(
                    "sucesso", "Produto inativado com sucesso!");

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/produtos";
    }

    // ==========================================
    // ATIVAR PRODUTO
    // ==========================================

    @PostMapping("/produtos/ativar/{id}")
    public String ativar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            produtosService.ativar(id);

            redirectAttributes.addFlashAttribute(
                    "sucesso", "Produto ativado com sucesso!");

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/produtos";
    }
}
