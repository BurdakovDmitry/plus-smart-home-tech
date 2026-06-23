package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;

public record BookedProductsDto(
        @NotNull
        Double deliveryWeight,

        @NotNull
        Double deliveryVolume,

        @NotNull
        Boolean fragile
) {}
