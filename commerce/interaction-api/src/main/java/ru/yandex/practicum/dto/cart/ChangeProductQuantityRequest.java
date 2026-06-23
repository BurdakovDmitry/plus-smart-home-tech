package ru.yandex.practicum.dto.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record ChangeProductQuantityRequest(
        @NotNull
        UUID productId,

        @NotNull
        @PositiveOrZero
        Long newQuantity
) {}
