package br.com.fiap.prospai.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class ReportResponseDTO extends RepresentationModel<ReportResponseDTO> {

    private Long id;
    private String titulo;
    private String descricao;
    private LocalDate periodoInicial;
    private LocalDate periodoFinal;
    private LocalDateTime dataCriacao;
    private ClienteResponseDTO cliente;
}