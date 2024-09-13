package br.com.fiap.prospai.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeedbackRequestDTO {

    private Long id;

    private Integer rating;

    @NotBlank(message = "O título não deve estar em branco")
    private String titulo;

    @NotBlank(message = "A descrição não deve estar em branco")
    private String descricao;

    @NotNull(message = "A nota não pode ser nula")
    private Integer nota;

    @NotNull(message = "O clienteId não pode ser nulo")
    private Long clienteId; // Adicionado o campo clienteId com getter e setter automáticos pelo Lombok
}
