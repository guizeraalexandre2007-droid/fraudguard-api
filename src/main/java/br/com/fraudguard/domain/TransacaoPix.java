package br.com.fraudguard.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "transacoes_pix")
@PrimaryKeyJoinColumn(name = "id")
public class TransacaoPix extends Transacao {
    @Column(nullable = false, length = 120)
    private String chavePix;
    @Column(nullable = false, length = 100)
    private String bancoDestino;

    protected TransacaoPix() {}
    public TransacaoPix(BigDecimal valor, StatusTransacao status, Cliente cliente,
                        Set<RegraAntifraude> regras, String chavePix, String bancoDestino) {
        super(valor, status, cliente, regras);
        atualizarDadosPix(chavePix, bancoDestino);
    }
    public void atualizarDadosPix(String chavePix, String bancoDestino) {
        this.chavePix = chavePix; this.bancoDestino = bancoDestino;
    }
    public String getChavePix() { return chavePix; }
    public String getBancoDestino() { return bancoDestino; }
}
