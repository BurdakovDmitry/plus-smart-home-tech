package ru.yandex.practicum.entity;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dto.warehouse.AddressDto;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class Warehouse {
    private static final String[] ADDRESSES = new String[] {"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    private final AddressDto warehouseAddress;

    public Warehouse() {
        this.warehouseAddress = new AddressDto(
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS);
    }

    public AddressDto getAddress() {
        return this.warehouseAddress;
    }
}