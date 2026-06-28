package ru.yandex.practicum.dto.store;

public record SortObject(
        String direction,
        String nullHandling,
        Boolean ascending,
        String property,
        Boolean ignoreCase
) {}
