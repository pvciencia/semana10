-- ============================================================
-- FerroVoz — Script de base de datos NUEVO
-- Esquema: usuario, cliente, producto, venta, detalle_venta
-- MySQL 8+ — Ejecutar ANTES de iniciar Spring Boot
-- ============================================================

CREATE DATABASE IF NOT EXISTS ferrovoz_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ferrovoz_db;

-- ────────────────────────────────────────────────────────────
-- 1. USUARIO
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS usuario (
    id       INT          NOT NULL AUTO_INCREMENT,
    username VARCHAR(50)  NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol      VARCHAR(20),                       -- 'ADMIN' | 'VENDEDOR'
    CONSTRAINT pk_usuario    PRIMARY KEY (id),
    CONSTRAINT uq_username   UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
-- 2. CLIENTE
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS cliente (
    id        INT          NOT NULL AUTO_INCREMENT,
    nombre    VARCHAR(100) NOT NULL,
    dni       VARCHAR(8)   NOT NULL,
    telefono  VARCHAR(15),
    correo    VARCHAR(100),
    direccion VARCHAR(150),
    CONSTRAINT pk_cliente    PRIMARY KEY (id),
    CONSTRAINT uq_cliente_dni UNIQUE (dni)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
-- 3. PRODUCTO
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS producto (
    id          INT           NOT NULL AUTO_INCREMENT,
    nombre      VARCHAR(100)  NOT NULL,
    categoria   VARCHAR(50),
    precio      DECIMAL(10,2) NOT NULL,
    stock       INT           NOT NULL DEFAULT 0,
    descripcion TEXT,
    imagen      LONGTEXT,
    CONSTRAINT pk_producto PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
-- 4. VENTA
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS venta (
    id          INT           NOT NULL AUTO_INCREMENT,
    cliente_id  INT           NOT NULL,
    usuario_id  INT,
    fecha       DATETIME      NOT NULL,
    total       DECIMAL(10,2) NOT NULL,
    estado      VARCHAR(20)   NOT NULL DEFAULT 'Pendiente',  -- 'Pendiente'|'Confirmado'|'Cancelado'
    metodo_pago VARCHAR(50),                                   -- 'Efectivo'|'Tarjeta'|'Transferencia'
    CONSTRAINT pk_venta         PRIMARY KEY (id),
    CONSTRAINT fk_venta_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE RESTRICT,
    CONSTRAINT fk_venta_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
-- 5. DETALLE_VENTA
-- ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS detalle_venta (
    id          INT           NOT NULL AUTO_INCREMENT,
    venta_id    INT           NOT NULL,
    producto_id INT           NOT NULL,
    cantidad    INT           NOT NULL,
    precio      DECIMAL(10,2) NOT NULL,
    CONSTRAINT pk_detalle         PRIMARY KEY (id),
    CONSTRAINT fk_detalle_venta   FOREIGN KEY (venta_id)    REFERENCES venta(id)    ON DELETE CASCADE,
    CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ────────────────────────────────────────────────────────────
-- 6. DATOS DE PRUEBA
-- ────────────────────────────────────────────────────────────

INSERT IGNORE INTO usuario (username, password, rol) VALUES
    ('admin',    '$2a$10$placeholder_hash_admin',   'ADMIN'),
    ('vendedor', '$2a$10$placeholder_hash_vendedor', 'VENDEDOR');

INSERT IGNORE INTO cliente (nombre, dni, telefono, correo, direccion) VALUES
    ('Quispe García, Juan Carlos', '74512389', '987654321', 'jquispe@gmail.com',   'Av. Los Álamos 123, SJL'),
    ('Rojas Mamani, Sandra',       '61234567', '956123789', 'srojas@hotmail.com',  'Jr. Lima 456, Cercado de Lima'),
    ('Torres Villanueva, Miguel',  '45678901', '945678012', 'mtorres@gmail.com',   'Calle Las Flores 789, La Molina'),
    ('Huanca López, Patricia',     '38901234', '912345678', 'phuanca@outlook.com', 'Av. Industrial 342, VES'),
    ('Condori Silva, Roberto',     '52345678', '934567890', null,                  null);

INSERT IGNORE INTO producto (nombre, categoria, precio, stock, descripcion) VALUES
    ('Martillo de Carpintero 16 oz',  'Herramientas',  38.50, 24, 'Cabeza de acero forjado, mango de fibra de vidrio.'),
    ('Taladro Percutor 750W',         'Eléctrico',    189.90, 50, 'Velocidad variable, mandril 13 mm. Incluye maletín.'),
    ('Llave Francesa Ajustable 12"',  'Plomería',      54.00,  3, 'Acero cromo-vanadio, apertura máxima 34 mm.'),
    ('Pintura Látex Blanca 4L',       'Pinturas',      62.00, 42, 'Interior/exterior, lavable, alta cobertura.'),
    ('Clavos de Acero 2.5" (kg)',     'Fijaciones',     8.50,  0, 'Clavos brillantes para madera, calibre 14.'),
    ('Sierra Circular 1200W 7¼"',    'Eléctrico',    320.00, 15, 'Disco 184mm, corte biselado hasta 48°.'),
    ('Destornillador Eléctrico 3.6V', 'Eléctrico',     89.90, 30, 'Compacto, 2 velocidades, carga USB-C.'),
    ('Tubo PVC Presión 1/2" (metro)', 'Plomería',       4.80,200, 'PVC rígido clase 10, apto agua potable.'),
    ('Disco de Corte Metal 4.5"',    'Herramientas',  18.50, 60, 'Para amoladora angular, pack de 10 discos.'),
    ('Cemento Gris Portland 42.5 kg', 'Construcción',  32.00, 80, 'Tipo I, alta resistencia inicial.'),
    ('Cinta Métrica 5m Stanley',      'Herramientas',  15.90, 45, 'Carcasa de caucho, bloqueo automático.'),
    ('Pegamento Epóxico Bicomponente','Fijaciones',     12.50,  5, 'Resistencia máxima metal-metal, fraguado 5 min.');

-- Ventas de ejemplo
INSERT IGNORE INTO venta (cliente_id, usuario_id, fecha, total, estado, metodo_pago)
    SELECT c.id, 1, '2026-06-01 10:30:00', 379.80, 'Confirmado', 'Efectivo'
    FROM cliente c WHERE c.dni = '74512389';

INSERT IGNORE INTO venta (cliente_id, usuario_id, fecha, total, estado, metodo_pago)
    SELECT c.id, 2, '2026-06-03 14:15:00', 192.50, 'Pendiente', 'Tarjeta'
    FROM cliente c WHERE c.dni = '61234567';

INSERT IGNORE INTO venta (cliente_id, usuario_id, fecha, total, estado, metodo_pago)
    SELECT c.id, 1, '2026-06-05 09:00:00', 320.00, 'Cancelado', 'Transferencia'
    FROM cliente c WHERE c.dni = '45678901';

-- Detalles de las ventas
INSERT IGNORE INTO detalle_venta (venta_id, producto_id, cantidad, precio)
    SELECT v.id, p.id, 2, p.precio
    FROM venta v
    JOIN cliente c ON c.id = v.cliente_id AND c.dni = '74512389'
    JOIN producto p ON p.nombre = 'Taladro Percutor 750W'
    WHERE v.fecha = '2026-06-01 10:30:00';

INSERT IGNORE INTO detalle_venta (venta_id, producto_id, cantidad, precio)
    SELECT v.id, p.id, 5, p.precio
    FROM venta v
    JOIN cliente c ON c.id = v.cliente_id AND c.dni = '61234567'
    JOIN producto p ON p.nombre = 'Martillo de Carpintero 16 oz'
    WHERE v.fecha = '2026-06-03 14:15:00';

INSERT IGNORE INTO detalle_venta (venta_id, producto_id, cantidad, precio)
    SELECT v.id, p.id, 10, p.precio
    FROM venta v
    JOIN cliente c ON c.id = v.cliente_id AND c.dni = '45678901'
    JOIN producto p ON p.nombre = 'Cemento Gris Portland 42.5 kg'
    WHERE v.fecha = '2026-06-05 09:00:00';

-- Verificación
SELECT 'usuario' AS tabla, COUNT(*) AS filas FROM usuario
UNION ALL SELECT 'cliente',      COUNT(*) FROM cliente
UNION ALL SELECT 'producto',     COUNT(*) FROM producto
UNION ALL SELECT 'venta',        COUNT(*) FROM venta
UNION ALL SELECT 'detalle_venta',COUNT(*) FROM detalle_venta;
