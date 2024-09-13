package br.com.fiap.prospai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "SALES_STRATEGIES_2")
public class SalesStrategy extends RepresentationModel<SalesStrategy> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sales_strategy_2_seq")
    @SequenceGenerator(name = "sales_strategy_2_seq", sequenceName = "SALES_STRATEGIES_2_SEQ", allocationSize = 1)
    private Long id;

    @NotBlank
    @Column(name = "TITULO", nullable = false)
    private String titulo;

    @NotBlank
    @Column(name = "DESCRICAO", nullable = false)
    private String descricao;

    @Column(name = "TIPO_ESTRATEGIA", nullable = false)
    private String tipoEstrategia;

    @Column(name = "DATA_IMPLEMENTACAO", nullable = false)
    private LocalDate dataImplementacao;

    @Column(name = "EFICACIA_PREVISTA")
    private Double eficaciaPrevista;
}
