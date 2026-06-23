package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController {
    private final ShoppingCartService cartService;

    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam String username) {
        log.info("GET /api/v1/shopping-cart: userName={}", username);
        return cartService.getShoppingCart(username);
    }

    @PutMapping
    ShoppingCartDto saveShoppingCart(@RequestParam String username,
                                     @RequestBody Map<UUID, Long> products) {
        log.info("PUT /api/v1/shopping-cart: userName={}, products={}", username, products);
        return cartService.saveShoppingCart(username, products);
    }

    @DeleteMapping
    void deleteShoppingCart(@RequestParam String username) {
        log.info("DELETE /api/v1/shopping-cart: userName={}", username);
        cartService.deleteShoppingCart(username);
    }

    @PostMapping("/remove")
    ShoppingCartDto removeShoppingCart(@RequestParam String username,
                                       @RequestBody List<UUID> productsId) {
        log.info("POST /api/v1/shopping-cart/remove: userName={}, productsId={}", username, productsId);
        return cartService.removeShoppingCart(username, productsId);
    }

    @PostMapping("/change-quantity")
    ShoppingCartDto updateQuantityShoppingCart(@RequestParam String username,
                                               @Valid @RequestBody ChangeProductQuantityRequest quantityRequest) {
        log.info("POST /api/v1/shopping-cart/change-quantity: userName={}, quantityRequest={}", username, quantityRequest);
        return cartService.updateQuantityShoppingCart(username, quantityRequest);
    }
}
