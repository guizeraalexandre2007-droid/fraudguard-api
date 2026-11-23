package br.com.fraudguard.api;

import br.com.fraudguard.domain.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public final class Dtos {
    private Dtos() {}

    public record ClienteRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Email @Size(max = 180) String email,
        @NotBlank @Pattern(regexp = "[0-9]{11}|[0-9]{14}", message = "deve ter 11 ou 14 digitos, somente numeros") String documento
    ) {}

    public record ClienteResponse(Long id, String nome, String email, String documento) {
        public static ClienteResponse of(Cliente c) {
            return new ClienteResponse(c.getId(), c.getNome(), c.getEmail(), c.getDocumento());
        }
    }

    public record RegraRequest(
        @NotBlank @Size(max = 120) String nome,
        @NotBlank @Size(max = 500) String descricao,
        @NotNull @DecimalMin("0.00") BigDecimal limiteValor,
        @NotNull Boolean ativa
    ) {}

    public record RegraResponse(Long id, String nome, String descricao, BigDecimal limiteValor, boolean ativa) {
        public static RegraResponse of(RegraAntifraude r) {
            return new RegraResponse(r.getId(), r.getNome(), r.getDescricao(), r.getLimiteValor(), r.isAtiva());
        }
    }

    public interface DadosTransacao {
        BigDecimal valor();
        StatusTransacao status();
        Long clienteId();
        Set<Long> regraIds();
    }

    public record PixRequest(
        @NotNull @DecimalMin("0.01") BigDecimal valor,
        @NotNull StatusTransacao status,
        @NotNull Long clienteId,
        @NotNull Set<@NotNull Long> regraIds,
        @NotBlank @Size(max = 120) String chavePix,
        @NotBlank @Size(max = 100) String bancoDestino
    ) implements DadosTransacao {}

    public record CartaoRequest(
        @NotNull @DecimalMin("0.01") BigDecimal valor,
        @NotNull StatusTransacao status,
        @NotNull Long clienteId,
        @NotNull Set<@NotNull Long> regraIds,
        @NotBlank @Pattern(regexp = "[0-9]{4}", message = "deve conter exatamente quatro digitos") String ultimosQuatroDigitos,
        @NotBlank @Size(max = 50) String bandeira
    ) implements DadosTransacao {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record TransacaoResponse(
        Long id,
        String tipo,
        BigDecimal valor,
        LocalDateTime dataHora,
        StatusTransacao status,
        Long clienteId,
        Set<Long> regraIds,
        String chavePix,
        String bancoDestino,
        String ultimosQuatroDigitos,
        String bandeira
    ) {
        public static TransacaoResponse of(Transacao t) {
            Set<Long> ids = t.getRegras().stream().map(RegraAntifraude::getId)
                .sorted().collect(Collectors.toCollection(java.util.LinkedHashSet::new));
            if (t instanceof TransacaoPix pix) {
                return new TransacaoResponse(t.getId(), "PIX", t.getValor(), t.getDataHora(),
                    t.getStatus(), t.getCliente().getId(), ids, pix.getChavePix(), pix.getBancoDestino(), null, null);
            }
            if (t instanceof TransacaoCartao cartao) {
                return new TransacaoResponse(t.getId(), "CARTAO", t.getValor(), t.getDataHora(),
                    t.getStatus(), t.getCliente().getId(), ids, null, null,
                    cartao.getUltimosQuatroDigitos(), cartao.getBandeira());
            }
            throw new IllegalStateException("Tipo de transacao nao suportado");
        }
    }
}
