package br.com.fiap.prospai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PredictionRequestDTO {

    private Long id;  // ID da predição

    @NotBlank
    private String titulo;  // Título da predição

    @NotBlank
    private String descricao;  // Descrição da predição

    private Double precisao;  // Precisão da predição

    @NotNull(message = "O clienteId não pode ser nulo")
    private Long clienteId;  // ID do cliente associado à predição
}
