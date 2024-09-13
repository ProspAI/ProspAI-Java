package br.com.fiap.prospai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "REPORTS_2")
public class Report extends RepresentationModel<Report> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_2_seq")
    @SequenceGenerator(name = "report_2_seq", sequenceName = "REPORTS_2_SEQ", allocationSize = 1)
    private Long id;

    @NotBlank
    @Column(name = "TITULO", nullable = false)
    private String titulo;

    @NotBlank
    @Column(name = "DESCRICAO", nullable = false)
    private String descricao;

    @Column(name = "PERIODO_INICIAL", nullable = false)
    private LocalDate periodoInicial;

    @Column(name = "PERIODO_FINAL", nullable = false)
    private LocalDate periodoFinal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENTE_2_ID", nullable = false)
    private Cliente cliente;

    @Column(name = "DATA_CRIACAO", nullable = false)
    private LocalDateTime dataCriacao;
}
