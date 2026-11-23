package br.com.fraudguard.service;

import br.com.fraudguard.api.Dtos.*;
import br.com.fraudguard.domain.*;
import br.com.fraudguard.repository.*;
import br.com.fraudguard.service.Exceptions.NaoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class TransacaoService {
    private final TransacaoRepository transacoes;
    private final TransacaoPixRepository pix;
    private final TransacaoCartaoRepository cartoes;
    private final ClienteRepository clientes;
    private final RegraAntifraudeRepository regras;

    public TransacaoService(TransacaoRepository transacoes, TransacaoPixRepository pix,
                            TransacaoCartaoRepository cartoes, ClienteRepository clientes,
                            RegraAntifraudeRepository regras) {
        this.transacoes = transacoes; this.pix = pix; this.cartoes = cartoes;
        this.clientes = clientes; this.regras = regras;
    }

    public List<TransacaoResponse> listarTodas() {
        return transacoes.findAll().stream().map(TransacaoResponse::of).toList();
    }
    public TransacaoResponse buscarQualquer(Long id) {
        return TransacaoResponse.of(transacoes.findById(id).orElseThrow(() ->
            new NaoEncontradoException("Transacao " + id + " nao encontrada")));
    }
    public List<TransacaoResponse> listarPix() {
        return pix.findAll().stream().map(TransacaoResponse::of).toList();
    }
    public List<TransacaoResponse> listarCartoes() {
        return cartoes.findAll().stream().map(TransacaoResponse::of).toList();
    }
    public TransacaoResponse buscarPix(Long id) { return TransacaoResponse.of(obterPix(id)); }
    public TransacaoResponse buscarCartao(Long id) { return TransacaoResponse.of(obterCartao(id)); }

    private TransacaoPix obterPix(Long id) {
        return pix.findById(id).orElseThrow(() -> new NaoEncontradoException("PIX " + id + " nao encontrado"));
    }
    private TransacaoCartao obterCartao(Long id) {
        return cartoes.findById(id).orElseThrow(() -> new NaoEncontradoException("Cartao " + id + " nao encontrado"));
    }
    private Cliente cliente(DadosTransacao req) {
        return clientes.findById(req.clienteId()).orElseThrow(() ->
            new NaoEncontradoException("Cliente " + req.clienteId() + " nao encontrado"));
    }
    private Set<RegraAntifraude> regras(DadosTransacao req) {
        Set<RegraAntifraude> encontradas = new HashSet<>(regras.findAllById(req.regraIds()));
        if (encontradas.size() != req.regraIds().size()) {
            throw new NaoEncontradoException("Uma ou mais regras informadas nao existem");
        }
        return encontradas;
    }

    @Transactional
    public TransacaoResponse criarPix(PixRequest req) {
        TransacaoPix t = new TransacaoPix(req.valor(), req.status(), cliente(req),
            regras(req), req.chavePix(), req.bancoDestino());
        return TransacaoResponse.of(pix.saveAndFlush(t));
    }
    @Transactional
    public TransacaoResponse atualizarPix(Long id, PixRequest req) {
        TransacaoPix t = obterPix(id);
        t.atualizar(req.valor(), req.status(), cliente(req), regras(req));
        t.atualizarDadosPix(req.chavePix(), req.bancoDestino());
        return TransacaoResponse.of(pix.saveAndFlush(t));
    }
    @Transactional
    public void excluirPix(Long id) {
        pix.delete(obterPix(id)); pix.flush();
    }
    @Transactional
    public TransacaoResponse criarCartao(CartaoRequest req) {
        TransacaoCartao t = new TransacaoCartao(req.valor(), req.status(), cliente(req),
            regras(req), req.ultimosQuatroDigitos(), req.bandeira());
        return TransacaoResponse.of(cartoes.saveAndFlush(t));
    }
    @Transactional
    public TransacaoResponse atualizarCartao(Long id, CartaoRequest req) {
        TransacaoCartao t = obterCartao(id);
        t.atualizar(req.valor(), req.status(), cliente(req), regras(req));
        t.atualizarDadosCartao(req.ultimosQuatroDigitos(), req.bandeira());
        return TransacaoResponse.of(cartoes.saveAndFlush(t));
    }
    @Transactional
    public void excluirCartao(Long id) {
        cartoes.delete(obterCartao(id)); cartoes.flush();
    }
}
