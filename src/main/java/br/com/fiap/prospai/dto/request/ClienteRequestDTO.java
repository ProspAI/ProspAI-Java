package br.com.fiap.prospai.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClienteRequestDTO {
    private Long id;
    @NotBlank
    @Size(max = 100)
    private String nome;
    @Email
    @NotBlank
    private String email;
    @Size(max = 15)
    private String telefone;
    private String segmentoMercado;
    private Double scoreEngajamento;
    private LocalDateTime dataCriacao;
}
