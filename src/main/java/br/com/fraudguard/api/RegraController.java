package br.com.fraudguard.api;

import br.com.fraudguard.api.Dtos.RegraRequest;
import br.com.fraudguard.api.Dtos.RegraResponse;
import br.com.fraudguard.service.RegraService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/regras")
@Tag(name = "Regras Antifraude", description = "CRUD de regras (relacionamento N:N com transacoes)")
public class RegraController {
    private final RegraService service;
    public RegraController(RegraService service) { this.service = service; }

    @GetMapping
    public List<RegraResponse> listar() { return service.listar(); }
    @GetMapping("/{id}")
    public RegraResponse buscar(@PathVariable Long id) { return service.buscar(id); }
    @PostMapping
    public ResponseEntity<RegraResponse> criar(@Valid @RequestBody RegraRequest req) {
        RegraResponse dto = service.criar(req);
        return ResponseEntity.created(URI.create("/api/regras/" + dto.id())).body(dto);
    }
    @PutMapping("/{id}")
    public RegraResponse atualizar(@PathVariable Long id, @Valid @RequestBody RegraRequest req) {
        return service.atualizar(id, req);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
