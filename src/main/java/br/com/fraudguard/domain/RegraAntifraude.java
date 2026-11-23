package br.com.fraudguard.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "regras_antifraude")
public class RegraAntifraude {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String nome;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal limiteValor;

    @Column(nullable = false)
    private boolean ativa;

    // Lado inverso da associação N:N com transações.
    @ManyToMany(mappedBy = "regras")
    private Set<Transacao> transacoes = new HashSet<>();

    protected RegraAntifraude() {}
    public RegraAntifraude(String nome, String descricao, BigDecimal limiteValor, boolean ativa) {
        this.nome = nome; this.descricao = descricao; this.limiteValor = limiteValor; this.ativa = ativa;
    }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public BigDecimal getLimiteValor() { return limiteValor; }
    public boolean isAtiva() { return ativa; }
    public Set<Transacao> getTransacoes() { return transacoes; }
    public void atualizar(String nome, String descricao, BigDecimal limiteValor, boolean ativa) {
        this.nome = nome; this.descricao = descricao; this.limiteValor = limiteValor; this.ativa = ativa;
    }
}
