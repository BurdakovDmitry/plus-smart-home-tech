package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NewProductInWarehouseRequest(
        @NotNull
        UUID productId,

        Boolean fragile,

        @NotNull
        DimensionDto dimension,

        @NotNull
        @DecimalMin(value = "1.0")
        Double weight
) {}
