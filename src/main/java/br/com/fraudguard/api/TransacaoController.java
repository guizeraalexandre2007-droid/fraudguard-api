package br.com.fraudguard.api;

import br.com.fraudguard.api.Dtos.TransacaoResponse;
import br.com.fraudguard.service.TransacaoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
@Tag(name = "Transacoes - Consulta Polimorfica", description = "Consulta de todas as subclasses pela entidade base")
public class TransacaoController {
    private final TransacaoService service;
    public TransacaoController(TransacaoService service) { this.service = service; }

    @GetMapping
    public List<TransacaoResponse> listar() { return service.listarTodas(); }
    @GetMapping("/{id}")
    public TransacaoResponse buscar(@PathVariable Long id) { return service.buscarQualquer(id); }
}
