package br.com.fiap.prospai.repository;

import br.com.fiap.prospai.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Busca um cliente pelo e-mail
    Optional<Cliente> findByEmail(String email);

    // Busca clientes pelo nome (ou parte dele)
    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    // Busca clientes por segmento de mercado
    List<Cliente> findBySegmentoMercado(String segmentoMercado);
}
