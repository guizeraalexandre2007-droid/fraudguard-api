package br.com.fraudguard.service;

import br.com.fraudguard.api.Dtos.RegraRequest;
import br.com.fraudguard.api.Dtos.RegraResponse;
import br.com.fraudguard.domain.RegraAntifraude;
import br.com.fraudguard.domain.Transacao;
import br.com.fraudguard.repository.RegraAntifraudeRepository;
import br.com.fraudguard.repository.TransacaoRepository;
import br.com.fraudguard.service.Exceptions.NaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RegraService {
    private final RegraAntifraudeRepository regras;
    private final TransacaoRepository transacoes;

    public RegraService(RegraAntifraudeRepository regras, TransacaoRepository transacoes) {
        this.regras = regras; this.transacoes = transacoes;
    }
    public List<RegraResponse> listar() { return regras.findAll().stream().map(RegraResponse::of).toList(); }
    public RegraResponse buscar(Long id) { return RegraResponse.of(obter(id)); }
    public RegraAntifraude obter(Long id) {
        return regras.findById(id).orElseThrow(() -> new NaoEncontradoException("Regra " + id + " nao encontrada"));
    }
    @Transactional
    public RegraResponse criar(RegraRequest req) {
        return RegraResponse.of(regras.saveAndFlush(
            new RegraAntifraude(req.nome(), req.descricao(), req.limiteValor(), req.ativa())));
    }
    @Transactional
    public RegraResponse atualizar(Long id, RegraRequest req) {
        RegraAntifraude r = obter(id);
        r.atualizar(req.nome(), req.descricao(), req.limiteValor(), req.ativa());
        return RegraResponse.of(regras.saveAndFlush(r));
    }
    @Transactional
    public void excluir(Long id) {
        RegraAntifraude r = obter(id);
        // Remove associações na tabela intermediária antes de excluir a regra.
        for (Transacao t : transacoes.findDistinctByRegras_Id(id)) {
            t.getRegras().remove(r);
        }
        transacoes.flush();
        regras.delete(r);
        regras.flush();
    }
}
