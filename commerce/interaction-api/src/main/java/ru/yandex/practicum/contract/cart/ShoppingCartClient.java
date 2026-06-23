package ru.yandex.practicum.contract.cart;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart", fallback = ShoppingCartClientFallbackFactory.class)
public interface ShoppingCartClient {
    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam("username") String username);

    @PutMapping
    ShoppingCartDto saveShoppingCart(@RequestParam("username") String username,
                                     @RequestBody Map<UUID, Long> products);

    @DeleteMapping
    void deleteShoppingCart(@RequestParam("username") String username);

    @PostMapping("/remove")
    ShoppingCartDto removeShoppingCart(@RequestParam("username") String username,
                                       @RequestBody List<UUID> productsId);

    @PostMapping("/change-quantity")
    ShoppingCartDto updateQuantityShoppingCart(@RequestParam("username") String username,
                                               @RequestBody ChangeProductQuantityRequest quantityRequest);
}
