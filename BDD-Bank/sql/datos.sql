USE banco;
INSERT INTO ciudad
VALUES (1001, "Tres Arroyos");
INSERT INTO ciudad
VALUES (2002, "Bahía Blanca");
INSERT INTO ciudad
VALUES (3003, "Punta Alta");
INSERT INTO sucursal
VALUES (
        001,
        "Suc-1",
        "Mitre 1075",
        "2983383858",
        "Mañana",
        1001
    );
INSERT INTO sucursal
VALUES (
        002,
        "Suc-2",
        "Alem 12",
        "2983640994",
        "Mediodía",
        2002
    );
INSERT INTO sucursal
VALUES (
        003,
        "Suc-3",
        "12 de Octubre 333",
        "2910439687",
        "Tarde",
        3003
    );
INSERT INTO empleado
VALUES (
        0001,
        "Goizueta",
        "Marcos",
        "TI",
        "45740685",
        "Brown 959",
        "212136358",
        "Secretario",
        md5('goico'),
        001
    );
INSERT INTO empleado
VALUES (
        0002,
        "Pereira",
        "Pedro",
        "DNI",
        "44762784",
        "Soler 1231",
        "295176348",
        "Administrador",
        md5('perro'),
        002
    );
INSERT INTO empleado
VALUES (
        0003,
        "Rodriguez",
        "Juan",
        "TI",
        "42140639",
        "Castelli 88",
        "224296319",
        "Seguridad",
        md5('gato'),
        003
    );
INSERT INTO cliente
VALUES (
        22001,
        "Rodriguez",
        "Raul",
        "CI",
        43858549,
        "San Juan 233",
        "2144963315",
        "1942/05/01"
    );
INSERT INTO cliente
VALUES (
        22002,
        "Martinez",
        "Jorge",
        "DNI",
        42848640,
        "Peru 1007",
        "2449984326",
        "1956/01/14"
    );
INSERT INTO cliente
VALUES (
        22003,
        "Messi",
        "Manuel",
        "CI",
        42654141,
        "Alvarado 556",
        "2474961347",
        "1838/07/22"
    );
INSERT INTO cliente
VALUES (
        22004,
        "Moroso1_apellido",
        "Moroso1_nombre",
        "DNI",
        41414141,
        "Colon 34",
        "2914211742",
        "2000/02/12"
    );
INSERT INTO cliente
VALUES (
        22005,
        "Moroso2_apellido",
        "Moroso2_nombre",
        "CI",
        43434343,
        "Belgrano 1002",
        "2931819714",
        "2003/09/19"
    );
INSERT INTO plazo_fijo
VALUES (
        00025101,
        00000000240500.00,
        "2012/06/21",
        "2013/06/21",
        08.12,
        00000000001500.30,
        001
    );
INSERT INTO plazo_fijo
VALUES (
        00025102,
        00000000120700.00,
        "2011/01/01",
        "2012/01/01",
        05.05,
        00000000004700.15,
        002
    );
INSERT INTO plazo_fijo
VALUES (
        00025103,
        00000000813000.00,
        "2014/11/08",
        "2015/11/08",
        11.01,
        00000000080150.03,
        003
    );
INSERT INTO tasa_plazo_fijo
VALUES (101, 00000000015500.00, 00000000062500.00, 12.38);
INSERT INTO tasa_plazo_fijo
VALUES (205, 00000000027000.00, 00000000081100.02, 09.22);
INSERT INTO tasa_plazo_fijo
VALUES (333, 00000000030000.00, 00000000089200.10, 02.87);
INSERT INTO plazo_cliente
VALUES (00025101, 22001);
INSERT INTO plazo_cliente
VALUES (00025102, 22002);
INSERT INTO plazo_cliente
VALUES (00025103, 22003);
INSERT INTO prestamo
VALUES (
        00001057,
        "2010/02/11",
        08,
        00050500.00,
        04.35,
        0004870.34,
        0000560.00,
        0001,
        22001
    );
INSERT INTO prestamo
VALUES (
        00001058,
        "2012/11/07",
        11,
        00070100.00,
        06.12,
        0006110.00,
        0000620.00,
        0002,
        22002
    );
INSERT INTO prestamo
VALUES (
        00001059,
        "2012/09/02",
        09,
        00120000.00,
        07.01,
        0011290.15,
        0001005.00,
        0003,
        22003
    );
INSERT INTO prestamo
VALUES (
        00001060,
        "2020/05/13",
        05,
        00110000.00,
        06.66,
        0010110.12,
        0000903.00,
        0003,
        22004
    );
INSERT INTO prestamo
VALUES (
        00001061,
        "2020/01/01",
        03,
        00220000.00,
        03.33,
        0020220.21,
        0001301.00,
        0001,
        22005
    );
INSERT INTO pago
VALUES (00001057, 01, "2010/12/27", "2010/11/05");
INSERT INTO pago
VALUES (00001057, 02, "2010/12/27", "2010/11/12");
INSERT INTO pago
VALUES (00001058, 01, "2012/12/28", "2012/12/02");
INSERT INTO pago
VALUES (00001058, 02, "2024/01/21", NULL);
INSERT INTO pago
VALUES (00001058, 03, "2024/11/25", NULL);
INSERT INTO pago
VALUES (00001058, 04, "2024/12/24", NULL);
INSERT INTO pago
VALUES (00001060, 01, "2021/09/11", NULL);
INSERT INTO pago
VALUES (00001060, 02, "2021/10/12", NULL);
INSERT INTO pago
VALUES (00001061, 01, "2020/02/27", NULL);
INSERT INTO pago
VALUES (00001061, 02, "2020/03/21", NULL);
INSERT INTO pago
VALUES (00001061, 03, "2020/04/19", NULL);
INSERT INTO tasa_prestamo
VALUES (003, 00105000.00, 00805000.00, 11.04);
INSERT INTO tasa_prestamo
VALUES (006, 00090000.00, 00350000.00, 09.01);
INSERT INTO tasa_prestamo
VALUES (009, 00230000.00, 00510000.00, 7.95);
INSERT INTO caja_ahorro
VALUES (00123456, 749571409452856917, 00000003251600.00);
INSERT INTO caja_ahorro
VALUES (00123457, 942502101493856913, 00001062251200.05);
INSERT INTO caja_ahorro
VALUES (00123458, 421551409152874122, 00004452213400.12);
INSERT INTO cliente_ca
VALUES (22001, 00123456);
INSERT INTO cliente_ca
VALUES (22002, 00123457);
INSERT INTO cliente_ca
VALUES (22003, 00123458);
INSERT INTO tarjeta
VALUES (
        0000000010022016,
        md5('151515'),
        md5('miclave123'),
        "2030/01/21",
        22001,
        00123456
    );
INSERT INTO caja
VALUES (12345);
INSERT INTO ventanilla
VALUES (12345, 001);
INSERT INTO atm
VALUES (12345, 1001, "Alem 12");
INSERT INTO transaccion
VALUES (
        1000002222,
        "2012/05/25",
        "15:45:32",
        00000000013400.00
    );
INSERT INTO transaccion
VALUES (
        1000002223,
        "2013/07/14",
        "17:30:00",
        00000000016500.00
    );
INSERT INTO transaccion
VALUES (
        1000002224,
        "2011/08/02",
        "00:15:06",
        00000000020000.00
    );
INSERT INTO transaccion
VALUES (
        1000002225,
        "2012/01/28",
        "04:27:24",
        00000000008100.00
    );
INSERT INTO transaccion
VALUES (
        1000002226,
        "2020/02/29",
        "04:27:24",
        00000000017100.00
    );
INSERT INTO transaccion
VALUES (
        1000002227,
        "2015/03/03",
        "01:21:21",
        00000000005705.00
    );
INSERT INTO transaccion
VALUES (
        1000002228,
        "2021/07/16",
        "12:22:23",
        00000000021900.00
    );
INSERT INTO transaccion
VALUES (
        1000002229,
        "2022/10/02",
        "09:00:11",
        00000000003020.00
    );
INSERT INTO debito
VALUES (1000002222, "Pago debito.", 22001, 00123456);
INSERT INTO transaccion_por_caja
VALUES (1000002223, 12345);
INSERT INTO transaccion_por_caja
VALUES (1000002224, 12345);
INSERT INTO transaccion_por_caja
VALUES (1000002225, 12345);
INSERT INTO transaccion_por_caja
VALUES (1000002226, 12345);
INSERT INTO transaccion_por_caja
VALUES (1000002227, 12345);
INSERT INTO deposito
VALUES (1000002223, 00123457);
INSERT INTO deposito
VALUES (1000002224, 00123458);
INSERT INTO extraccion
VALUES (1000002224, 22002, 00123457);
INSERT INTO extraccion
VALUES (1000002226, 22001, 00123456);
INSERT INTO extraccion
VALUES (1000002227, 22002, 00123457);
INSERT INTO transferencia
VALUES (1000002225, 22003, 00123458, 00123457);
INSERT INTO transferencia
VALUES (1000002224, 22002, 00123457, 00123458);