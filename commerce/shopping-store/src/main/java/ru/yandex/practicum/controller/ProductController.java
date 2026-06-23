package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.dto.store.PageProductDto;
import ru.yandex.practicum.dto.store.ProductCategory;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.ProductParamDto;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.service.ProductService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/shopping-store")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    PageProductDto getProduct(@RequestParam ProductCategory category,
                              @PositiveOrZero @RequestParam(defaultValue = "0") int page,
                              @Positive @RequestParam(defaultValue = "20") int size,
                              @RequestParam(required = false) List<String> sort) {
        ProductParamDto param = new ProductParamDto(category, page, size, sort);
        log.info("GET /api/v1/shopping-store: {}", param);
        return productService.getProduct(param);
    }

    @GetMapping("/{productId}")
    ProductDto getProductById(@PathVariable("productId") UUID productId) {
        log.info("GET /api/v1/shopping-store/{productId}: id={}", productId);
        return productService.getProductById(productId);
    }

    @PutMapping
    ProductDto saveProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("PUT /api/v1/shopping-store: {}", productDto);
        return productService.saveProduct(productDto);
    }

    @PostMapping
    ProductDto updateProduct(@Valid @RequestBody ProductDto productDto) {
        log.info("POST /api/v1/shopping-store: {}", productDto);
        return productService.updateProduct(productDto);
    }

    @PostMapping("/removeProductFromStore")
    Boolean removeProduct(@RequestBody UUID productId) {
        log.info("POST /api/v1/shopping-store/removeProductFromStore: {}", productId);
        return productService.removeProduct(productId);
    }

    @PostMapping("/quantityState")
    Boolean updateStateProduct(@Valid @RequestBody SetProductQuantityStateRequest quantityState) {
        log.info("POST /api/v1/shopping-store/quantityState: {}", quantityState);
        return productService.updateStateProduct(quantityState);
    }
}
