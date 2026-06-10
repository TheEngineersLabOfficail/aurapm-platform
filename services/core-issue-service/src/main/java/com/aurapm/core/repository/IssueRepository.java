package com.aurapm.core.repository;

import com.aurapm.core.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IssueRepository extends JpaRepository<Issue, UUID> {
    
    // Finds all issues belonging to a specific epic, story, or project
    List<Issue> findByProjectId(UUID projectId);
    
    // Fetches child sub-tasks linked directly to a parent issue
    List<Issue> findByParentIssueId(UUID parentIssueId);
}
