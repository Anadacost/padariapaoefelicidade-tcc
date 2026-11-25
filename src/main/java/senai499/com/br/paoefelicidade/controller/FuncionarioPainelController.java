package senai499.com.br.paoefelicidade.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FuncionarioPainelController {

    @GetMapping("/funcionario/painel")
    public String painel() {
        return "funcionario/index";
    }
}