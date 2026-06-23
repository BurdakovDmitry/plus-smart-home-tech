package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.entity.ShoppingCart;
import ru.yandex.practicum.exception.UnauthorizedException;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository cartRepository;
    private final ShoppingCartMapper cartMapper;

    public ShoppingCartDto getShoppingCart(String username) {
        unauthorizedUser(username);
        ShoppingCart cart = checkShoppingCart(username);

        log.info("Получена корзина для пользователя: {}", username);
        return cartMapper.mapToShoppingCartDto(cart);
    }

    public ShoppingCartDto saveShoppingCart(String username, Map<UUID, Long> products) {
        unauthorizedUser(username);
        ShoppingCart cart = checkShoppingCart(username);

        cart.getProducts().clear();
        if (products != null) {
            cart.getProducts().putAll(products);
        }

        ShoppingCart savedCart = cartRepository.save(cart);

        log.info("Корзина для пользователя: {} сохранена", username);
        return cartMapper.mapToShoppingCartDto(savedCart);
    }

    public void deleteShoppingCart(String username) {
        unauthorizedUser(username);

        cartRepository.deleteByUsername(username);
        log.info("Корзина для пользователя: {} удалена", username);
    }

    public ShoppingCartDto removeShoppingCart(String username, List<UUID> productsId) {
        unauthorizedUser(username);
        ShoppingCart cart = checkShoppingCart(username);

        if (productsId != null) {
            productsId.forEach(cart.getProducts()::remove);
        }

        ShoppingCart savedCart = cartRepository.save(cart);

        log.info("Продукты {} из корзины пользователя: {} удалены", productsId, username);
        return cartMapper.mapToShoppingCartDto(savedCart);
    }

    public ShoppingCartDto updateQuantityShoppingCart(String username, ChangeProductQuantityRequest quantityRequest) {
        unauthorizedUser(username);
        ShoppingCart cart = checkShoppingCart(username);

        UUID productId = quantityRequest.productId();
        Long newQuantity = quantityRequest.newQuantity();

        if (newQuantity == 0) {
            cart.getProducts().remove(productId);
        } else {
            cart.getProducts().put(productId, newQuantity);
        }

        ShoppingCart savedCart = cartRepository.save(cart);

        log.info("Новое количество товара для пользователя {}: {}", username, quantityRequest);
        return cartMapper.mapToShoppingCartDto(savedCart);
    }

    private ShoppingCart createNewCart(String username) {
        log.info("Корзина не найдена. Создание новой пустой корзины для пользователя: {}", username);
        ShoppingCart newCart = new ShoppingCart();
        newCart.setUsername(username);
        newCart.setProducts(new HashMap<>());
        return cartRepository.save(newCart);
    }

    private ShoppingCart checkShoppingCart(String username) {
        return cartRepository.findByUsername(username).orElseGet(() -> createNewCart(username));
    }

    private void unauthorizedUser(String username) {
        if (username.isBlank()) {
            throw new UnauthorizedException("Имя пользователя не должно быть пустым");
        }
    }
}
