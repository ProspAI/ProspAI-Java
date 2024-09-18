package br.com.fiap.prospai.controller.api;

import br.com.fiap.prospai.dto.request.UsuarioRequestDTO;
import br.com.fiap.prospai.dto.response.UsuarioResponseDTO;
import br.com.fiap.prospai.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import jakarta.validation.Valid;
import org.springframework.hateoas.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UsuarioResponseDTO>>> getAllUsuarios() {
        List<EntityModel<UsuarioResponseDTO>> usuarios = usuarioService.getAllUsuarios().stream()
                .map(this::toEntityModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<UsuarioResponseDTO>> collectionModel = CollectionModel.of(usuarios);
        collectionModel.add(linkTo(methodOn(UsuarioController.class).getAllUsuarios()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @Operation(summary = "Get a user by ID", description = "Returns a single user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> getUsuarioById(
            @PathVariable Long id) {
        return usuarioService.getUsuarioById(id)
                .map(usuario -> ResponseEntity.ok(toEntityModel(usuario)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Create a new user", description = "Creates a new user.")
    @ApiResponse(responseCode = "201", description = "User created")
    @PostMapping
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> createUsuario(
            @Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        UsuarioResponseDTO novoUsuario = usuarioService.createUsuario(usuarioRequestDTO);
        EntityModel<UsuarioResponseDTO> usuarioModel = toEntityModel(novoUsuario);
        return ResponseEntity
                .created(usuarioModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(usuarioModel);
    }

    @Operation(summary = "Update a user", description = "Updates an existing user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioResponseDTO>> updateUsuario(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        return usuarioService.getUsuarioById(id)
                .map(existingUsuario -> {
                    UsuarioResponseDTO usuarioAtualizado = usuarioService.updateUsuario(id, usuarioRequestDTO);
                    return ResponseEntity.ok(toEntityModel(usuarioAtualizado));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Delete a user", description = "Deletes a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        return usuarioService.getUsuarioById(id)
                .map(usuario -> {
                    usuarioService.deleteUsuario(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    private EntityModel<UsuarioResponseDTO> toEntityModel(UsuarioResponseDTO usuario) {
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).getUsuarioById(usuario.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).getAllUsuarios()).withRel("usuarios"));
    }
}
