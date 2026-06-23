package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.store.PageProductDto;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.ProductParamDto;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;

import java.util.UUID;

public interface ProductService {
    PageProductDto getProduct(ProductParamDto param);

    ProductDto getProductById(UUID productId);

    ProductDto updateProduct(ProductDto productDto);

    ProductDto saveProduct(ProductDto productDto);

    Boolean removeProduct(UUID productId);

    Boolean updateStateProduct(SetProductQuantityStateRequest quantityState);
}
