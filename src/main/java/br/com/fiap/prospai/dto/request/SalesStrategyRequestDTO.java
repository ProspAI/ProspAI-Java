package br.com.fiap.prospai.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SalesStrategyRequestDTO {

    private Long id;

    @NotBlank
    private String titulo;

    @NotBlank
    private String descricao;

    private String tipoEstrategia;
    private LocalDate dataImplementacao;
    private Double eficaciaPrevista;
}
