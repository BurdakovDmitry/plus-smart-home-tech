package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.warehouse.DimensionDto;
import ru.yandex.practicum.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.entity.Dimension;
import ru.yandex.practicum.entity.WarehouseProduct;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    @Mapping(target = "quantity", ignore = true)
    WarehouseProduct mapToWarehouseProduct(NewProductInWarehouseRequest request);

    Dimension toEmbeddable(DimensionDto dto);
}
