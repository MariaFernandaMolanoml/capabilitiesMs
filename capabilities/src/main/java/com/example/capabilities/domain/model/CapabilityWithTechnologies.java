package com.example.capabilities.domain.model;

import java.util.List;
import java.util.UUID;

public record CapabilityWithTechnologies(UUID id, String name, List<TechnologySummary> technologies) {}


