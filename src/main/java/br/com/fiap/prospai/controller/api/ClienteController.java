package br.com.fiap.prospai.controller.api;

import br.com.fiap.prospai.dto.request.ClienteRequestDTO;
import br.com.fiap.prospai.dto.response.ClienteResponseDTO;
import br.com.fiap.prospai.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Operation(summary = "Retrieve all clients", description = "Returns a list of all clients in the system.")
    @GetMapping
    public ResponseEntity<List<EntityModel<ClienteResponseDTO>>> getAllClientes() {
        List<EntityModel<ClienteResponseDTO>> clientes = clienteService.getAllClientes().stream()
                .map(this::toEntityModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clientes);
    }

    @Operation(summary = "Get a client by ID", description = "Returns a single client by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client found"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ClienteResponseDTO>> getClienteById(
            @Parameter(description = "ID of the client to be retrieved") @PathVariable Long id) {
        Optional<ClienteResponseDTO> cliente = clienteService.getClienteById(id);
        return cliente.map(this::toEntityModel)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new client", description = "Creates a new client in the system.")
    @ApiResponse(responseCode = "201", description = "Client created")
    @PostMapping
    public ResponseEntity<EntityModel<ClienteResponseDTO>> createCliente(@RequestBody ClienteRequestDTO clienteRequestDTO) {
        ClienteResponseDTO novoCliente = clienteService.createCliente(clienteRequestDTO);
        EntityModel<ClienteResponseDTO> clienteModel = toEntityModel(novoCliente);
        return ResponseEntity.created(clienteModel.getRequiredLink("self").toUri()).body(clienteModel);
    }

    @Operation(summary = "Update a client", description = "Updates an existing client by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client updated"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ClienteResponseDTO>> updateCliente(
            @Parameter(description = "ID of the client to be updated") @PathVariable Long id,
            @RequestBody ClienteRequestDTO clienteRequestDTO) {
        ClienteResponseDTO clienteAtualizado = clienteService.updateCliente(id, clienteRequestDTO);
        return ResponseEntity.ok(toEntityModel(clienteAtualizado));
    }

    @Operation(summary = "Delete a client", description = "Deletes a client by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Client deleted"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCliente(
            @Parameter(description = "ID of the client to be deleted") @PathVariable Long id) {
        clienteService.deleteCliente(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<ClienteResponseDTO> toEntityModel(ClienteResponseDTO cliente) {
        EntityModel<ClienteResponseDTO> model = EntityModel.of(cliente);
        model.add(linkTo(methodOn(ClienteController.class).getClienteById(cliente.getId())).withSelfRel());
        model.add(linkTo(methodOn(ClienteController.class).getAllClientes()).withRel("clientes"));
        return model;
    }
}
