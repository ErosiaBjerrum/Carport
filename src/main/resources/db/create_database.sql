-- create_database.sql
-- Kør først DROP/CREATE DATABASE mens du er forbundet til standarddatabasen "postgres".
-- Derefter skal du skifte connection til databasen "fog_carport" og køre resten af scriptet fra CREATE TABLE zip_code og ned.

DROP DATABASE IF EXISTS fog_carport;
CREATE DATABASE fog_carport;

-- =====================================================================
-- Kør nedenstående EFTER du har skiftet connection til databasen fog_carport
-- =====================================================================

SELECT * FROM material;

DROP TABLE IF EXISTS customer_order CASCADE;
DROP TABLE IF EXISTS bom_line CASCADE;
DROP TABLE IF EXISTS bill_of_material CASCADE;
DROP TABLE IF EXISTS offer CASCADE;
DROP TABLE IF EXISTS carport CASCADE;
DROP TABLE IF EXISTS carport_request CASCADE;
DROP TABLE IF EXISTS user_account CASCADE;
DROP TABLE IF EXISTS material CASCADE;
DROP TABLE IF EXISTS zip_code CASCADE;

CREATE TABLE zip_code (
    zip_code INTEGER PRIMARY KEY,
    city VARCHAR(100) NOT NULL
);

CREATE TABLE user_account (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(30) NOT NULL,
    address VARCHAR(255) NOT NULL,
    zip_code INTEGER NOT NULL,
    role VARCHAR(50) NOT NULL,

    CONSTRAINT fk_user_zip_code
        FOREIGN KEY (zip_code)
        REFERENCES zip_code(zip_code)
);

CREATE TABLE carport_request (
    request_id SERIAL PRIMARY KEY,
    user_id INTEGER,
    contact_name VARCHAR(100) NOT NULL,
    contact_email VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(30) NOT NULL,
    request_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_request_user
        FOREIGN KEY (user_id)
        REFERENCES user_account(user_id)
);

CREATE TABLE carport (
    carport_id SERIAL PRIMARY KEY,
    request_id INTEGER NOT NULL UNIQUE,
    height INTEGER NOT NULL,
    length INTEGER NOT NULL,
    width INTEGER NOT NULL,

    CONSTRAINT fk_carport_request
        FOREIGN KEY (request_id)
        REFERENCES carport_request(request_id)
        ON DELETE CASCADE
);

CREATE TABLE offer (
    offer_id SERIAL PRIMARY KEY,
    request_id INTEGER NOT NULL UNIQUE,
    total_price NUMERIC(10,2) NOT NULL,

    CONSTRAINT fk_offer_request
        FOREIGN KEY (request_id)
        REFERENCES carport_request(request_id)
        ON DELETE CASCADE
);

CREATE TABLE bill_of_material (
    bom_id SERIAL PRIMARY KEY,
    offer_id INTEGER NOT NULL UNIQUE,

    CONSTRAINT fk_bom_offer
        FOREIGN KEY (offer_id)
        REFERENCES offer(offer_id)
        ON DELETE CASCADE
);

CREATE TABLE material (
    material_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL
);

CREATE TABLE bom_line (
    line_id SERIAL PRIMARY KEY,
    material_id INTEGER NOT NULL,
    bom_id INTEGER NOT NULL,
    quantity NUMERIC(10,2) NOT NULL,

    CONSTRAINT fk_bom_line_material
        FOREIGN KEY (material_id)
        REFERENCES material(material_id),

    CONSTRAINT fk_bom_line_bom
        FOREIGN KEY (bom_id)
        REFERENCES bill_of_material(bom_id)
        ON DELETE CASCADE
);

CREATE TABLE customer_order (
    order_id SERIAL PRIMARY KEY,
    offer_id INTEGER NOT NULL UNIQUE,
    user_id INTEGER NOT NULL,
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_order_offer
        FOREIGN KEY (offer_id)
        REFERENCES offer(offer_id),

    CONSTRAINT fk_order_user
        FOREIGN KEY (user_id)
        REFERENCES user_account(user_id)
);

-- Simple testdata
INSERT INTO zip_code (zip_code, city) VALUES
(3700, 'Rønne'),
(2100, 'København Ø'),
(8000, 'Aarhus C');

INSERT INTO material (name, unit, unit_price) VALUES
('Stolpe 97x97 mm', 'stk', 149.95),
('Rem 45x195 mm', 'meter', 89.95),
('Spærtræ 45x195 mm', 'meter', 79.95),
('Tagplade plasttrapez', 'stk', 129.95),
('Skruer', 'pakke', 59.95);
