package ru.yandex.practicum.contract.cart;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class ShoppingCartClientFallbackFactory implements FallbackFactory<ShoppingCartClient> {

    private static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @Override
    public ShoppingCartClient create(Throwable cause) {
        log.error("Сбой при вызове сервиса shopping-cart. Причина: {}", cause.getMessage());

        return new ShoppingCartClient() {
            @Override
            public ShoppingCartDto getShoppingCart(String username) {
                log.error("Fallback response: сервис временно недоступен");
                return new ShoppingCartDto(EMPTY_UUID, Map.of());
            }

            @Override
            public ShoppingCartDto saveShoppingCart(String username, Map<UUID, Long> products) {
                log.error("Fallback response: сервис временно недоступен");
                return new ShoppingCartDto(EMPTY_UUID, Map.of());
            }

            @Override
            public void deleteShoppingCart(String username) {
                log.error("Fallback response: сервис временно недоступен");
            }

            @Override
            public ShoppingCartDto removeShoppingCart(String username, List<UUID> productsId) {
                log.error("Fallback response: сервис временно недоступен");
                return new ShoppingCartDto(EMPTY_UUID, Map.of());
            }

            @Override
            public ShoppingCartDto updateQuantityShoppingCart(String username, ChangeProductQuantityRequest quantityRequest) {
                log.error("Fallback response: сервис временно недоступен");
                return new ShoppingCartDto(EMPTY_UUID, Map.of());
            }
        };
    }
}
