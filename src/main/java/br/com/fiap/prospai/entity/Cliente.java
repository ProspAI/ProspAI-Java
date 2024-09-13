package br.com.fiap.prospai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "CLIENTES_2")
public class Cliente extends RepresentationModel<Cliente> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cliente_2_seq")
    @SequenceGenerator(name = "cliente_2_seq", sequenceName = "CLIENTES_2_SEQ", allocationSize = 1)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(name = "NOME", nullable = false)
    private String nome;

    @Email
    @NotBlank
    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Size(max = 15)
    @Column(name = "TELEFONE", nullable = false)
    private String telefone;

    @Column(name = "SEGMENTO_MERCADO")
    private String segmentoMercado;

    @Column(name = "DATA_CRIACAO", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "SCORE_ENGAJAMENTO")
    private Double scoreEngajamento;
}
