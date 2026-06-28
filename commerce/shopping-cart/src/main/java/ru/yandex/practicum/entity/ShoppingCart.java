package ru.yandex.practicum.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "shopping_cart")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "shopping_cart_id")
    private UUID shoppingCartId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "shopping_cart_products",
            joinColumns = @JoinColumn(name = "shopping_cart_id")
    )
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    @ToString.Exclude
    private Map<UUID, Long> products;
}
