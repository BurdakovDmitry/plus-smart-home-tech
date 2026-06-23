package ru.yandex.practicum.contract.warehouse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;

@Slf4j
@Component
public class WarehouseClientFallbackFactory implements FallbackFactory<WarehouseClient> {

    @Override
    public WarehouseClient create(Throwable cause) {
        log.error("Сбой при вызове сервиса warehouse. Причина: {}", cause.getMessage());

        return new WarehouseClient() {
            @Override
            public void addNewProduct(NewProductInWarehouseRequest request) {
                log.error("Fallback: Не удалось добавить новый товар {}. Сервис склада недоступен.", request.productId());
            }

            @Override
            public BookedProductsDto checkBookedProducts(ShoppingCartDto cartDto) {
                log.error("Fallback: Не удалось проверить доступность товаров по корзине {}. Сервис склада недоступен.", cartDto.shoppingCartId());
                return new BookedProductsDto(0.0, 0.0, false);
            }

            @Override
            public void addProductQuantity(AddProductToWarehouseRequest request) {
                log.error("Fallback: Не удалось увеличить количество товара {}. Сервис склада недоступен.", request.productId());
            }

            @Override
            public AddressDto getAddress() {
                log.error("Fallback: Не удалось получить адрес склада. Сервис склада недоступен.");
                return new AddressDto("FALLBACK_UNKNOWN", "FALLBACK_UNKNOWN", "FALLBACK_UNKNOWN", "FALLBACK_UNKNOWN", "FALLBACK_UNKNOWN");
            }
        };
    }
}
