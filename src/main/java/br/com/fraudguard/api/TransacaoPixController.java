package br.com.fraudguard.api;

import br.com.fraudguard.api.Dtos.PixRequest;
import br.com.fraudguard.api.Dtos.TransacaoResponse;
import br.com.fraudguard.service.TransacaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/transacoes/pix")
@Tag(name = "Transacoes PIX", description = "CRUD da subclasse TransacaoPix")
public class TransacaoPixController {
    private final TransacaoService service;
    public TransacaoPixController(TransacaoService service) { this.service = service; }

    @GetMapping
    public List<TransacaoResponse> listar() { return service.listarPix(); }
    @GetMapping("/{id}")
    public TransacaoResponse buscar(@PathVariable Long id) { return service.buscarPix(id); }
    @PostMapping
    public ResponseEntity<TransacaoResponse> criar(@Valid @RequestBody PixRequest req) {
        TransacaoResponse dto = service.criarPix(req);
        return ResponseEntity.created(URI.create("/api/transacoes/pix/" + dto.id())).body(dto);
    }
    @PutMapping("/{id}")
    public TransacaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody PixRequest req) {
        return service.atualizarPix(id, req);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluirPix(id);
        return ResponseEntity.noContent().build();
    }
}
