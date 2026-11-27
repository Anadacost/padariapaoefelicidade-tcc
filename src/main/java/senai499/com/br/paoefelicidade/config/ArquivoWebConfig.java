
package senai499.com.br.paoefelicidade.config;

import java.nio.file.Path;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ArquivoWebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String caminho = Path.of("uploads", "produtos")
                .toAbsolutePath()
                .normalize()
                .toUri()
                .toString();

        if (!caminho.endsWith("/")) {
            caminho += "/";
        }

        registry.addResourceHandler("/uploads/produtos/**")
                .addResourceLocations(caminho);
    }
}
