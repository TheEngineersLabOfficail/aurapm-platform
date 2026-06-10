package com.aurapm.core.dto;

import com.aurapm.core.entity.IssueStatus;
import lombok.Data;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class IssueResponseDTO {
    private UUID id;
    private UUID projectId;
    private String title;
    private String description;
    private IssueStatus status;
    private Integer storyPoints;
    private UUID parentIssueId;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}