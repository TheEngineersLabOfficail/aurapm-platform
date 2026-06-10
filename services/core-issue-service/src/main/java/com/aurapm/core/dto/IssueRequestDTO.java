package com.aurapm.core.dto;

import com.aurapm.core.entity.IssueStatus;
import lombok.Data;
import java.util.UUID;

@Data
public class IssueRequestDTO {
    private UUID projectId;
    private String title;
    private String description;
    private IssueStatus status;
    private Integer storyPoints;
    private UUID parentIssueId; // Null if it's an Epic
}