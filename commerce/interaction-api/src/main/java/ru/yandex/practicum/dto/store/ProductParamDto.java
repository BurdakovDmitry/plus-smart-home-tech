package ru.yandex.practicum.dto.store;

import java.util.List;

public record ProductParamDto(
        ProductCategory category,
        int page,
        int size,
        List<String> sort
) {}
