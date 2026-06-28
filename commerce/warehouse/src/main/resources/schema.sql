CREATE TABLE IF NOT EXISTS warehouse_products (
    product_id   UUID NOT NULL PRIMARY KEY,
    quantity     BIGINT NOT NULL,
    fragile      BOOLEAN,
    weight       DOUBLE PRECISION NOT NULL,
    width        DOUBLE PRECISION NOT NULL,
    height       DOUBLE PRECISION NOT NULL,
    depth        DOUBLE PRECISION NOT NULL
);