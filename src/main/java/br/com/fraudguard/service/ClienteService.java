package br.com.fraudguard.service;

import br.com.fraudguard.api.Dtos.ClienteRequest;
import br.com.fraudguard.api.Dtos.ClienteResponse;
import br.com.fraudguard.domain.Cliente;
import br.com.fraudguard.repository.ClienteRepository;
import br.com.fraudguard.repository.TransacaoRepository;
import br.com.fraudguard.service.Exceptions.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClienteService {
    private final ClienteRepository clientes;
    private final TransacaoRepository transacoes;

    public ClienteService(ClienteRepository clientes, TransacaoRepository transacoes) {
        this.clientes = clientes; this.transacoes = transacoes;
    }
    public List<ClienteResponse> listar() { return clientes.findAll().stream().map(ClienteResponse::of).toList(); }
    public ClienteResponse buscar(Long id) { return ClienteResponse.of(obter(id)); }

    public Cliente obter(Long id) {
        return clientes.findById(id).orElseThrow(() -> new NaoEncontradoException("Cliente " + id + " nao encontrado"));
    }

    @Transactional
    public ClienteResponse criar(ClienteRequest req) {
        return ClienteResponse.of(clientes.saveAndFlush(new Cliente(req.nome(), req.email(), req.documento())));
    }
    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest req) {
        Cliente c = obter(id);
        c.atualizar(req.nome(), req.email(), req.documento());
        return ClienteResponse.of(clientes.saveAndFlush(c));
    }
    @Transactional
    public void excluir(Long id) {
        Cliente c = obter(id);
        if (transacoes.existsByCliente_Id(id)) {
            throw new ConflitoException("Cliente possui transacoes; exclua-as antes de remover o cliente");
        }
        clientes.delete(c);
        clientes.flush();
    }
}
