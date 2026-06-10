package com.aurapm.core.controller;

import com.aurapm.core.service.IssueService;
import com.aurapm.core.dto.IssueRequestDTO;
import com.aurapm.core.dto.IssueResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping
    public ResponseEntity<IssueResponseDTO> createIssue(@RequestBody IssueRequestDTO request) {
        IssueResponseDTO response = issueService.createIssue(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IssueResponseDTO> getIssueById(@PathVariable UUID id) {
        return ResponseEntity.ok(issueService.getIssueById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<IssueResponseDTO> updateStatus(
            @PathVariable UUID id, 
            @RequestParam String status) {
        return ResponseEntity.ok(issueService.updateIssueStatus(id, status));
    }
}