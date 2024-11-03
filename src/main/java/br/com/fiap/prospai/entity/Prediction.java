package br.com.fiap.prospai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "PREDICTIONS_2")
public class Prediction extends RepresentationModel<Prediction> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prediction_2_seq")
    @SequenceGenerator(name = "prediction_2_seq", sequenceName = "PREDICTIONS_2_SEQ", allocationSize = 1)
    private Long id;

    @NotBlank
    @Column(name = "TITULO", nullable = false)
    private String titulo;

    // Atualizado para NVARCHAR(MAX) e uso de @Lob
    @Lob
    @Column(name = "DESCRICAO", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String descricao;

    @Column(name = "PRECISAO")
    private Double precisao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENTE_2_ID", nullable = false)
    private Cliente cliente;

    @Column(name = "DATA_GERACAO", nullable = false)
    private LocalDateTime dataGeracao;
}
