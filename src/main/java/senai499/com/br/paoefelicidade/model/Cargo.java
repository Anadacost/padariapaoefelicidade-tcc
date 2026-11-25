package senai499.com.br.paoefelicidade.model;

public enum Cargo {

    ATENDENTE("Atendente"),
    PADEIRO("Padeiro"),
    CONFEITEIRO("Confeiteiro"),
    CAIXA("Caixa"),
    GERENTE("Gerente"),
    AUXILIAR_PRODUCAO("Auxiliar de produção"),
    ENTREGADOR("Entregador");

    private final String descricao;

    Cargo(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}