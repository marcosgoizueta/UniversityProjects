#Creo la base de datos
CREATE DATABASE banco;
#Selecciono la base de datos sobre la cuál voy a hacer modificaciones
USE banco;
#Creación tablas
CREATE TABLE ciudad(
	cod_postal SMALLINT UNSIGNED NOT NULL,
	nombre VARCHAR(25) NOT NULL,
	CONSTRAINT pk_ciudad PRIMARY KEY (cod_postal)
) ENGINE = InnoDB;
CREATE TABLE sucursal(
	nro_suc SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
	nombre VARCHAR(45) NOT NULL,
	direccion VARCHAR(45) NOT NULL,
	telefono VARCHAR(20) NOT NULL,
	horario VARCHAR(30) NOT NULL,
	cod_postal SMALLINT UNSIGNED NOT NULL,
	CONSTRAINT pk_sucursal PRIMARY KEY (nro_suc),
	CONSTRAINT FK_sucursal_ciudad FOREIGN KEY (cod_postal) REFERENCES ciudad (cod_postal) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;
CREATE TABLE empleado(
	legajo SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT,
	apellido VARCHAR(45) NOT NULL,
	nombre VARCHAR(45) NOT NULL,
	tipo_doc VARCHAR(20) NOT NULL,
	nro_doc INT UNSIGNED NOT NULL,
	direccion VARCHAR(45) NOT NULL,
	telefono VARCHAR(45) NOT NULL,
	cargo VARCHAR(45) NOT NULL,
	password VARCHAR(32) NOT NULL,
	nro_suc SMALLINT UNSIGNED NOT NULL,
	CONSTRAINT pk_empleado PRIMARY KEY (legajo),
	CONSTRAINT FK_empleado_sucursal FOREIGN KEY (nro_suc) REFERENCES sucursal (nro_suc) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;
CREATE TABLE cliente(
	nro_cliente INT UNSIGNED NOT NULL AUTO_INCREMENT,
	apellido VARCHAR(45) NOT NULL,
	nombre VARCHAR(45) NOT NULL,
	tipo_doc VARCHAR(20) NOT NULL,
	nro_doc INT UNSIGNED NOT NULL,
	direccion VARCHAR(45) NOT NULL,
	telefono VARCHAR(45) NOT NULL,
	fecha_nac DATE NOT NULL,
	CONSTRAINT pk_cliente PRIMARY KEY (nro_cliente)
) ENGINE = InnoDB;
CREATE TABLE plazo_fijo(
	nro_plazo INT UNSIGNED NOT NULL AUTO_INCREMENT,
	capital DECIMAL(16, 2) UNSIGNED NOT NULL,
	fecha_inicio DATE NOT NULL,
	fecha_fin DATE NOT NULL,
	tasa_interes DECIMAL(4, 2) UNSIGNED NOT NULL,
	interes DECIMAL(16, 2) UNSIGNED NOT NULL,
	nro_suc SMALLINT UNSIGNED NOT NULL,
	CONSTRAINT pk_plazo_fijo PRIMARY KEY (nro_plazo),
	CONSTRAINT FK_plazo_fijo_sucursal FOREIGN KEY (nro_suc) REFERENCES sucursal (nro_suc) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;
CREATE TABLE tasa_plazo_fijo(
	periodo SMALLINT UNSIGNED NOT NULL,
	monto_inf DECIMAL(16, 2) UNSIGNED NOT NULL,
	monto_sup DECIMAL(16, 2) UNSIGNED NOT NULL,
	tasa DECIMAL(4, 2) UNSIGNED NOT NULL,
	CONSTRAINT pk_tasa_plazo_fijo PRIMARY KEY (periodo, monto_inf, monto_sup)
) ENGINE = InnoDB;
CREATE TABLE plazo_cliente(
	nro_plazo INT UNSIGNED NOT NULL,
	nro_cliente INT UNSIGNED NOT NULL,
	CONSTRAINT pk_plazo_cliente PRIMARY KEY (nro_plazo, nro_cliente),
	CONSTRAINT FK_plazo_cliente_plazo_fijo FOREIGN KEY (nro_plazo) REFERENCES plazo_fijo (nro_plazo) ON DELETE RESTRICT ON UPDATE CASCADE,
	CONSTRAINT FK_plazo_cliente_cliente FOREIGN KEY (nro_cliente) REFERENCES cliente (nro_cliente) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;
CREATE TABLE prestamo(
	nro_prestamo INT UNSIGNED NOT NULL AUTO_INCREMENT,
	fecha DATE NOT NULL,
	cant_meses TINYINT UNSIGNED NOT NULL,
	monto DECIMAL(10, 2) UNSIGNED NOT NULL,
	tasa_interes DECIMAL(4, 2) UNSIGNED NOT NULL,
	interes DECIMAL(9, 2) UNSIGNED NOT NULL,
	valor_cuota DECIMAL(9, 2) UNSIGNED NOT NULL,
	legajo SMALLINT UNSIGNED NOT NULL,
	nro_cliente INT UNSIGNED NOT NULL,
	CONSTRAINT pk_prestamo PRIMARY KEY (nro_prestamo),
	CONSTRAINT FK_prestamo_empleado FOREIGN KEY (legajo) REFERENCES empleado (legajo) ON DELETE RESTRICT ON UPDATE CASCADE,
	CONSTRAINT FK_prestamo_cliente FOREIGN KEY (nro_cliente) REFERENCES cliente (nro_cliente) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;
CREATE TABLE pago(
	nro_prestamo INT UNSIGNED NOT NULL,
	nro_pago TINYINT UNSIGNED NOT NULL,
	fecha_venc DATE NOT NULL,
	fecha_pago DATE,
	CONSTRAINT pk_pago PRIMARY KEY (nro_prestamo, nro_pago),
	CONSTRAINT FK_pago_prestamo FOREIGN KEY (nro_prestamo) REFERENCES prestamo (nro_prestamo) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;
CREATE TABLE tasa_prestamo(
	periodo SMALLINT UNSIGNED NOT NULL,
	monto_inf DECIMAL(10, 2) UNSIGNED NOT NULL,
	monto_sup DECIMAL(10, 2) UNSIGNED NOT NULL,
	tasa DECIMAL(4, 2) UNSIGNED NOT NULL,
	CONSTRAINT pk_tasa_prestamo PRIMARY KEY (periodo, monto_inf, monto_sup)
) ENGINE = InnoDB;
CREATE TABLE caja_ahorro(
	nro_ca INT UNSIGNED NOT NULL AUTO_INCREMENT,
	cbu BIGINT UNSIGNED NOT NULL,
	saldo DECIMAL(16, 2) UNSIGNED NOT NULL,
	CONSTRAINT pk_caja_ahorro PRIMARY KEY (nro_ca)
) ENGINE = InnoDB;
CREATE TABLE cliente_ca(
	nro_cliente INT UNSIGNED NOT NULL,
	nro_ca INT UNSIGNED NOT NULL,
	CONSTRAINT pk_cliente_ca PRIMARY KEY (nro_cliente, nro_ca),
	CONSTRAINT FK_cliente_ca_cliente FOREIGN KEY (nro_cliente) REFERENCES cliente (nro_cliente) ON DELETE RESTRICT ON UPDATE CASCADE,
	CONSTRAINT FK_cliente_ca_caja_ahorro FOREIGN KEY (nro_ca) REFERENCES caja_ahorro (nro_ca) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;
CREATE TABLE tarjeta(
	nro_tarjeta BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	pin VARCHAR(32) NOT NULL,
	cvt VARCHAR(32) NOT NULL,
	fecha_venc DATE NOT NULL,
	nro_cliente INT UNSIGNED NOT NULL,
	nro_ca INT UNSIGNED NOT NULL,
	CONSTRAINT pk_tarjeta PRIMARY KEY (nro_tarjeta),
	CONSTRAINT FK_tarjeta_cliente_ca FOREIGN KEY (nro_cliente, nro_ca) REFERENCES cliente_ca (nro_cliente, nro_ca) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;
CREATE TABLE caja(
	cod_caja INT UNSIGNED NOT NULL AUTO_INCREMENT,
	CONSTRAINT pk_caja PRIMARY KEY (cod_caja)
) ENGINE = InnoDB;
CREATE TABLE ventanilla(
	cod_caja INT UNSIGNED NOT NULL,
	nro_suc SMALLINT UNSIGNED NOT NULL,
	CONSTRAINT pk_ventanilla PRIMARY KEY (cod_caja),
	CONSTRAINT FK_ventanilla_caja FOREIGN KEY (cod_caja) REFERENCES caja (cod_caja) ON DELETE RESTRICT ON UPDATE CASCADE,
	CONSTRAINT FK_ventanilla_sucursal FOREIGN KEY (nro_suc) REFERENCES sucursal (nro_suc) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;
CREATE TABLE atm(
	cod_caja INT UNSIGNED NOT NULL,
	cod_postal SMALLINT UNSIGNED NOT NULL,
	direccion VARCHAR(45) NOT NULL,
	CONSTRAINT pk_atm PRIMARY KEY (cod_caja),
	CONSTRAINT FK_atm_caja FOREIGN KEY (cod_caja) REFERENCES caja (cod_caja) ON DELETE RESTRICT ON UPDATE CASCADE,
	CONSTRAINT FK_atm_ciudad FOREIGN KEY (cod_postal) REFERENCES ciudad (cod_postal) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;
CREATE TABLE transaccion(
	nro_trans BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
	fecha DATE NOT NULL,
	hora TIME NOT NULL,
	monto DECIMAL(16, 2) UNSIGNED NOT NULL,
	CONSTRAINT pk_transaccion PRIMARY KEY (nro_trans)
) ENGINE = InnoDB;
CREATE TABLE debito(
	nro_trans BIGINT UNSIGNED NOT NULL,
	descripcion TEXT,
	nro_cliente INT UNSIGNED NOT NULL,
	nro_ca INT UNSIGNED NOT NULL,
	CONSTRAINT pk_debito PRIMARY KEY (nro_trans),
	CONSTRAINT FK_debito_transaccion FOREIGN KEY (nro_trans) REFERENCES transaccion (nro_trans) ON DELETE RESTRICT ON UPDATE CASCADE,
	CONSTRAINT FK_debito_cliente_ca FOREIGN KEY (nro_cliente, nro_ca) REFERENCES cliente_ca (nro_cliente, nro_ca) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;
CREATE TABLE transaccion_por_caja(
	nro_trans BIGINT UNSIGNED NOT NULL,
	cod_caja INT UNSIGNED NOT NULL,
	CONSTRAINT pk_transaccion_por_caja PRIMARY KEY (nro_trans),
	CONSTRAINT FK_transaccion_por_caja_transaccion FOREIGN KEY (nro_trans) REFERENCES transaccion (nro_trans) ON DELETE RESTRICT ON UPDATE CASCADE,
	CONSTRAINT FK_transaccion_por_caja_caja FOREIGN KEY (cod_caja) REFERENCES caja (cod_caja) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;
CREATE TABLE deposito(
	nro_trans BIGINT UNSIGNED NOT NULL,
	nro_ca INT UNSIGNED NOT NULL,
	CONSTRAINT pk_deposito PRIMARY KEY (nro_trans),
	CONSTRAINT FK_deposito_transaccion_por_caja FOREIGN KEY (nro_trans) REFERENCES transaccion_por_caja (nro_trans) ON DELETE RESTRICT ON UPDATE CASCADE,
	CONSTRAINT FK_deposito_caja_ahorro FOREIGN KEY (nro_ca) REFERENCES caja_ahorro (nro_ca) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;
CREATE TABLE extraccion(
	nro_trans BIGINT UNSIGNED NOT NULL,
	nro_cliente INT UNSIGNED NOT NULL,
	nro_ca INT UNSIGNED NOT NULL,
	CONSTRAINT pk_extraccion PRIMARY KEY (nro_trans),
	CONSTRAINT FK_extraccion_transaccion_por_caja FOREIGN KEY (nro_trans) REFERENCES transaccion_por_caja (nro_trans) ON DELETE RESTRICT ON UPDATE CASCADE,
	CONSTRAINT FK_extraccion_cliente_ca FOREIGN KEY (nro_cliente, nro_ca) REFERENCES cliente_ca (nro_cliente, nro_ca) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;
CREATE TABLE transferencia(
	nro_trans BIGINT UNSIGNED NOT NULL,
	nro_cliente INT UNSIGNED NOT NULL,
	origen INT UNSIGNED NOT NULL,
	destino INT UNSIGNED NOT NULL,
	CONSTRAINT pk_transferencia PRIMARY KEY (nro_trans),
	CONSTRAINT FK_transferencia_transaccion_por_caja FOREIGN KEY (nro_trans) REFERENCES transaccion_por_caja (nro_trans) ON DELETE RESTRICT ON UPDATE CASCADE,
	CONSTRAINT FK_transferencia_cliente_ca FOREIGN KEY (nro_cliente, origen) REFERENCES cliente_ca (nro_cliente, nro_ca) ON DELETE RESTRICT ON UPDATE CASCADE,
	CONSTRAINT FK_transferencia_caja_ahorro FOREIGN KEY (destino) REFERENCES caja_ahorro (nro_ca) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB;
#-------------------------------------------------------------------------
# Creación de vistas 
CREATE VIEW v_debito AS
SELECT d.nro_ca,
	ca.saldo,
	d.nro_trans,
	t.fecha,
	t.hora,
	d.tipo,
	t.monto,
	d.cod_caja,
	d.nro_cliente,
	cl.tipo_doc,
	cl.nro_doc,
	cl.nombre,
	cl.apellido,
	d.destino
FROM (
		SELECT nro_ca,
			nro_trans,
			nro_cliente,
			'debito' AS tipo,
			NULL AS cod_caja,
			NULL AS destino
		FROM debito
	) AS d
	JOIN (
		SELECT saldo,
			nro_ca
		FROM caja_ahorro
	) AS ca ON d.nro_ca = ca.nro_ca
	JOIN (
		SELECT nro_trans,
			fecha,
			hora,
			monto
		FROM transaccion
	) AS t ON d.nro_trans = t.nro_trans
	JOIN cliente_ca AS cl_ca ON d.nro_ca = cl_ca.nro_ca
	JOIN (
		SELECT nro_cliente,
			tipo_doc,
			nro_doc,
			nombre,
			apellido
		FROM cliente
	) AS cl ON d.nro_cliente = cl.nro_cliente;
CREATE VIEW v_deposito AS
SELECT d.nro_ca,
	ca.saldo,
	d.nro_trans,
	t.fecha,
	t.hora,
	d.tipo,
	t.monto,
	tpc.cod_caja,
	d.nro_cliente,
	d.tipo_doc,
	d.nro_doc,
	d.nombre,
	d.apellido,
	d.destino
FROM (
		SELECT nro_ca,
			nro_trans,
			'deposito' AS tipo,
			NULL AS nro_cliente,
			NULL AS tipo_doc,
			NULL AS nro_doc,
			NULL AS nombre,
			NULL AS apellido,
			NULL AS destino
		FROM deposito
	) AS d
	JOIN (
		SELECT saldo,
			nro_ca
		FROM caja_ahorro
	) AS ca ON d.nro_ca = ca.nro_ca
	JOIN (
		SELECT nro_trans,
			fecha,
			hora,
			monto
		FROM transaccion
	) AS t ON d.nro_trans = t.nro_trans
	JOIN transaccion_por_caja AS tpc ON t.nro_trans = tpc.nro_trans;
CREATE VIEW v_extraccion AS
SELECT e.nro_ca,
	ca.saldo,
	e.nro_trans,
	t.fecha,
	t.hora,
	e.tipo,
	t.monto,
	tpc.cod_caja,
	cl.nro_cliente,
	cl.tipo_doc,
	cl.nro_doc,
	cl.nombre,
	cl.apellido,
	e.destino
FROM (
		SELECT nro_ca,
			nro_trans,
			nro_cliente,
			'extraccion' AS tipo,
			NULL AS destino
		FROM extraccion
	) AS e
	JOIN (
		SELECT saldo,
			nro_ca
		FROM caja_ahorro
	) AS ca ON e.nro_ca = ca.nro_ca
	JOIN (
		SELECT nro_trans,
			fecha,
			hora,
			monto
		FROM transaccion
	) AS t ON e.nro_trans = t.nro_trans
	JOIN transaccion_por_caja AS tpc ON t.nro_trans = tpc.nro_trans
	JOIN (
		SELECT nro_cliente,
			tipo_doc,
			nro_doc,
			nombre,
			apellido
		FROM cliente
	) AS cl ON e.nro_cliente = cl.nro_cliente;
CREATE VIEW v_transferencia AS
SELECT tf.nro_ca,
	ca.saldo,
	tf.nro_trans,
	t.fecha,
	t.hora,
	tf.tipo,
	t.monto,
	tpc.cod_caja,
	cl.nro_cliente,
	cl.tipo_doc,
	cl.nro_doc,
	cl.nombre,
	cl.apellido,
	tf.destino
FROM (
		SELECT origen AS nro_ca,
			nro_trans,
			nro_cliente,
			'transferencia' AS tipo,
			destino
		FROM transferencia
	) AS tf
	JOIN (
		SELECT saldo,
			nro_ca
		FROM caja_ahorro
	) AS ca ON tf.nro_ca = ca.nro_ca
	JOIN (
		SELECT nro_trans,
			fecha,
			hora,
			monto
		FROM transaccion
	) AS t ON tf.nro_trans = t.nro_trans
	JOIN transaccion_por_caja AS tpc ON t.nro_trans = tpc.nro_trans
	JOIN (
		SELECT nro_cliente,
			tipo_doc,
			nro_doc,
			nombre,
			apellido
		FROM cliente
	) AS cl ON tf.nro_cliente = cl.nro_cliente;
CREATE VIEW trans_cajas_ahorro AS
SELECT *
FROM (
		SELECT *
		FROM v_debito
		UNION ALL
		SELECT *
		FROM v_deposito
		UNION ALL
		SELECT *
		FROM v_extraccion
		UNION ALL
		SELECT *
		FROM v_transferencia
	) AS tca;
#-------------------------------------------------------------------------
#stored procedures
DELIMITER !
CREATE PROCEDURE p_extraccion(
	IN monto DECIMAL(16, 2),
	IN codigoATM INT,
	IN nroTarjeta INT
)
BEGIN
DECLARE codigo_SQL CHAR(5) DEFAULT '00000';
DECLARE codigo_MYSQL INT DEFAULT 0;
DECLARE mensaje_error TEXT;
DECLARE cajaOrigen INT;
DECLARE saldoOrigen DECIMAL(16, 2);
DECLARE nroCliente INT;
DECLARE nroTrans INT;
DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN GET DIAGNOSTICS CONDITION 1 codigo_MYSQL = MYSQL_ERRNO,
	codigo_SQL = RETURNED_SQLSTATE,
	mensaje_error = MESSAGE_TEXT;
SELECT 'SQLEXCEPTION!, transaccion abortada' AS resultado,
	codigo_MYSQL,
	codigo_SQL,
	mensaje_error;
ROLLBACK;
END;
START TRANSACTION;
IF EXISTS (
	SELECT nro_tarjeta,
		nro_cliente,
		nro_ca
	FROM tarjeta
	WHERE nro_tarjeta = nroTarjeta
) THEN
SELECT nro_cliente,
	nro_ca INTO nroCliente,
	cajaOrigen
FROM tarjeta
WHERE nro_tarjeta = nroTarjeta;
SELECT saldo INTO saldoOrigen
FROM caja_ahorro
WHERE nro_ca = cajaOrigen FOR
UPDATE;
IF saldoOrigen >= monto THEN
UPDATE caja_ahorro
SET saldo = saldo - monto
WHERE nro_ca = cajaOrigen;
SELECT saldo INTO saldoOrigen
FROM caja_ahorro
WHERE nro_ca = cajaOrigen;
INSERT INTO transaccion (fecha, hora, monto)
VALUES (CURDATE(), CURTIME(), monto);
SET nroTrans = LAST_INSERT_ID();
INSERT INTO transaccion_por_caja
VALUES (nroTrans, codigoATM);
INSERT INTO extraccion
VALUES (nroTrans, nroCliente, cajaOrigen);
SELECT 'La extraccion se realizo con exito' AS resultado,
	saldoOrigen AS saldo;
ELSE
SELECT 'Saldo insuficiente para realizar la extraccion' AS resultado,
	NULL AS saldo;
END IF;
ELSE
SELECT 'Error: cuenta inexistente' AS resultado,
	NULL AS saldo;
END IF;
COMMIT;
END;
! CREATE PROCEDURE p_transferencia(
	IN monto DECIMAL(16, 2),
	IN codigoATM INT,
	IN nroTarjeta BIGINT,
	IN cajaDestino INT
) BEGIN
DECLARE codigo_SQL CHAR(5) DEFAULT '00000';
DECLARE codigo_MYSQL INT DEFAULT 0;
DECLARE mensaje_error TEXT;
DECLARE cajaOrigen INT;
DECLARE saldoOrigen DECIMAL(16, 2);
DECLARE nroCliente INT;
DECLARE nroTrans BIGINT;
DECLARE EXIT HANDLER FOR SQLEXCEPTION BEGIN GET DIAGNOSTICS CONDITION 1 codigo_MYSQL = MYSQL_ERRNO,
	codigo_SQL = RETURNED_SQLSTATE,
	mensaje_error = MESSAGE_TEXT;
SELECT 'SQLEXCEPTION!, transaccion abortada' AS resultado,
	codigo_MYSQL,
	codigo_SQL,
	mensaje_error;
ROLLBACK;
END;
START TRANSACTION;
IF EXISTS (
	SELECT nro_tarjeta,
		nro_cliente,
		nro_ca
	FROM tarjeta
	WHERE nro_tarjeta = nroTarjeta
)
AND EXISTS (
	SELECT *
	FROM caja_ahorro
	WHERE nro_ca = cajaDestino
) THEN
SELECT nro_cliente,
	nro_ca INTO nroCliente,
	cajaOrigen
FROM tarjeta
WHERE nro_tarjeta = nroTarjeta;
SELECT saldo INTO saldoOrigen
FROM caja_ahorro
WHERE nro_ca = cajaOrigen FOR
UPDATE;
IF saldoOrigen >= monto THEN
UPDATE caja_ahorro
SET saldo = saldo - monto
WHERE nro_ca = cajaOrigen;
UPDATE caja_ahorro
SET saldo = saldo + monto
WHERE nro_ca = cajaDestino;
SELECT saldo INTO saldoOrigen
FROM caja_ahorro
WHERE nro_ca = cajaOrigen;
INSERT INTO transaccion (fecha, hora, monto)
VALUES (CURDATE(), CURTIME(), monto);
SET nroTrans = LAST_INSERT_ID();
INSERT INTO transaccion_por_caja
VALUES (nroTrans, codigoATM);
INSERT INTO transferencia
VALUES (nroTrans, nroCliente, cajaOrigen, cajaDestino);
INSERT INTO transaccion (fecha, hora, monto)
VALUES (CURDATE(), CURTIME(), monto);
SET nroTrans = LAST_INSERT_ID();
INSERT INTO transaccion_por_caja
VALUES (nroTrans, codigoATM);
INSERT INTO deposito
VALUES (nroTrans, cajaDestino);
SELECT 'Transferencia Exitosa' AS resultado,
	saldoOrigen AS saldo;
ELSE
SELECT 'Saldo insuficiente para realizar la transferencia' AS resultado,
	NULL AS saldo;
END IF;
ELSE
SELECT 'Error: cuenta inexistente' AS resultado,
	NULL AS saldo;
END IF;
COMMIT;
END;
CREATE TRIGGER crear_pagos_prestamo
AFTER
INSERT ON prestamo FOR EACH ROW BEGIN
DECLARE i INT DEFAULT 1;
DECLARE fechaVenc DATE;
WHILE i <= NEW.cant_meses DO
SET fechaVenc = DATE_ADD(CURDATE(), INTERVAL i MONTH);
INSERT INTO pago
VALUES (NEW.nro_prestamo, i, fechaVenc, NULL);
SET i = i + 1;
END WHILE;
END; !
DELIMITER ;

#Creación de usuarios y otorgamiento de privilegios
DROP USER IF EXISTS '' @'localhost';
CREATE USER 'admin' @'localhost' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON banco.* TO 'admin' @'localhost' WITH
GRANT OPTION;
CREATE USER 'empleado' @'%' IDENTIFIED BY 'empleado';
GRANT SELECT ON banco.empleado TO 'empleado' @'%';
GRANT SELECT ON banco.sucursal TO 'empleado' @'%';
GRANT SELECT ON banco.tasa_plazo_fijo TO 'empleado' @'%';
GRANT SELECT ON banco.tasa_prestamo TO 'empleado' @'%';
GRANT SELECT,
	INSERT ON banco.prestamo TO 'empleado' @'%';
GRANT SELECT,
	INSERT ON banco.plazo_fijo TO 'empleado' @'%';
GRANT SELECT,
	INSERT ON banco.plazo_cliente TO 'empleado' @'%';
GRANT SELECT,
	INSERT ON banco.caja_ahorro TO 'empleado' @'%';
GRANT SELECT,
	INSERT ON banco.tarjeta TO 'empleado' @'%';
GRANT SELECT,
	INSERT,
	UPDATE ON banco.cliente_ca TO 'empleado' @'%';
GRANT SELECT,
	INSERT,
	UPDATE ON banco.cliente TO 'empleado' @'%';
GRANT SELECT,
	INSERT,
	UPDATE ON banco.pago TO 'empleado' @'%';
CREATE USER 'atm' @'%' IDENTIFIED BY 'atm';
GRANT SELECT,
	UPDATE ON banco.tarjeta TO 'atm' @'%';
GRANT SELECT ON banco.trans_cajas_ahorro TO 'atm' @'%';
GRANT EXECUTE ON PROCEDURE banco.p_extraccion TO 'atm' @'%';
GRANT EXECUTE ON PROCEDURE banco.p_transferencia TO 'atm' @'%';
#------------------------------------------------------------------