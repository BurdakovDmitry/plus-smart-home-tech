CREATE TABLE IF NOT EXISTS products (
    product_id       UUID NOT NULL PRIMARY KEY,
    product_name     VARCHAR(255) NOT NULL,
    description      VARCHAR(5000) NOT NULL,
    image_src        VARCHAR(250),
    quantity_state   VARCHAR(255) NOT NULL,
    product_state    VARCHAR(255) NOT NULL,
    product_category VARCHAR(255),
    price            DOUBLE PRECISION NOT NULL
);