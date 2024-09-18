// Usuario.java
package br.com.fiap.prospai.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Data
@Table(name = "USUARIOS_2")
public class Usuario {

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

    @NotBlank
    @Column(name = "PAPEL", nullable = false)
    private String papel; // Exemplo: "ROLE_ADMIN" ou "ROLE_USER"

    @Column(name = "ATIVO", nullable = false)
    private boolean ativo;
}
