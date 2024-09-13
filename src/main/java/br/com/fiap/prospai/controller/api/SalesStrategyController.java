package br.com.fiap.prospai.controller.api;

import br.com.fiap.prospai.dto.request.SalesStrategyRequestDTO;
import br.com.fiap.prospai.dto.response.SalesStrategyResponseDTO;
import br.com.fiap.prospai.service.SalesStrategyService;
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
@RequestMapping("/api/sales-strategies")
public class SalesStrategyController {

    private final SalesStrategyService salesStrategyService;

    public SalesStrategyController(SalesStrategyService salesStrategyService) {
        this.salesStrategyService = salesStrategyService;
    }

    @Operation(summary = "Retrieve all sales strategies", description = "Returns a list of all sales strategies.")
    @GetMapping
    public ResponseEntity<List<EntityModel<SalesStrategyResponseDTO>>> getAllSalesStrategies() {
        List<EntityModel<SalesStrategyResponseDTO>> salesStrategies = salesStrategyService.getAllSalesStrategies().stream()
                .map(this::toEntityModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(salesStrategies);
    }

    @Operation(summary = "Get a sales strategy by ID", description = "Returns a single sales strategy by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sales strategy found"),
            @ApiResponse(responseCode = "404", description = "Sales strategy not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<SalesStrategyResponseDTO>> getSalesStrategyById(
            @Parameter(description = "ID of the sales strategy to be retrieved") @PathVariable Long id) {
        Optional<SalesStrategyResponseDTO> salesStrategy = salesStrategyService.getSalesStrategyById(id);
        return salesStrategy.map(this::toEntityModel)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new sales strategy", description = "Creates a new sales strategy.")
    @ApiResponse(responseCode = "201", description = "Sales strategy created")
    @PostMapping
    public ResponseEntity<EntityModel<SalesStrategyResponseDTO>> createSalesStrategy(
            @RequestBody SalesStrategyRequestDTO salesStrategyRequestDTO) {
        SalesStrategyResponseDTO novaSalesStrategy = salesStrategyService.createSalesStrategy(salesStrategyRequestDTO);
        EntityModel<SalesStrategyResponseDTO> salesStrategyModel = toEntityModel(novaSalesStrategy);
        return ResponseEntity.created(salesStrategyModel.getRequiredLink("self").toUri()).body(salesStrategyModel);
    }

    @Operation(summary = "Update a sales strategy", description = "Updates an existing sales strategy by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sales strategy updated"),
            @ApiResponse(responseCode = "404", description = "Sales strategy not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<SalesStrategyResponseDTO>> updateSalesStrategy(
            @Parameter(description = "ID of the sales strategy to be updated") @PathVariable Long id,
            @RequestBody SalesStrategyRequestDTO salesStrategyRequestDTO) {
        SalesStrategyResponseDTO salesStrategyAtualizada = salesStrategyService.updateSalesStrategy(id, salesStrategyRequestDTO);
        return ResponseEntity.ok(toEntityModel(salesStrategyAtualizada));
    }

    @Operation(summary = "Delete a sales strategy", description = "Deletes a sales strategy by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sales strategy deleted"),
            @ApiResponse(responseCode = "404", description = "Sales strategy not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalesStrategy(
            @Parameter(description = "ID of the sales strategy to be deleted") @PathVariable Long id) {
        salesStrategyService.deleteSalesStrategy(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<SalesStrategyResponseDTO> toEntityModel(SalesStrategyResponseDTO salesStrategy) {
        EntityModel<SalesStrategyResponseDTO> model = EntityModel.of(salesStrategy);
        model.add(linkTo(methodOn(SalesStrategyController.class).getSalesStrategyById(salesStrategy.getId())).withSelfRel());
        model.add(linkTo(methodOn(SalesStrategyController.class).getAllSalesStrategies()).withRel("sales-strategies"));
        return model;
    }
}
