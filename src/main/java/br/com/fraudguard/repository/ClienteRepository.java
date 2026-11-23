package br.com.fraudguard.repository;
import br.com.fraudguard.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ClienteRepository extends JpaRepository<Cliente, Long> {}
