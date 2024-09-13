package br.com.fiap.prospai.controller.api;

import br.com.fiap.prospai.dto.request.FeedbackRequestDTO;
import br.com.fiap.prospai.dto.response.FeedbackResponseDTO;
import br.com.fiap.prospai.service.FeedbackService;
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
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @Operation(summary = "Retrieve all feedbacks", description = "Returns a list of all feedbacks.")
    @GetMapping
    public ResponseEntity<List<EntityModel<FeedbackResponseDTO>>> getAllFeedbacks() {
        List<EntityModel<FeedbackResponseDTO>> feedbacks = feedbackService.getAllFeedbacks().stream()
                .map(this::toEntityModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(feedbacks);
    }

    @Operation(summary = "Get a feedback by ID", description = "Returns a single feedback by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback found"),
            @ApiResponse(responseCode = "404", description = "Feedback not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<FeedbackResponseDTO>> getFeedbackById(
            @Parameter(description = "ID of the feedback to be retrieved") @PathVariable Long id) {
        Optional<FeedbackResponseDTO> feedback = feedbackService.getFeedbackById(id);
        return feedback.map(this::toEntityModel)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new feedback", description = "Creates a new feedback associated with a client.")
    @ApiResponse(responseCode = "201", description = "Feedback created")
    @PostMapping("/cliente/{clienteId}")
    public ResponseEntity<EntityModel<FeedbackResponseDTO>> createFeedback(
            @Parameter(description = "ID of the client for which feedback is being created") @PathVariable Long clienteId,
            @RequestBody FeedbackRequestDTO feedbackRequestDTO) {
        FeedbackResponseDTO novoFeedback = feedbackService.createFeedback(feedbackRequestDTO, clienteId);
        EntityModel<FeedbackResponseDTO> feedbackModel = toEntityModel(novoFeedback);
        return ResponseEntity.created(feedbackModel.getRequiredLink("self").toUri()).body(feedbackModel);
    }

    @Operation(summary = "Update a feedback", description = "Updates an existing feedback by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Feedback updated"),
            @ApiResponse(responseCode = "404", description = "Feedback not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<FeedbackResponseDTO>> updateFeedback(
            @Parameter(description = "ID of the feedback to be updated") @PathVariable Long id,
            @RequestBody FeedbackRequestDTO feedbackRequestDTO) {
        FeedbackResponseDTO feedbackAtualizado = feedbackService.updateFeedback(id, feedbackRequestDTO);
        return ResponseEntity.ok(toEntityModel(feedbackAtualizado));
    }

    @Operation(summary = "Delete a feedback", description = "Deletes a feedback by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Feedback deleted"),
            @ApiResponse(responseCode = "404", description = "Feedback not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(
            @Parameter(description = "ID of the feedback to be deleted") @PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<FeedbackResponseDTO> toEntityModel(FeedbackResponseDTO feedback) {
        EntityModel<FeedbackResponseDTO> model = EntityModel.of(feedback);
        model.add(linkTo(methodOn(FeedbackController.class).getFeedbackById(feedback.getId())).withSelfRel());
        model.add(linkTo(methodOn(FeedbackController.class).getAllFeedbacks()).withRel("feedbacks"));
        model.add(linkTo(methodOn(ClienteController.class).getClienteById(feedback.getCliente().getId())).withRel("cliente"));
        return model;
    }
}
