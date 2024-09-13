package br.com.fiap.prospai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Entity
@Data
@Table(name = "USUARIOS_2")
public class Usuario extends RepresentationModel<Usuario> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_2_seq")
    @SequenceGenerator(name = "usuario_2_seq", sequenceName = "USUARIOS_2_SEQ", allocationSize = 1)
    private Long id;

    @NotBlank
    @Column(name = "NOME", nullable = false)
    private String nome;

    @Email
    @NotBlank
    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(name = "SENHA", nullable = false)
    private String senha;

    @Column(name = "PAPEL", nullable = false)
    private String papel;

    @Column(name = "ATIVO", nullable = false)
    private boolean ativo;
}
