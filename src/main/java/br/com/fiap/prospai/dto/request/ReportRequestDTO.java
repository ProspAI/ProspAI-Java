package br.com.fiap.prospai.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReportRequestDTO {

    private Long id;

    @NotBlank
    private String titulo;

    @NotBlank
    private String descricao;

    private LocalDate periodoInicial;
    private LocalDate periodoFinal;

    private Long clienteId; // ID do cliente associado ao relatório
}