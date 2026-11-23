package br.com.fraudguard.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes", uniqueConstraints = {
    @UniqueConstraint(name = "uk_cliente_email", columnNames = "email"),
    @UniqueConstraint(name = "uk_cliente_documento", columnNames = "documento")
})
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 180)
    private String email;

    @Column(nullable = false, length = 20)
    private String documento;

    // Associação 1:N - um cliente possui várias transações.
    @OneToMany(mappedBy = "cliente")
    private List<Transacao> transacoes = new ArrayList<>();

    protected Cliente() {}
    public Cliente(String nome, String email, String documento) {
        this.nome = nome;
        this.email = email;
        this.documento = documento;
    }
    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getDocumento() { return documento; }
    public List<Transacao> getTransacoes() { return transacoes; }
    public void atualizar(String nome, String email, String documento) {
        this.nome = nome; this.email = email; this.documento = documento;
    }
}
