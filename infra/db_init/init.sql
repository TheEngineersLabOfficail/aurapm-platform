-- Enable UUID extension (essential for distributed microservices)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- 1. Users Table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Projects Table
CREATE TABLE projects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    key VARCHAR(10) UNIQUE NOT NULL, -- e.g., "JIRA", "AURA"
    owner_id UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Allowed Workflow Statuses
CREATE TYPE issue_status AS ENUM ('TODO', 'IN_PROGRESS', 'IN_REVIEW', 'DONE');

-- 4. Core Issues Table (Solving the Hierarchical Challenge)
CREATE TABLE issues (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status issue_status DEFAULT 'TODO',
    
    -- SELF-REFERENCING KEY: Solves Hierarchy (Epic -> Story -> Task)
    parent_issue_id UUID REFERENCES issues(id) ON DELETE SET NULL,
    
    assignee_id UUID REFERENCES users(id) ON DELETE SET NULL,
    reporter_id UUID REFERENCES users(id) ON DELETE SET NULL,
    story_points INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5. Creating Performance Indexes
CREATE INDEX idx_issues_project ON issues(project_id);
CREATE INDEX idx_issues_parent ON issues(parent_issue_id); -- Speeds up hierarchical tree lookups

-- 6. Automation Trigger for 'updated_at' timestamp
CREATE OR REPLACE FUNCTION update_modified_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_issue_modtime
    BEFORE UPDATE ON issues
    FOR EACH ROW
    EXECUTE FUNCTION update_modified_column();