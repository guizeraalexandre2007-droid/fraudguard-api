package br.com.fraudguard.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

// Herança JPA: tabela de base + tabelas próprias para PIX e cartão.
@Entity
@Table(name = "transacoes")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Transacao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusTransacao status;

    // Lado N da associação 1:N: cada transação pertence a um cliente.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Associação N:N: uma transação pode acionar várias regras, e vice-versa.
    @ManyToMany
    @JoinTable(name = "transacao_regras",
        joinColumns = @JoinColumn(name = "transacao_id"),
        inverseJoinColumns = @JoinColumn(name = "regra_id"),
        uniqueConstraints = @UniqueConstraint(name = "uk_transacao_regra", columnNames = {"transacao_id", "regra_id"}))
    private Set<RegraAntifraude> regras = new HashSet<>();

    protected Transacao() {}
    protected Transacao(BigDecimal valor, StatusTransacao status, Cliente cliente, Set<RegraAntifraude> regras) {
        this.dataHora = LocalDateTime.now();
        atualizar(valor, status, cliente, regras);
    }
    public void atualizar(BigDecimal valor, StatusTransacao status, Cliente cliente, Set<RegraAntifraude> regras) {
        this.valor = valor; this.status = status; this.cliente = cliente;
        this.regras.clear(); this.regras.addAll(regras);
    }
    public Long getId() { return id; }
    public BigDecimal getValor() { return valor; }
    public LocalDateTime getDataHora() { return dataHora; }
    public StatusTransacao getStatus() { return status; }
    public Cliente getCliente() { return cliente; }
    public Set<RegraAntifraude> getRegras() { return regras; }
}
