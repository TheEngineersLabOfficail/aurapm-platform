package com.aurapm.core.service;

import com.aurapm.core.dto.IssueRequestDTO;
import com.aurapm.core.dto.IssueResponseDTO;
import java.util.UUID;

public interface IssueService {
    IssueResponseDTO createIssue(IssueRequestDTO request);
    IssueResponseDTO getIssueById(UUID id);
    IssueResponseDTO updateIssueStatus(UUID id, String newStatus);
}