package br.com.fiap.prospai.repository;

import br.com.fiap.prospai.entity.Prediction;
import br.com.fiap.prospai.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    // Busca predições de um determinado cliente
    List<Prediction> findByCliente(Cliente cliente);

    // Busca predições com precisão acima de um determinado valor
    List<Prediction> findByPrecisaoGreaterThan(Double precisao);

    // Busca predições de um cliente com precisão acima de um determinado valor
    List<Prediction> findByClienteAndPrecisaoGreaterThan(Cliente cliente, Double precisao);
}
