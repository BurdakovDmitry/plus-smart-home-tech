package ru.yandex.practicum.dto.warehouse;

public record AddressDto(
        String country,
        String city,
        String street,
        String house,
        String flat
) {}
