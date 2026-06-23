CREATE TABLE IF NOT EXISTS shopping_cart (
    shopping_cart_id UUID NOT NULL PRIMARY KEY,
    username         VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS shopping_cart_products (
    shopping_cart_id UUID NOT NULL REFERENCES shopping_cart (shopping_cart_id) ON DELETE CASCADE,
    product_id       UUID NOT NULL,
    quantity         BIGINT,

    PRIMARY KEY (shopping_cart_id, product_id)
);