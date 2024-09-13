package br.com.fiap.prospai.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class FeedbackResponseDTO extends RepresentationModel<FeedbackResponseDTO> {

    private Long id;
    private String titulo;
    private String descricao;
    private Integer nota;
    private LocalDateTime dataCriacao;
    private ClienteResponseDTO cliente;
}
