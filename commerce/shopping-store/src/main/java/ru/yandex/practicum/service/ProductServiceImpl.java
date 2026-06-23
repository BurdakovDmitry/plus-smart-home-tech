package ru.yandex.practicum.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.store.PageProductDto;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.ProductParamDto;
import ru.yandex.practicum.dto.store.SetProductQuantityStateRequest;
import ru.yandex.practicum.entity.Product;
import ru.yandex.practicum.entity.QProduct;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.mapper.ProductMapper;
import ru.yandex.practicum.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;


    @Override
    public PageProductDto getProduct(ProductParamDto param) {
        List<Sort.Order> orders = new ArrayList<>();

        if (param.sort() != null && !param.sort().isEmpty()) {
            for (String sortParam : param.sort()) {
                String[] parts = sortParam.split(",");
                String property = parts[0].trim();

                Sort.Direction direction = Sort.Direction.ASC;

                if (parts.length > 1) {
                    String directionStr = parts[1].trim();
                    if ("desc".equalsIgnoreCase(directionStr)) {
                        direction = Sort.Direction.DESC;
                    }
                }

                orders.add(new Sort.Order(direction, property));
            }
        }

        Pageable pageable = PageRequest.of(param.page(), param.size(), orders.isEmpty() ? Sort.unsorted() : Sort.by(orders)
        );

        // Строим динамический QueryDSL
        QProduct qProduct = QProduct.product;
        BooleanExpression predicate = qProduct.productCategory.eq(param.category());

        Page<Product> productPage = productRepository.findAll(predicate, pageable);

        log.info("Получен список продуктов по указанным фильтрам");
        return productMapper.mapToPageProductDto(productPage);
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар с id=" + productId + " не найден"));

        log.info("Получен товар с id={}", productId);
        return productMapper.mapToProductDto(product);
    }

    @Override
    @Transactional
    public ProductDto saveProduct(ProductDto productDto) {
        Product product = productMapper.mapToProduct(productDto);

        Product savedProduct = productRepository.save(product);

        log.info("Товар успешно сохранен с id={}", savedProduct.getProductId());
        return productMapper.mapToProductDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(ProductDto productDto) {
        if (productDto.productId() == null) {
            throw new ProductNotFoundException("Невозможно обновить. Товар не найден");
        }

        Product oldProduct = productRepository.findById(productDto.productId())
                .orElseThrow(() -> new ProductNotFoundException("Невозможно обновить. Товар не найден"));

        productMapper.updateProductFromDto(productDto, oldProduct);

        Product updatedProduct = productRepository.save(oldProduct);

        log.info("Товар успешно обновлен с id={}", productDto.productId());
        return productMapper.mapToProductDto(updatedProduct);
    }

    @Override
    @Transactional
    public Boolean removeProduct(UUID productId) {
        if (!productRepository.existsById(productId)) {
            log.warn("Товар с id={} не найден", productId);
            return false;
        }

        productRepository.deleteById(productId);
        log.info("Товар успешно удален с id={}", productId);
        return true;
    }

    @Override
    @Transactional
    public Boolean updateStateProduct(SetProductQuantityStateRequest quantityState) {
        Product product = productRepository.findById(quantityState.productId()).orElse(null);
        if (product == null) {
            log.warn("Товар для изменения статуса количества не найден: id={}", quantityState.productId());
            return false;
        }

        product.setQuantityState(quantityState.quantityState());
        productRepository.save(product);

        log.info("Статус количества товара успешно обновлен");
        return true;
    }
}
