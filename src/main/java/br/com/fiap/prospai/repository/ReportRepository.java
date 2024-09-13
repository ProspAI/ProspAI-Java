package br.com.fiap.prospai.repository;

import br.com.fiap.prospai.entity.Report;
import br.com.fiap.prospai.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    // Busca relatórios de um determinado cliente
    List<Report> findByCliente(Cliente cliente);

    // Busca relatórios criados dentro de um período específico
    List<Report> findByPeriodoInicialGreaterThanEqualAndPeriodoFinalLessThanEqual(LocalDate startDate, LocalDate endDate);

    // Busca relatórios de um cliente por título
    List<Report> findByClienteAndTituloContainingIgnoreCase(Cliente cliente, String titulo);
}
