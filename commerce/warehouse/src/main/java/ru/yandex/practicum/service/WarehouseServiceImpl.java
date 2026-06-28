package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.dto.warehouse.AddressDto;
import ru.yandex.practicum.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.entity.Dimension;
import ru.yandex.practicum.entity.Warehouse;
import ru.yandex.practicum.entity.WarehouseProduct;
import ru.yandex.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.exception.ProductNotFoundException;
import ru.yandex.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.mapper.WarehouseMapper;
import ru.yandex.practicum.repository.WarehouseRepository;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final Warehouse warehouse;

    @Override
    @Transactional
    public void addNewProduct(NewProductInWarehouseRequest request) {
        if (warehouseRepository.existsById(request.productId())) {
            throw new SpecifiedProductAlreadyInWarehouseException(
                    "Товар с id=" + request.productId() + " уже зарегистрирован на складе");
        }

        WarehouseProduct product = warehouseMapper.mapToWarehouseProduct(request);
        product.setQuantity(1L);
        warehouseRepository.save(product);

        log.info("Товар {} успешно добавлен на склад", product);
    }

    @Override
    @Transactional
    public BookedProductsDto checkBookedProducts(ShoppingCartDto cartDto) {
        Map<UUID, Long> requestedProducts = cartDto.products();

        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragile = false;

        for (Map.Entry<UUID, Long> entry : requestedProducts.entrySet()) {
            UUID productId = entry.getKey();
            Long quantity = entry.getValue();

            WarehouseProduct warehouseProduct = warehouseRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException("Товар с id=" + productId + " отсутствует на складе"));

            if (warehouseProduct.getQuantity() < quantity) {
                long missingQuantity = quantity - warehouseProduct.getQuantity();
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Недостаточно товара на складе. id=" + productId + ", не хватает: " + missingQuantity);
            }

            totalWeight += warehouseProduct.getWeight() * quantity;

            Dimension dimension = warehouseProduct.getDimension();
            double volume = dimension.getWidth() * dimension.getHeight() * dimension.getDepth();
            totalVolume += volume * quantity;

            if (Boolean.TRUE.equals(warehouseProduct.getFragile())) {
                hasFragile = true;
            }
        }

        log.info("Сведения по корзине: вес доставки={}, объем доставки={}, хрупкое={}", totalWeight, totalVolume, hasFragile);

        return new BookedProductsDto(totalWeight, totalVolume, hasFragile);
    }

    @Override
    @Transactional
    public void addProductQuantity(AddProductToWarehouseRequest request) {
        log.info("Администратор увеличивает количество товара {} на {}", request.productId(), request.quantity());

        WarehouseProduct product = warehouseRepository.findById(request.productId())
                .orElseThrow(() ->
                        new NoSpecifiedProductInWarehouseException("Товара с id=" + request.productId() + " нет на складе"));

        product.setQuantity(product.getQuantity() + request.quantity());

        log.info("Увеличение количества товара {} на {}", request.productId(), request.quantity());
    }

    @Override
    public AddressDto getAddress() {
        AddressDto address = warehouse.getAddress();
        log.info("Адрес склада успешно получен");
        return address;
    }
}
