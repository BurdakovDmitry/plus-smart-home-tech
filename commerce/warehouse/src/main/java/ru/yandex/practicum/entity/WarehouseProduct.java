package ru.yandex.practicum.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "warehouse_products")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseProduct {
    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Column(nullable = false)
    private Long quantity;
    
    private Boolean fragile;

    @Column(nullable = false)
    private Double weight;

    @Embedded
    @Column(nullable = false)
    private Dimension dimension;
}
