package br.com.fraudguard.api;

import br.com.fraudguard.api.Dtos.ClienteRequest;
import br.com.fraudguard.api.Dtos.ClienteResponse;
import br.com.fraudguard.service.ClienteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "CRUD de clientes (relacionamento 1:N com transacoes)")
public class ClienteController {
    private final ClienteService service;
    public ClienteController(ClienteService service) { this.service = service; }

    @GetMapping
    public List<ClienteResponse> listar() { return service.listar(); }
    @GetMapping("/{id}")
    public ClienteResponse buscar(@PathVariable Long id) { return service.buscar(id); }
    @PostMapping
    public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody ClienteRequest req) {
        ClienteResponse dto = service.criar(req);
        return ResponseEntity.created(URI.create("/api/clientes/" + dto.id())).body(dto);
    }
    @PutMapping("/{id}")
    public ClienteResponse atualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest req) {
        return service.atualizar(id, req);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
