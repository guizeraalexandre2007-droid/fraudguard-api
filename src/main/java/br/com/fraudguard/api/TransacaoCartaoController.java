package br.com.fraudguard.api;

import br.com.fraudguard.api.Dtos.CartaoRequest;
import br.com.fraudguard.api.Dtos.TransacaoResponse;
import br.com.fraudguard.service.TransacaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/transacoes/cartao")
@Tag(name = "Transacoes Cartao", description = "CRUD da subclasse TransacaoCartao")
public class TransacaoCartaoController {
    private final TransacaoService service;
    public TransacaoCartaoController(TransacaoService service) { this.service = service; }

    @GetMapping
    public List<TransacaoResponse> listar() { return service.listarCartoes(); }
    @GetMapping("/{id}")
    public TransacaoResponse buscar(@PathVariable Long id) { return service.buscarCartao(id); }
    @PostMapping
    public ResponseEntity<TransacaoResponse> criar(@Valid @RequestBody CartaoRequest req) {
        TransacaoResponse dto = service.criarCartao(req);
        return ResponseEntity.created(URI.create("/api/transacoes/cartao/" + dto.id())).body(dto);
    }
    @PutMapping("/{id}")
    public TransacaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody CartaoRequest req) {
        return service.atualizarCartao(id, req);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluirCartao(id);
        return ResponseEntity.noContent().build();
    }
}
