package br.com.fiap.prospai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "FEEDBACKS_2")
public class Feedback extends RepresentationModel<Feedback> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feedback_2_seq")
    @SequenceGenerator(name = "feedback_2_seq", sequenceName = "FEEDBACKS_2_SEQ", allocationSize = 1)
    private Long id;

    @NotBlank(message = "O título não deve estar em branco")
    @Column(name = "TITULO", nullable = false)
    private String titulo;

    @NotBlank(message = "A descrição não deve estar em branco")
    @Column(name = "DESCRICAO", nullable = false)
    private String descricao;

    @Column(name = "NOTA", nullable = false)
    private Integer nota;

    @Column(name = "DATA_CRIACAO", nullable = false)
    private LocalDateTime dataCriacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLIENTE_2_ID", nullable = false)
    private Cliente cliente;
}
