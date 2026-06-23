package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record DimensionDto(
        @NotNull
        @DecimalMin(value = "1.0")
        Double width,

        @NotNull
        @DecimalMin(value = "1.0")
        Double height,

        @NotNull
        @DecimalMin(value = "1.0")
        Double depth
) {}
