package ru.yandex.practicum.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record AddProductToWarehouseRequest(
        UUID productId,

        @NotNull
        @Positive
        Long quantity
) {}
