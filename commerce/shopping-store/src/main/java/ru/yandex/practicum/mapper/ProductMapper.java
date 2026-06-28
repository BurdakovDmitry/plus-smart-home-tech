package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.yandex.practicum.dto.store.PageProductDto;
import ru.yandex.practicum.dto.store.PageableObject;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.SortObject;
import ru.yandex.practicum.entity.Product;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDto mapToProductDto(Product product);

    @Mapping(target = "productId", ignore = true)
    Product mapToProduct(ProductDto productDto);

    void updateProductFromDto(ProductDto productDto, @MappingTarget Product product);

    @Mapping(target = "content", source = "page.content")
    @Mapping(target = "sort", source = "page.sort")
    PageProductDto mapToPageProductDto(Page<Product> page);

    PageableObject toPageableObject(Pageable pageable);

    SortObject toSortObject(Sort.Order order);

    List<SortObject> toSortObjectList(Sort sort);
}
