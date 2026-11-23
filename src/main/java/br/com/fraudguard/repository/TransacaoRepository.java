package br.com.fraudguard.repository;
import br.com.fraudguard.domain.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    boolean existsByCliente_Id(Long clienteId);
    List<Transacao> findDistinctByRegras_Id(Long regraId);
}
