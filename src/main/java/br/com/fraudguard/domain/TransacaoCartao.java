package br.com.fraudguard.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "transacoes_cartao")
@PrimaryKeyJoinColumn(name = "id")
public class TransacaoCartao extends Transacao {
    @Column(nullable = false, length = 4)
    private String ultimosQuatroDigitos;
    @Column(nullable = false, length = 50)
    private String bandeira;

    protected TransacaoCartao() {}
    public TransacaoCartao(BigDecimal valor, StatusTransacao status, Cliente cliente,
                           Set<RegraAntifraude> regras, String ultimosQuatroDigitos, String bandeira) {
        super(valor, status, cliente, regras);
        atualizarDadosCartao(ultimosQuatroDigitos, bandeira);
    }
    public void atualizarDadosCartao(String ultimosQuatroDigitos, String bandeira) {
        this.ultimosQuatroDigitos = ultimosQuatroDigitos; this.bandeira = bandeira;
    }
    public String getUltimosQuatroDigitos() { return ultimosQuatroDigitos; }
    public String getBandeira() { return bandeira; }
}
