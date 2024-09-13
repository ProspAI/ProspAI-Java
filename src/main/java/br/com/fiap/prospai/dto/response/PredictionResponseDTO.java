package br.com.fiap.prospai.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class PredictionResponseDTO extends RepresentationModel<PredictionResponseDTO> {

    private Long id;
    private String titulo;
    private String descricao;
    private Double precisao;
    private LocalDateTime dataGeracao;
    private ClienteResponseDTO cliente;
}
