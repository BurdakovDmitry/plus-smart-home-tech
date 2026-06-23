package ru.yandex.practicum.service;

import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ShoppingCartService {
    ShoppingCartDto getShoppingCart(String username);

    ShoppingCartDto saveShoppingCart(String username, Map<UUID, Long> products);

    void deleteShoppingCart(String username);

    ShoppingCartDto removeShoppingCart(String username, List<UUID> productsId);

    ShoppingCartDto updateQuantityShoppingCart(String username, ChangeProductQuantityRequest quantityRequest);
}
