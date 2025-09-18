CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS capability (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS capability_technology (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    capability_id UUID NOT NULL,
    technology_id UUID NOT NULL,
    CONSTRAINT fk_capability FOREIGN KEY (capability_id) REFERENCES capability (id) ON DELETE CASCADE,
    CONSTRAINT uq_capability_technology UNIQUE (capability_id, technology_id)
);
