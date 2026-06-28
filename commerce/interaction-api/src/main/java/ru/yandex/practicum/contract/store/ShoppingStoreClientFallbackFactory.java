package ru.yandex.practicum.contract.store;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.store.PageProductDto;
import ru.yandex.practicum.dto.store.PageableObject;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.ProductState;
import ru.yandex.practicum.dto.store.QuantityState;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.dto.store.SortObject;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class ShoppingStoreClientFallbackFactory implements FallbackFactory<ShoppingStoreClient> {

    @Override
    public ShoppingStoreClient create(Throwable cause) {
        log.error("Сбой при вызове сервиса shopping-store. Причина: {}", cause.getMessage());

        return new ShoppingStoreClient() {
            @Override
            public PageProductDto getProduct(ProductCategory category, int page, int size, List<String> sort) {
                log.error("Fallback response: сервис временно недоступен");

                List<ProductDto> emptyContent = List.of();
                List<SortObject> emptySort = List.of();

                return new PageProductDto(
                        0L,
                        0,
                        true,
                        true,
                        size,
                        emptyContent,
                        page,
                        emptySort,
                        0,
                        new PageableObject(0L, emptySort, true, false, page, size),
                        true
                );
            }

            @Override
            public ProductDto getProductById(UUID productId) {
                log.error("Fallback response: сервис временно недоступен");
                return new ProductDto(
                        productId,
                        "Товар",
                        "временно",
                        "недоступен",
                        QuantityState.ENDED,
                        ProductState.DEACTIVATE,
                        null,
                        0.0
                );
            }

            @Override
            public ProductDto updateProduct(ProductDto productDto) {
                log.error("Fallback response: сервис временно недоступен");
                return new ProductDto(
                        productDto.productId(),
                        "Товар",
                        "временно",
                        "недоступен",
                        QuantityState.ENDED,
                        ProductState.DEACTIVATE,
                        null,
                        0.0
                );
            }

            @Override
            public ProductDto saveProduct(ProductDto productDto) {
                log.error("Fallback response: сервис временно недоступен");
                return new ProductDto(
                        productDto.productId(),
                        "Товар",
                        "временно",
                        "недоступен",
                        QuantityState.ENDED,
                        ProductState.DEACTIVATE,
                        null,
                        0.0
                );
            }

            @Override
            public Boolean removeProduct(UUID productId) {
                log.error("Fallback response: сервис временно недоступен");
                return false;
            }

            @Override
            public Boolean updateStateProduct(SetProductQuantityStateRequest quantityState) {
                log.error("Fallback response: сервис временно недоступен");
                return false;
            }
        };
    }
}