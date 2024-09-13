package br.com.fiap.prospai.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class SalesStrategyResponseDTO extends RepresentationModel<SalesStrategyResponseDTO> {

    private Long id;
    private String titulo;
    private String descricao;
    private String tipoEstrategia;
    private LocalDate dataImplementacao;
    private Double eficaciaPrevista;
}
