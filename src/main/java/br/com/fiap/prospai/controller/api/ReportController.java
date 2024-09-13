package br.com.fiap.prospai.controller.api;

import br.com.fiap.prospai.dto.request.ReportRequestDTO;
import br.com.fiap.prospai.dto.response.ReportResponseDTO;
import br.com.fiap.prospai.service.ReportService;
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
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "Retrieve all reports", description = "Returns a list of all reports.")
    @GetMapping
    public ResponseEntity<List<EntityModel<ReportResponseDTO>>> getAllReports() {
        List<EntityModel<ReportResponseDTO>> reports = reportService.getAllReports().stream()
                .map(this::toEntityModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(reports);
    }

    @Operation(summary = "Get a report by ID", description = "Returns a single report by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report found"),
            @ApiResponse(responseCode = "404", description = "Report not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ReportResponseDTO>> getReportById(
            @Parameter(description = "ID of the report to be retrieved") @PathVariable Long id) {
        Optional<ReportResponseDTO> report = reportService.getReportById(id);
        return report.map(this::toEntityModel)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new report", description = "Creates a new report for a client.")
    @ApiResponse(responseCode = "201", description = "Report created")
    @PostMapping
    public ResponseEntity<EntityModel<ReportResponseDTO>> createReport(
            @RequestBody ReportRequestDTO reportRequestDTO) {
        Long clienteId = reportRequestDTO.getClienteId();
        ReportResponseDTO novoReport = reportService.createReport(reportRequestDTO, clienteId);
        EntityModel<ReportResponseDTO> reportModel = toEntityModel(novoReport);
        return ResponseEntity.created(reportModel.getRequiredLink("self").toUri()).body(reportModel);
    }

    @Operation(summary = "Update a report", description = "Updates an existing report by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report updated"),
            @ApiResponse(responseCode = "404", description = "Report not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ReportResponseDTO>> updateReport(
            @Parameter(description = "ID of the report to be updated") @PathVariable Long id,
            @RequestBody ReportRequestDTO reportRequestDTO) {
        ReportResponseDTO reportAtualizado = reportService.updateReport(id, reportRequestDTO);
        return ResponseEntity.ok(toEntityModel(reportAtualizado));
    }

    @Operation(summary = "Delete a report", description = "Deletes a report by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Report deleted"),
            @ApiResponse(responseCode = "404", description = "Report not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(
            @Parameter(description = "ID of the report to be deleted") @PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    private EntityModel<ReportResponseDTO> toEntityModel(ReportResponseDTO report) {
        EntityModel<ReportResponseDTO> model = EntityModel.of(report);
        model.add(linkTo(methodOn(ReportController.class).getReportById(report.getId())).withSelfRel());
        model.add(linkTo(methodOn(ReportController.class).getAllReports()).withRel("reports"));
        model.add(linkTo(methodOn(ClienteController.class).getClienteById(report.getCliente().getId())).withRel("cliente"));
        return model;
    }
}
