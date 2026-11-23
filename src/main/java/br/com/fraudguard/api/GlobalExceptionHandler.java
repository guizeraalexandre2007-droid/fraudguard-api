package br.com.fraudguard.api;

import br.com.fraudguard.service.Exceptions.ConflitoException;
import br.com.fraudguard.service.Exceptions.NaoEncontradoException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public record ApiError(LocalDateTime timestamp, int status, String erro, String mensagem,
                           String caminho, Map<String, String> campos) {}

    private ResponseEntity<ApiError> response(HttpStatus status, String mensagem, HttpServletRequest req,
                                               Map<String, String> campos) {
        return ResponseEntity.status(status).body(new ApiError(LocalDateTime.now(), status.value(),
            status.getReasonPhrase(), mensagem, req.getRequestURI(), campos));
    }
    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<ApiError> naoEncontrado(NaoEncontradoException ex, HttpServletRequest req) {
        return response(HttpStatus.NOT_FOUND, ex.getMessage(), req, Map.of());
    }
    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ApiError> conflito(ConflitoException ex, HttpServletRequest req) {
        return response(HttpStatus.CONFLICT, ex.getMessage(), req, Map.of());
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> restricao(DataIntegrityViolationException ex, HttpServletRequest req) {
        return response(HttpStatus.CONFLICT, "Conflito de integridade (valor duplicado ou relacionamento invalido)", req, Map.of());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> validacao(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> campos.put(e.getField(), e.getDefaultMessage()));
        return response(HttpStatus.BAD_REQUEST, "Erro de validacao", req, campos);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> jsonInvalido(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return response(HttpStatus.BAD_REQUEST, "JSON invalido ou campo com tipo/valor nao permitido", req, Map.of());
    }
}
