package com.example.capabilities.infrastructure.adapter;

import com.example.capabilities.domain.model.Capability;
import com.example.capabilities.domain.model.CapabilityWithTechnologies;
import com.example.capabilities.domain.model.TechnologySummary;
import com.example.capabilities.domain.spi.ICapabilityPersistencePort;
import com.example.capabilities.infrastructure.adapter.entity.CapabilityEntity;
import com.example.capabilities.infrastructure.adapter.entity.CapabilityTechnologyEntity;
import com.example.capabilities.infrastructure.adapter.mapper.CapabilityEntityMapper;
import com.example.capabilities.infrastructure.adapter.repository.CapabilityRepository;
import com.example.capabilities.infrastructure.adapter.repository.CapabilityTechnologyRepository;
import com.example.capabilities.infrastructure.entrypoints.dto.CapabilityTechnologyDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class CapabilityPersistenceAdapter implements ICapabilityPersistencePort {

    private final CapabilityRepository capabilityRepository;
    private final CapabilityTechnologyRepository capabilityTechnologyRepository;
    private final CapabilityEntityMapper capabilityEntityMapper;
    private final WebClient technologyWebClient;

    @Override
    public Mono<Capability> saveCapability(Capability capability) {
        CapabilityEntity entity = capabilityEntityMapper.toEntity(capability);
        return capabilityRepository.save(entity)
                .map(capabilityEntityMapper::toModel);
    }

    @Override
    public Mono<Void> saveCapabilityTechnologies(UUID capabilityId, List<UUID> technologies) {
        log.info("Saving capability technologies for {} -> {}", capabilityId, technologies);
        return Flux.fromIterable(technologies)
                .flatMap(techId -> capabilityTechnologyRepository.save(
                        CapabilityTechnologyEntity.builder()
                                .capabilityId(capabilityId)
                                .technologyId(techId)
                                .build()
                ).then())
                .then();
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return capabilityRepository.findByName(name)
                .map(e -> true)
                .defaultIfEmpty(false);
    }

    @Override
    public Mono<Boolean> validateTechnologiesExist(List<UUID> technologies) {
        log.info("Validating technologies exist (ids={}): contacting tech microservice", technologies);

        return technologyWebClient.post()
                .uri("/technologies/validate")
                .bodyValue(technologies)
                .retrieve()
                .bodyToFlux(CapabilityTechnologyDTO.class)
                .map(CapabilityTechnologyDTO::getId)
                .collectList()
                .map(foundIds -> {
                    log.info("Technologies found: {}", foundIds);
                    return foundIds.size() == technologies.size();
                })
                .onErrorResume(err -> {
                    log.error("Error validating technologies", err);
                    return Mono.just(false);
                });
    }

    @Override
    public Flux<CapabilityWithTechnologies> findAll(int page, int size, String sortBy, String order) {
        int offset = page * size;

        Flux<CapabilityEntity> baseFlux = getSortedCapabilityEntities(size, offset, sortBy, order);

        return baseFlux
                .flatMap(this::mapCapabilityWithTechnologies)
                .collectSortedList((c1, c2) -> compareCapabilities(c1, c2, sortBy, order))
                .flatMapMany(Flux::fromIterable);
    }

    private Flux<CapabilityEntity> getSortedCapabilityEntities(int size, int offset, String sortBy, String order) {
        if ("name".equalsIgnoreCase(sortBy)) {
            return "desc".equalsIgnoreCase(order)
                    ? capabilityRepository.findAllByPageDesc(size, offset)
                    : capabilityRepository.findAllByPageAsc(size, offset);
        }
        return capabilityRepository.findAllByPageAsc(size, offset);
    }

    private Mono<CapabilityWithTechnologies> mapCapabilityWithTechnologies(CapabilityEntity cap) {
        return capabilityTechnologyRepository.findByCapabilityId(cap.getId())
                .flatMap(rel -> technologyWebClient.get()
                        .uri("/technologies/{id}", rel.getTechnologyId())
                        .retrieve()
                        .bodyToMono(CapabilityTechnologyDTO.class)
                        .map(dto -> new TechnologySummary(dto.getId(), dto.getName()))
                )
                .collectList()
                .map(techs -> new CapabilityWithTechnologies(cap.getId(), cap.getName(), techs));
    }

    private int compareCapabilities(CapabilityWithTechnologies c1, CapabilityWithTechnologies c2, String sortBy, String order) {
        if ("techCount".equalsIgnoreCase(sortBy)) {
            int diff = Integer.compare(c1.technologies().size(), c2.technologies().size());
            if (diff == 0) return compareByName(c1, c2, order);
            return "desc".equalsIgnoreCase(order) ? -diff : diff;
        } else if ("name".equalsIgnoreCase(sortBy)) {
            int cmp = compareByName(c1, c2, order);
            if (cmp == 0) return c1.id().compareTo(c2.id());
            return cmp;
        }
        return 0;
    }

    private int compareByName(CapabilityWithTechnologies c1, CapabilityWithTechnologies c2, String order) {
        int cmp = c1.name().compareToIgnoreCase(c2.name());
        return "desc".equalsIgnoreCase(order) ? -cmp : cmp;
    }

    @Override
    public Mono<Long> countAll() {
        return capabilityRepository.count();
    }

    @Override
    public Flux<Capability> findAll() {
        return capabilityRepository.findAll()
                .map(entity -> new Capability(entity.getId(), entity.getName(), entity.getDescription()));
    }
}
