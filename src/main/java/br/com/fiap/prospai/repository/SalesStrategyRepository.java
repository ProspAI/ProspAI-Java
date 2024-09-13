package br.com.fiap.prospai.repository;

import br.com.fiap.prospai.entity.SalesStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalesStrategyRepository extends JpaRepository<SalesStrategy, Long> {

    // Busca estratégias de vendas por tipo
    List<SalesStrategy> findByTipoEstrategia(String tipoEstrategia);

    // Busca estratégias de vendas com eficácia prevista acima de um determinado valor
    List<SalesStrategy> findByEficaciaPrevistaGreaterThan(Double eficaciaPrevista);

    // Busca estratégias de vendas implementadas após uma determinada data
    List<SalesStrategy> findByDataImplementacaoAfter(LocalDate dataImplementacao);
}
