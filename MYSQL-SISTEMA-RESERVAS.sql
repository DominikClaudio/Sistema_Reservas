/*
create database Sistema_Reserva;

drop database if exists sistema_reserva;
*/

CREATE TABLE Roles (
    idrol INT PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL
);

CREATE TABLE Usuarios (
    idusuario INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(20) NOT NULL,
    apellido VARCHAR(20) NOT NULL,
    dni INT UNIQUE,
    correo VARCHAR(50) NOT NULL,
    password VARCHAR(100) NOT NULL,
    rolid INT NOT NULL DEFAULT 1,
    FOREIGN KEY (rolid) REFERENCES Roles(idrol)
);

CREATE TABLE EstadoHabitacion (
    idestadohab INT PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL
);

CREATE TABLE EstadoReserva (
    idestadores INT PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL
);

CREATE TABLE Pisos (
    idpiso INT PRIMARY KEY AUTO_INCREMENT,
    numero INT NOT NULL,
    nombre VARCHAR(15) NOT NULL
);

CREATE TABLE TipoHabitacion (
    idtipohab INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(35) NOT NULL,
    imagenurl VARCHAR(255),
    precioxdia DECIMAL(10,2),
    descripcionhab VARCHAR(255),
    capacidad INT DEFAULT 2
);

CREATE TABLE Habitaciones (
    idhabitacion INT PRIMARY KEY AUTO_INCREMENT,
    numhabitacion INT NOT NULL,
    tipohabid INT NOT NULL,
    pisoid INT NOT NULL,
    estadohabid INT NOT NULL DEFAULT 1,
    FOREIGN KEY (tipohabid) REFERENCES TipoHabitacion(idtipohab),
    FOREIGN KEY (pisoid) REFERENCES Pisos(idpiso),
    FOREIGN KEY (estadohabid) REFERENCES EstadoHabitacion(idestadohab)
);

CREATE TABLE Reservas (
    idreserva INT PRIMARY KEY AUTO_INCREMENT,
    codigoreserva VARCHAR(50) NOT NULL,
    usuarioid INT,
    fechaentrada DATE,
    fechasalida DATE,
    estadoid INT,
    horaentrada TIME,
    horasalida TIME,
    FOREIGN KEY (usuarioid) REFERENCES Usuarios(idusuario),
    FOREIGN KEY (estadoid) REFERENCES EstadoReserva(idestadores)
);

CREATE TABLE DetalleReserva (
    iddetallereserva INT PRIMARY KEY AUTO_INCREMENT,
    reservaid INT,
    habitacionid INT,
    cantidaddias INT,
    FOREIGN KEY (reservaid) REFERENCES Reservas(idreserva),
    FOREIGN KEY (habitacionid) REFERENCES Habitaciones(idhabitacion)
);

INSERT INTO Roles (idrol, nombre) VALUES 
(1, 'CLIENTE'), 
(2, 'ADMIN');

INSERT INTO EstadoHabitacion (idestadohab, nombre) VALUES
(1, 'Disponible'),
(2, 'Mantenimiento'),
(3, 'Limpieza'),
(4, 'Fuera de servicio'),
(5, 'Ocupado');

INSERT INTO EstadoReserva (idestadores, nombre) VALUES 
(1, 'Disponible'),
(2, 'Pendiente'),  
(3, 'Confirmado'), 
(4, 'Cancelado'),
(5, 'Finalizado');


INSERT INTO Pisos (numero, nombre) VALUES
(1, 'Primero'),
(2, 'Segundo'),
(3, 'Tercero'),
(4, 'Cuarto');

INSERT INTO TipoHabitacion (nombre, imagenurl, precioxdia, descripcionhab, capacidad)
VALUES 
('Habitación Estándar', '/imagenes/estandar.png', 100.00, 'Habitación sencilla con cama matrimonial, baño privado, Smart TV y armario básico.', 2),
('Habitación Doble', '/imagenes/doble.png', 150.00, 'Habitación doble con dos camas individuales, baño privado, aire acondicionado, armario y Smart TV.', 4),
('Habitación Ejecutiva', '/imagenes/ejecutiva.png', 200.00, 'Habitación ejecutiva con cama king size, escritorio de trabajo, baño privado con ducha de hidromasaje y Smart TV.', 2),
('Habitación Panorámica', '/imagenes/panoramica.png', 250.00, 'Habitación panorámica con vista a la ciudad, cama king size, baño privado, minibar y Smart TV.', 2),
('Habitación Suite Panorámica', '/imagenes/suite_panoramica.png', 350.00, 'Suite panorámica con dormitorio amplio, baño con jacuzzi, pequeña cocina, área de star y vista al parque. Incluye Smart TV.', 2),
('Habitación Suite', '/imagenes/suite.png', 300.00, 'Suite con dormitorio amplio, baño con jacuzzi, pequeña cocina, área de star y Smart TV. No tiene vista panorámica.', 2);

SET SQL_SAFE_UPDATES = 0;

UPDATE TipoHabitacion
SET ImagenURL = REPLACE(ImagenURL, '/imagenes/', '/Imagenes/');

SET SQL_SAFE_UPDATES = 1;


-- Piso 1
INSERT INTO Habitaciones (numhabitacion, tipohabid, pisoid, estadohabid) VALUES
(101, 1, 1, 1), 
(102, 1, 1, 1),
(103, 2, 1, 1),  
(104, 2, 1, 1),
(105, 1, 1, 1),
(106, 1, 1, 1);

-- Piso 2
INSERT INTO Habitaciones (NumHabitacion, TipoHabId, PisoId, EstadoHabId) VALUES
(201, 3, 2, 1), 
(202, 3, 2, 1),
(203, 2, 2, 1),  
(204, 2, 2, 1),
(205, 1, 2, 1),  
(206, 1, 2, 1);

-- Piso 3
INSERT INTO Habitaciones (NumHabitacion, TipoHabId, PisoId, EstadoHabId) VALUES
(301, 4, 3, 1),
(302, 4, 3, 1),
(303, 3, 3, 1),  
(304, 3, 3, 1),
(305, 1, 3, 1),
(306, 1, 3, 1);

-- Piso 4
INSERT INTO Habitaciones (NumHabitacion, TipoHabId, PisoId, EstadoHabId) VALUES
(401, 5, 4, 1),  
(402, 5, 4, 1),
(403, 6, 4, 1),  
(404, 6, 4, 1);

/*SOLO PARA ELIMINAR*/
/*DROP PROCEDURE SP_ListarHabitacionesDisponibles;*/
DELIMITER //

/*PROCEDURES PARA ADMIN*/
CREATE PROCEDURE SP_ListarHabTipoEstado(
    IN TipoHabId INT,
    IN EstadoHabId INT,
    IN EstadoReservaId INT,
    IN FechaInicio DATE,
    IN FechaFin DATE
)
BEGIN
    SELECT 
        H.idhabitacion,
        H.numhabitacion,
        TH.precioxdia,
        TH.nombre AS TipoHabitacion,
        P.nombre AS Piso,
        H.estadohabid,
        EH.nombre AS EstadoHabitacion,
        IFNULL(R.estadoid, 1) AS EstadoReservaId,
        IFNULL(ER.nombre, 'Disponible') AS EstadoReserva,
        IFNULL(CONCAT(U.nombre, ' ', U.apellido), '-') AS Cliente,
		R.fechaentrada,
		R.fechasalida
    FROM Habitaciones H
    JOIN TipoHabitacion TH ON H.tipohabid = TH.idtipohab
    JOIN Pisos P ON H.pisoid = P.idpiso
    JOIN EstadoHabitacion EH ON H.estadohabid = EH.idestadohab
    LEFT JOIN (
        SELECT DR.habitacionid, R.*
        FROM DetalleReserva DR
        JOIN Reservas R ON DR.reservaid = R.idreserva
        WHERE (FechaInicio IS NULL OR FechaFin IS NULL 
               OR (R.fechaentrada <= FechaFin AND R.fechasalida >= FechaInicio))
        AND R.fechaentrada = (
            SELECT MAX(R2.fechaentrada)
            FROM DetalleReserva DR2
            JOIN Reservas R2 ON DR2.reservaid = R2.idreserva
            WHERE DR2.habitacionid = DR.habitacionid
        )
    ) R ON H.idhabitacion = R.habitacionid
    LEFT JOIN EstadoReserva ER ON R.estadoid = ER.idestadores
    LEFT JOIN Usuarios U ON R.usuarioid = U.idusuario
    WHERE
        (TipoHabId IS NULL OR H.tipohabid = TipoHabId)
        AND (EstadoHabId IS NULL OR H.estadohabid = EstadoHabId)
        AND (EstadoReservaId IS NULL OR R.estadoid = EstadoReservaId);
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE SP_ListarUsuariosPorRol(
IN rolId INT
)
BEGIN
	SELECT 
		U.idusuario, 
		U.nombre, 
		U.apellido, 
		U.dni, 
		U.correo,
		U.rolid,
		R.nombre AS RolNombre
	FROM Usuarios U
		JOIN Roles R ON U.rolid = R.idrol
	WHERE (rolId IS NULL OR U.rolid = rolId);
END //

DELIMITER ;


DELIMITER //

CREATE PROCEDURE SP_Perfil(
IN UsuarioId INT
)
BEGIN
	SELECT
		nombre,
		apellido,
		correo,
		password
	FROM Usuarios
	WHERE idusuario = UsuarioId;
END //

DELIMITER ;


DELIMITER //

CREATE PROCEDURE SP_ActualizarPerfil(
    IN UsuarioId INT,
    IN Nombre VARCHAR(20),
    IN Apellido VARCHAR(20),
    IN DNI INT,
    IN Correo VARCHAR(50),
    IN Contrasena VARCHAR(255)
)
BEGIN
    UPDATE Usuarios
    SET nombre = Nombre,
        apellido = Apellido,
        dni = DNI,
        correo = Correo,
        password = Contrasena
    WHERE idusuario = UsuarioId;
END //

DELIMITER ;

/*PROCEDURES PARA CLIENTE*/

DELIMITER //

CREATE PROCEDURE SP_ListarHabitacionesDisponibles (
    IN FechaEntrada DATE,
    IN FechaSalida DATE,
    IN TipoHabId INT,
    IN PisoId INT,
    IN CantidadPersonas INT
)
BEGIN
    SELECT 
        H.idhabitacion,
        H.numhabitacion,
        TH.imagenurl
    FROM Habitaciones H
    JOIN TipoHabitacion TH ON H.tipohabid = TH.idtipohab
    JOIN Pisos P ON H.pisoid = P.idpiso
    JOIN EstadoHabitacion EH ON H.estadohabid = EH.idestadohab
    WHERE EH.nombre = 'Disponible'
      AND TH.capacidad >= CantidadPersonas
      AND H.idhabitacion NOT IN (
            SELECT DR.habitacionid
            FROM DetalleReserva DR
            JOIN Reservas R ON DR.reservaid = R.idreserva
            JOIN EstadoReserva ER ON R.estadoid = ER.idestadores
            WHERE ER.nombre IN ('Pendiente', 'Confirmado')
              AND (
                    FechaEntrada BETWEEN R.fechaentrada AND R.fechasalida OR
                    FechaSalida BETWEEN R.fechaentrada AND R.fechasalida OR
                    R.fechaentrada BETWEEN FechaEntrada AND FechaSalida OR
                    R.fechasalida BETWEEN FechaEntrada AND FechaSalida
                  )
        )
      AND (TipoHabId IS NULL OR H.tipohabid = TipoHabId)
      AND (PisoId IS NULL OR H.pisoid = PisoId)
    ORDER BY H.numhabitacion;
END //

DELIMITER ;

/*

CALL SP_ListarHabitacionesDisponibles("2025-06-21", "2025-06-22", 1, 1, 2);

*/