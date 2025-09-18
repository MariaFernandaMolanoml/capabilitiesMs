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
                .flatMap(techId -> {
                    CapabilityTechnologyEntity rel = CapabilityTechnologyEntity.builder()
                            .capabilityId(capabilityId)
                            .technologyId(techId)
                            .build();
                    return capabilityTechnologyRepository.save(rel).then();
                })
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

        Flux<CapabilityEntity> baseFlux;

        if ("name".equalsIgnoreCase(sortBy)) {
            baseFlux = "desc".equalsIgnoreCase(order)
                    ? capabilityRepository.findAllByPageDesc(size, offset)
                    : capabilityRepository.findAllByPageAsc(size, offset);
        } else {
            baseFlux = capabilityRepository.findAllByPageAsc(size, offset);
        }

        return baseFlux.flatMap(cap ->
                capabilityTechnologyRepository.findByCapabilityId(cap.getId())
                        .flatMap(rel -> technologyWebClient.get()
                                .uri("/technologies/{id}", rel.getTechnologyId())
                                .retrieve()
                                .bodyToMono(CapabilityTechnologyDTO.class)
                                .map(dto -> new TechnologySummary(dto.getId(), dto.getName()))
                        )
                        .collectList()
                        .map(techs -> new CapabilityWithTechnologies(cap.getId(), cap.getName(), techs))
        ).collectSortedList((c1, c2) -> {
            if ("techCount".equalsIgnoreCase(sortBy)) {
                int diff = Integer.compare(c1.technologies().size(), c2.technologies().size());
                return "desc".equalsIgnoreCase(order) ? -diff : diff;
            }
            return 0;
        }).flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Long> countAll() {
        return capabilityRepository.count();
    }

}
