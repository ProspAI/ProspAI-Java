package br.com.fiap.prospai.controller.api;

import br.com.fiap.prospai.dto.request.UsuarioRequestDTO;
import br.com.fiap.prospai.dto.response.UsuarioResponseDTO;
import br.com.fiap.prospai.service.UsuarioService;
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
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Retrieve all users", description = "Returns a list of all users.")
    @GetMapping
    public ResponseEntity<List<EntityModel<UsuarioResponseDTO>>> getAllUsuarios() {
        List<EntityModel<UsuarioResponseDTO>> usuarios = usuarioService.getAllUsuarios().stream()
                .map(this::toEntityModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(usuarios);
    }

    @Operation(summary = "Get a user by ID", description = "Returns a single user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> getUsuarioById(
            @Parameter(description = "ID of the user to be retrieved") @PathVariable Long id) {
        Optional<UsuarioResponseDTO> usuario = usuarioService.getUsuarioById(id);
        return usuario.map(this::toEntityModel)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new user", description = "Creates a new user.")
    @ApiResponse(responseCode = "201", description = "User created")
    @PostMapping
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> createUsuario(@RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO novoUsuario = usuarioService.createUsuario(usuarioRequestDTO);
        EntityModel<UsuarioResponseDTO> usuarioModel = toEntityModel(novoUsuario);
        return ResponseEntity.created(usuarioModel.getRequiredLink("self").toUri()).body(usuarioModel);
    }

    @Operation(summary = "Update a user", description = "Updates an existing user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> updateUsuario(
            @Parameter(description = "ID of the user to be updated") @PathVariable Long id,
            @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO usuarioAtualizado = usuarioService.updateUsuario(id, usuarioRequestDTO);
        return ResponseEntity.ok(toEntityModel(usuarioAtualizado));
    }

    @Operation(summary = "Delete a user", description = "Deletes a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(
            @Parameter(description = "ID of the user to be deleted") @PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<UsuarioResponseDTO> toEntityModel(UsuarioResponseDTO usuario) {
        EntityModel<UsuarioResponseDTO> model = EntityModel.of(usuario);
        model.add(linkTo(methodOn(UsuarioController.class).getUsuarioById(usuario.getId())).withSelfRel());
        model.add(linkTo(methodOn(UsuarioController.class).getAllUsuarios()).withRel("usuarios"));
        return model;
    }
}
