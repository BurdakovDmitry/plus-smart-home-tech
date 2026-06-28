package ru.yandex.practicum.dto.store;

import jakarta.validation.constraints.DecimalMin;
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

        @DecimalMin(value = "1.0")
        @NotNull
        Double price
){}
