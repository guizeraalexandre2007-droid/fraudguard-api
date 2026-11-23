package br.com.fraudguard.repository;
import br.com.fraudguard.domain.TransacaoCartao;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TransacaoCartaoRepository extends JpaRepository<TransacaoCartao, Long> {}
