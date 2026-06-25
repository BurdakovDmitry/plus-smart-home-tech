package ru.yandex.practicum.dto.store;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProductDto (
        UUID productId,

        @NotBlank
        String productName,

        @NotBlank
        String description,

        String imageSrc,

        @NotNull
        QuantityState quantityState,

        @NotNull
        ProductState productState,

        ProductCategory productCategory,

        @Min(value = 1)
        @NotNull
        Double price
){}
