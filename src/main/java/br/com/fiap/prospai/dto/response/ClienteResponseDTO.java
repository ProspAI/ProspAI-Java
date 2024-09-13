package br.com.fiap.prospai.dto.response;

import br.com.fiap.prospai.entity.Cliente;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
public class ClienteResponseDTO extends RepresentationModel<ClienteResponseDTO> {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String segmentoMercado;
    private LocalDateTime dataCriacao;
    private Double scoreEngajamento;

    // Construtor que aceita um Cliente
    public ClienteResponseDTO(Cliente cliente) {
        this.id = cliente.getId();
        this.nome = cliente.getNome();
        this.email = cliente.getEmail();
        this.telefone = cliente.getTelefone();
        this.segmentoMercado = cliente.getSegmentoMercado();
        this.dataCriacao = cliente.getDataCriacao();
        this.scoreEngajamento = cliente.getScoreEngajamento();
    }

    // Construtor padrão (necessário para o uso do Lombok)
    public ClienteResponseDTO() {}
}
