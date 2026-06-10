package com.aurapm.core.service;

import com.aurapm.core.entity.Issue;
import com.aurapm.core.entity.IssueStatus;
import com.aurapm.core.repository.IssueRepository;
import com.aurapm.core.dto.IssueRequestDTO;
import com.aurapm.core.dto.IssueResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.UUID;

@Service
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;
    private final RestClient restClient;

    // Constructor injection - setting up the base URL dynamically using Docker DNS
    public IssueServiceImpl(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
        this.restClient = RestClient.builder()
                .baseUrl("http://predictive-analytics:8000") // Resolves via Docker DNS
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public IssueResponseDTO getIssueById(UUID id) {Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found with ID: " + id));
        
        IssueResponseDTO responseDTO = mapToResponseDTO(issue);

        // CROSS-SERVICE CALL: Enrich the response with Python MLOps Intelligence
        try {
            Map<String, Object> pythonPayload = Map.of(
                "issue_id", issue.getId().toString(),
                "story_points", issue.getStoryPoints() != null ? issue.getStoryPoints() : 0,
                "description_length", issue.getDescription() != null ? issue.getDescription().length() : 0,
                "historical_spillover_rate", 0.35 // Simulating a static historical team baseline
            );

            // POST to FastAPI microservice
            Map<?, ?> analyticsResult = restClient.post()
                    .uri("/api/v1/analytics/predict-spillover")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(pythonPayload)
                    .retrieve()
                    .body(Map.class);

            if (analyticsResult != null) {
                // Dynamically append the ML risk level directly into the ticket description summary
                responseDTO.setDescription(responseDTO.getDescription() + 
                    "\n\n[AI Delivery Insight] Spillover Risk: " + analyticsResult.get("risk_level") + 
                    " (Probability: " + analyticsResult.get("spillover_probability") + ")");
            }
        } catch (Exception e) {
            // Fault Tolerance: If the analytics engine goes down, the core ticket engine must not crash!
            responseDTO.setDescription(responseDTO.getDescription() + "\n\n[AI Delivery Insight] Analytics service temporarily offline.");
        }

        return responseDTO;
    }
    

    @Override
    @Transactional
    public IssueResponseDTO createIssue(IssueRequestDTO request) {
        Issue issue = Issue.builder()
                .projectId(request.getProjectId())
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : IssueStatus.TODO)
                .storyPoints(request.getStoryPoints())
                .build();

        Issue saved = issueRepository.save(issue);
        return mapToResponseDTO(saved);
    }

    @Override
    @Transactional
    public IssueResponseDTO updateIssueStatus(UUID id, String newStatus) {
        Issue issue = issueRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Issue not found with ID: " + id));

        try {
            IssueStatus status = IssueStatus.valueOf(newStatus);
            issue.setStatus(status);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid status provided: " + newStatus);
        }

        Issue saved = issueRepository.save(issue);
        return mapToResponseDTO(saved);
    }

    private IssueResponseDTO mapToResponseDTO(Issue issue) {
        IssueResponseDTO dto = new IssueResponseDTO();
        dto.setId(issue.getId());
        dto.setProjectId(issue.getProjectId());
        dto.setTitle(issue.getTitle());
        dto.setDescription(issue.getDescription());
        dto.setStatus(issue.getStatus());
        dto.setStoryPoints(issue.getStoryPoints());
        dto.setParentIssueId(issue.getParentIssue() != null ? issue.getParentIssue().getId() : null);
        dto.setCreatedAt(issue.getCreatedAt());
        dto.setUpdatedAt(issue.getUpdatedAt());
        return dto;
    }
}