package br.com.fiap.prospai.controller.api;

import br.com.fiap.prospai.dto.request.PredictionRequestDTO;
import br.com.fiap.prospai.dto.response.PredictionResponseDTO;
import br.com.fiap.prospai.service.PredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/predictions")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @Operation(summary = "Retrieve all predictions", description = "Returns a list of all predictions.")
    @GetMapping
    public ResponseEntity<List<EntityModel<PredictionResponseDTO>>> getAllPredictions() {
        List<EntityModel<PredictionResponseDTO>> predictions = predictionService.getAllPredictions().stream()
                .map(this::toEntityModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(predictions);
    }

    @Operation(summary = "Get a prediction by ID", description = "Returns a single prediction by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prediction found"),
            @ApiResponse(responseCode = "404", description = "Prediction not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PredictionResponseDTO>> getPredictionById(
            @Parameter(description = "ID of the prediction to be retrieved") @PathVariable Long id) {
        Optional<PredictionResponseDTO> prediction = predictionService.getPredictionById(id);
        return prediction.map(this::toEntityModel)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(EntityModel.of(new PredictionResponseDTO())));
    }

    @Operation(summary = "Create a new prediction", description = "Creates a new prediction for a client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Prediction created"),
            @ApiResponse(responseCode = "400", description = "Invalid data or client ID not found")
    })
    @PostMapping("/cliente/{clienteId}")
    public ResponseEntity<EntityModel<PredictionResponseDTO>> createPrediction(
            @Parameter(description = "ID of the client for which the prediction is being created") @PathVariable Long clienteId,
            @Valid @RequestBody PredictionRequestDTO predictionRequestDTO) {
        PredictionResponseDTO novaPrediction = predictionService.createPrediction(predictionRequestDTO, clienteId);
        EntityModel<PredictionResponseDTO> predictionModel = toEntityModel(novaPrediction);
        return ResponseEntity.created(predictionModel.getRequiredLink("self").toUri()).body(predictionModel);
    }

    @Operation(summary = "Update a prediction", description = "Updates an existing prediction by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prediction updated"),
            @ApiResponse(responseCode = "404", description = "Prediction not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<PredictionResponseDTO>> updatePrediction(
            @Parameter(description = "ID of the prediction to be updated") @PathVariable Long id,
            @Valid @RequestBody PredictionRequestDTO predictionRequestDTO) {
        PredictionResponseDTO predictionAtualizada = predictionService.updatePrediction(id, predictionRequestDTO);
        return ResponseEntity.ok(toEntityModel(predictionAtualizada));
    }

    @Operation(summary = "Delete a prediction", description = "Deletes a prediction by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Prediction deleted"),
            @ApiResponse(responseCode = "404", description = "Prediction not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrediction(
            @Parameter(description = "ID of the prediction to be deleted") @PathVariable Long id) {
        predictionService.deletePrediction(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<PredictionResponseDTO> toEntityModel(PredictionResponseDTO prediction) {
        EntityModel<PredictionResponseDTO> model = EntityModel.of(prediction);
        model.add(linkTo(methodOn(PredictionController.class).getPredictionById(prediction.getId())).withSelfRel());
        model.add(linkTo(methodOn(PredictionController.class).getAllPredictions()).withRel("predictions"));
        model.add(linkTo(methodOn(ClienteController.class).getClienteById(prediction.getCliente().getId())).withRel("cliente"));
        return model;
    }
}
