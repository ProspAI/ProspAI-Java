package br.com.fiap.prospai.repository;

import br.com.fiap.prospai.entity.Feedback;
import br.com.fiap.prospai.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Busca feedbacks por título (ou parte dele)
    List<Feedback> findByTituloContainingIgnoreCase(String titulo);

    // Busca feedbacks de um determinado cliente
    List<Feedback> findByCliente(Cliente cliente);

    // Busca feedbacks de um cliente por título
    List<Feedback> findByClienteAndTituloContainingIgnoreCase(Cliente cliente, String titulo);
}
