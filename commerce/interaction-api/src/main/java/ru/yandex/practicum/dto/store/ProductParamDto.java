package ru.yandex.practicum.dto.store;

import org.springframework.data.domain.Pageable;

public record ProductParamDto(
        ProductCategory category,
        Pageable pageable
) {}
