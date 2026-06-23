package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.entity.ShoppingCart;

@Mapper(componentModel = "spring")
public interface ShoppingCartMapper {
    ShoppingCartDto mapToShoppingCartDto(ShoppingCart entity);
}
