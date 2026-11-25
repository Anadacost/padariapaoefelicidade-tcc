
package senai499.com.br.paoefelicidade.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ArquivoUploadService {

    private final Path pasta = Path.of("uploads", "produtos")
            .toAbsolutePath().normalize();

    public String salvarImagem(MultipartFile arquivo) {

        if (arquivo == null || arquivo.isEmpty()) {
            return null;
        }

        String tipo = arquivo.getContentType();

        if (tipo == null ||
                !(tipo.equals("image/jpeg")
                        || tipo.equals("image/png")
                        || tipo.equals("image/webp"))) {
            throw new IllegalArgumentException(
                    "Selecione uma imagem JPG, PNG ou WEBP.");
        }

        if (arquivo.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException(
                    "A imagem deve ter no máximo 10 MB.");
        }

        try {
            Files.createDirectories(pasta);

            String extensao = switch (tipo) {
                case "image/jpeg" -> ".jpg";
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                default -> throw new IllegalArgumentException(
                        "Formato de imagem inválido.");
            };

            String nomeArquivo = UUID.randomUUID() + extensao;

            Path destino = pasta.resolve(nomeArquivo).normalize();

            if (!destino.startsWith(pasta)) {
                throw new IllegalArgumentException(
                        "Nome de arquivo inválido.");
            }

            arquivo.transferTo(destino);

            return "/uploads/produtos/" + nomeArquivo;

        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Não foi possível salvar a imagem.");
        }
    }
}
