/***********************************************************************************************************************/
/*****************************************    STORE PROCEDURES      **********************************************/
/***********************************************************************************************************************/

DROP PROCEDURE SP_RegistrarReservaConDetalles;
DELIMITER //

CREATE PROCEDURE SP_ListarHabTipoEstado(
    IN TipoHabId INT,
    IN EstadoHabId INT,
    IN EstadoReservaId INT,
    IN FechaInicio DATE,
    IN FechaFin DATE
)
BEGIN
    
    CREATE TEMPORARY TABLE UltimaReservaPorHabitacion AS
    SELECT *
    FROM (
        SELECT
            DR.habitacionid,
            R.idreserva,
            R.estadoid,
            R.usuarioid,
            R.fechaentrada,
            R.fechasalida,
            ROW_NUMBER() OVER (PARTITION BY DR.habitacionid ORDER BY R.fechaentrada DESC, R.idreserva DESC) AS rn
        FROM DetalleReserva DR
        JOIN Reservas R ON DR.reservaid = R.idreserva
        WHERE (FechaInicio IS NULL OR FechaFin IS NULL 
               OR (R.fechaentrada <= FechaFin AND R.fechasalida >= FechaInicio))
    ) AS sub
    WHERE rn = 1;

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
    LEFT JOIN UltimaReservaPorHabitacion R ON H.idhabitacion = R.habitacionid
    LEFT JOIN EstadoReserva ER ON R.estadoid = ER.idestadores
    LEFT JOIN Usuarios U ON R.usuarioid = U.idusuario
    WHERE
        (TipoHabId IS NULL OR H.tipohabid = TipoHabId)
        AND (EstadoHabId IS NULL OR H.estadohabid = EstadoHabId)
        AND (EstadoReservaId IS NULL OR R.estadoid = EstadoReservaId);

    DROP TEMPORARY TABLE IF EXISTS UltimaReservaPorHabitacion;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE SP_BuscarReservaPorCodigo(
    IN p_codigoReserva VARCHAR(50)
)
BEGIN
    SELECT 
        R.idreserva AS idReserva,
        R.codigoreserva AS codigoReserva,
        CONCAT(U.nombre, ' ', U.apellido) AS cliente,
        H.numhabitacion AS numHabitacion,
        T.nombre AS tipoHabitacion,
        P.nombre AS piso,
        R.fechaentrada AS fechaEntrada,
        R.fechasalida AS fechaSalida,
        T.precioxdia AS precioxDia,
        DR.cantidaddias AS cantidadDias,
        (T.precioxdia * DR.cantidaddias) AS total,
        ER.nombre AS estadoReserva
    FROM Reservas R
    INNER JOIN Usuarios U ON R.usuarioid = U.idusuario
    INNER JOIN DetalleReserva DR ON R.idreserva = DR.reservaid
    INNER JOIN Habitaciones H ON DR.habitacionid = H.idhabitacion
    INNER JOIN TipoHabitacion T ON H.tipohabid = T.idtipohab
    INNER JOIN Pisos P ON H.pisoid = P.idpiso
    INNER JOIN EstadoReserva ER ON R.estadoid = ER.idestadores
    WHERE R.codigoreserva = p_codigoReserva;
END //

DELIMITER ;
drop procedure SP_FinalizarReserva;
DELIMITER //

CREATE PROCEDURE SP_ConfirmarReserva (
    IN p_reservaId INT
)
BEGIN
    UPDATE Reservas
    SET estadoid = 3,
        horaentrada = CURRENT_TIME()
    WHERE idreserva = p_reservaId;

    UPDATE Habitaciones
    SET estadohabid = 5
    WHERE idhabitacion IN (
        SELECT habitacionid
        FROM DetalleReserva
        WHERE reservaid = p_reservaId
    );
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE SP_FinalizarReserva (
    IN p_reservaId INT
)
BEGIN
    UPDATE Reservas
    SET estadoid = 5,
        horasalida = CURRENT_TIME()
    WHERE idreserva = p_reservaId;

    UPDATE Habitaciones
    SET estadohabid = 3
    WHERE idhabitacion IN (
        SELECT habitacionid
        FROM DetalleReserva
        WHERE reservaid = p_reservaId
    );
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

CREATE PROCEDURE SP_ReporteReservasConfirmadas(
    IN p_fechaInicio DATE,
    IN p_fechaFin DATE
)
BEGIN

    SELECT 
        R.codigoreserva AS CodigoReserva,
        H.numhabitacion AS NumHabitacion,
        P.nombre AS Piso,
        U.nombre AS NombreCliente,
        U.apellido AS ApellidoCliente,
        U.dni AS DNI,
        R.fechaentrada AS FechaEntrada,
        R.horaentrada AS HoraEntrada,
        R.fechasalida AS FechaSalida,
        R.horasalida AS HoraSalida,
        TH.nombre AS TipoHabitacion,
        TH.precioxdia AS PrecioxDia,
        DATEDIFF(R.fechasalida, R.fechaentrada) * TH.precioxdia AS Total,
        ER.nombre AS Estado
    FROM Reservas R
        INNER JOIN DetalleReserva DR ON R.idreserva = DR.reservaid
        INNER JOIN Habitaciones H ON DR.habitacionid = H.idhabitacion
        INNER JOIN TipoHabitacion TH ON H.tipohabid = TH.idtipohab
        INNER JOIN Pisos P ON H.pisoid = P.idpiso
        INNER JOIN EstadoReserva ER ON R.estadoid = ER.idestadores
        INNER JOIN Usuarios U ON R.usuarioid = U.idusuario
    WHERE R.fechaentrada >= p_fechaInicio
      AND R.fechaentrada <= p_fechaFin
      AND ER.nombre IN ('Confirmado', 'Finalizado')
    ORDER BY R.fechaentrada DESC, R.idreserva DESC;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE SP_ReporteClientesFrecuentes(
    IN p_fechaInicio DATE,
    IN p_fechaFin DATE
)
BEGIN
    SELECT 
        U.nombre AS Nombre,
        U.apellido AS Apellido,
        U.dni AS DNI,
        COUNT(*) AS TotalReservas
    FROM Reservas R
    INNER JOIN Usuarios U ON R.usuarioid = U.idusuario
    WHERE R.fechaentrada BETWEEN p_fechaInicio AND p_fechaFin
    GROUP BY U.idusuario
    ORDER BY TotalReservas DESC;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE SP_ReporteHabitacionesFrecuentes(
    IN p_fechaInicio DATE,
    IN p_fechaFin DATE
)
BEGIN
    SELECT 
        H.numhabitacion AS NumHabitacion,
        P.nombre AS Piso,
        TH.nombre AS TipoHabitacion,
        COUNT(*) AS VecesReservada
    FROM Reservas R
    INNER JOIN DetalleReserva DR ON R.idreserva = DR.reservaid
    INNER JOIN Habitaciones H ON DR.habitacionid = H.idhabitacion
    INNER JOIN Pisos P ON H.pisoid = P.idpiso
    INNER JOIN TipoHabitacion TH ON H.tipohabid = TH.idtipohab
    WHERE R.fechaentrada BETWEEN p_fechaInicio AND p_fechaFin
    GROUP BY H.idhabitacion
    ORDER BY VecesReservada DESC;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE SP_ReporteOcupabilidadPisos(
    IN p_fechaInicio DATE,
    IN p_fechaFin DATE
)
BEGIN
    SELECT 
        P.nombre AS Piso,
        COUNT(*) AS TotalReservas
    FROM Reservas R
    INNER JOIN DetalleReserva DR ON R.idreserva = DR.reservaid
    INNER JOIN Habitaciones H ON DR.habitacionid = H.idhabitacion
    INNER JOIN Pisos P ON H.pisoid = P.idpiso
    WHERE R.fechaentrada BETWEEN p_fechaInicio AND p_fechaFin
    GROUP BY P.idpiso
    ORDER BY TotalReservas DESC;
END //

DELIMITER ;


DELIMITER //

CREATE PROCEDURE SP_TotalReservasConfirmadas(
    IN p_fechaInicio DATE,
    IN p_fechaFin DATE
)
BEGIN
    SELECT COUNT(*) AS Total
    FROM Reservas R
    WHERE R.fechaentrada >= p_fechaInicio
      AND R.fechaentrada <= p_fechaFin
      AND R.estadoid IN (
        SELECT idestadores FROM EstadoReserva WHERE nombre IN ('Confirmado', 'Finalizado')
      );
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

/***********************************************************************************************************************/
/*****************************************    PROCEDURES PARA CLIENTE     **********************************************/
/***********************************************************************************************************************/

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

DELIMITER //

CREATE PROCEDURE SP_DetalleHabitacionesSeleccionadas (
    IN FechaEntrada DATE,
    IN FechaSalida DATE,
    IN HabIds TEXT
)
BEGIN
    SET @query = CONCAT(
        'SELECT 
            H.idhabitacion,
            TH.nombre AS tipoHabitacion,
            H.numhabitacion,
            P.nombre AS piso,
            ? AS fechaEntrada,
            ? AS fechaSalida,
            TH.precioxdia,
            DATEDIFF(?, ?) AS cantidadDias,
            (TH.precioxdia * DATEDIFF(?, ?)) AS total
        FROM Habitaciones H
        JOIN TipoHabitacion TH ON H.tipohabid = TH.idtipohab
        JOIN Pisos P ON H.pisoid = P.idpiso
        WHERE H.idhabitacion IN (', HabIds, ')'
    );

    PREPARE stmt FROM @query;
    EXECUTE stmt USING @FechaEntrada, @FechaSalida, @FechaSalida, @FechaEntrada, @FechaSalida, @FechaEntrada;
    DEALLOCATE PREPARE stmt;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE SP_RegistrarReservaConDetalles (
    IN p_usuarioId INT,
    IN p_fechaEntrada DATE,
    IN p_fechaSalida DATE,
    IN p_estadoId INT,
    IN p_listaHabitaciones TEXT,
    OUT p_idReserva INT
)
BEGIN
    DECLARE v_nombre VARCHAR(50);
    DECLARE v_apellido VARCHAR(50);
    DECLARE v_codigoReserva VARCHAR(50);
    DECLARE v_dia VARCHAR(2);
    DECLARE v_inicialN CHAR(1);
    DECLARE v_inicialA CHAR(1);
    DECLARE v_random CHAR(3);
    DECLARE v_habitacionId INT;
    DECLARE done INT DEFAULT 0;

    DECLARE cur_hab CURSOR FOR 
        SELECT CAST(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(p_listaHabitaciones, ',', n.n), ',', -1)) AS UNSIGNED) AS habId
        FROM (
            SELECT a.N + b.N * 10 + 1 AS n
            FROM 
              (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
               UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
              (SELECT 0 AS N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4
               UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) b
            WHERE a.N + b.N * 10 < CHAR_LENGTH(p_listaHabitaciones) - CHAR_LENGTH(REPLACE(p_listaHabitaciones, ',', '')) + 1
        ) n;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    SELECT nombre, apellido INTO v_nombre, v_apellido
    FROM Usuarios
    WHERE idusuario = p_usuarioId;

    SET @dias = DATEDIFF(p_fechaSalida, p_fechaEntrada);

    OPEN cur_hab;

    hab_loop: LOOP
        FETCH cur_hab INTO v_habitacionId;
        IF done THEN
            LEAVE hab_loop;
        END IF;

        SET v_dia = DATE_FORMAT(CURDATE(), '%d');
        SET v_inicialN = LEFT(v_nombre, 1);
        SET v_inicialA = LEFT(v_apellido, 1);
        SET v_random = LPAD(FLOOR(RAND() * 1000), 3, '0');
        SET v_codigoReserva = CONCAT('RSV-', v_dia, v_inicialN, v_inicialA, '-', v_random);

        INSERT INTO Reservas (codigoreserva, usuarioid, fechaentrada, fechasalida, estadoid, horaentrada, horasalida)
        VALUES (v_codigoReserva, p_usuarioId, p_fechaEntrada, p_fechaSalida, p_estadoId, '12:00:00', '12:00:00');

		SET p_idReserva = LAST_INSERT_ID();

        INSERT INTO DetalleReserva (reservaid, habitacionid, cantidaddias)
        VALUES (p_idReserva, v_habitacionId, @dias);

        IF p_estadoId IN (2, 3) THEN
            UPDATE Habitaciones
            SET estadohabid = 5
            WHERE idhabitacion = v_habitacionId;
        END IF;

    END LOOP;

    CLOSE cur_hab;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE SP_DetalleReserva (
    IN p_idReserva INT
)
BEGIN
    SELECT 
        U.correo AS CorreoDestino,
        R.codigoreserva AS CodigoReserva,
        CONCAT(U.nombre, ' ', U.apellido) AS Cliente,
        H.numhabitacion AS NumHabitacion,
        TH.nombre AS TipoHabitacion,
        P.nombre AS Piso,
        R.fechaentrada AS FechaEntrada,
        R.fechasalida AS FechaSalida,
        TH.precioxdia AS PrecioPorDia,
        DATEDIFF(R.fechasalida, R.fechaentrada) AS CantidadDias,
        DATEDIFF(R.fechasalida, R.fechaentrada) * TH.precioxdia AS Total
    FROM Reservas R
        JOIN DetalleReserva DR ON R.idreserva = DR.reservaid
        JOIN Habitaciones H ON DR.habitacionid = H.idhabitacion
        JOIN TipoHabitacion TH ON H.tipohabid = TH.idtipohab
        JOIN Pisos P ON H.pisoid = P.idpiso
        JOIN Usuarios U ON R.usuarioid = U.idusuario
    WHERE R.idreserva = p_idReserva
    LIMIT 1;
END //

DELIMITER ;


DELIMITER //

CREATE PROCEDURE SP_ListarMisReservas (
    IN p_usuarioId INT
)
BEGIN
    SELECT 
        R.idreserva,
        H.numhabitacion,
        TH.nombre AS tipoHabitacion,
        R.fechaentrada,
        DR.cantidaddias,
        ER.nombre AS estadoReserva
    FROM Reservas R
    INNER JOIN DetalleReserva DR ON R.idreserva = DR.reservaid
    INNER JOIN Habitaciones H ON DR.habitacionid = H.idhabitacion
    INNER JOIN TipoHabitacion TH ON H.tipohabid = TH.idtipohab
    INNER JOIN EstadoReserva ER ON R.estadoid = ER.idestadores
    WHERE R.usuarioid = p_usuarioId
    ORDER BY R.fechaentrada DESC, R.idreserva DESC;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE SP_CancelarReserva(
IN p_reservaId INT
)
BEGIN
    DECLARE done INT DEFAULT 0;
    DECLARE habId INT;
    DECLARE cur CURSOR FOR 
        SELECT habitacionid FROM DetalleReserva WHERE reservaid = p_reservaId;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

    UPDATE Reservas
    SET estadoid = 4 
    WHERE idreserva = p_reservaId;

    OPEN cur;
    bucle: LOOP
        FETCH cur INTO habId;
        IF done THEN
            LEAVE bucle;
        END IF;
        UPDATE Habitaciones
        SET estadohabid = 1 
        WHERE idhabitacion = habId;
    END LOOP;
    CLOSE cur;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE SP_ActualizarRolUsuario(
    IN p_usuarioId INT,
    IN p_nuevoRolId INT
)
BEGIN
    UPDATE Usuarios
    SET rolid = p_nuevoRolId
    WHERE idusuario = p_usuarioId;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE SP_ListarPisosPorTipo(
IN tipoHabId INT
)
BEGIN
    SELECT DISTINCT
        P.idpiso,
        P.nombre
    FROM
        Habitaciones H
    INNER JOIN Pisos P ON H.pisoid = P.idpiso
    WHERE
        H.tipohabid = tipoHabId;
END //

DELIMITER ;

CALL SP_DetalleReserva(11);
SELECT idhabitacion FROM Habitaciones WHERE numhabitacion = 101;
SELECT * FROM Habitaciones WHERE idhabitacion = 1;
SELECT * FROM reservas;
SELECT * FROM DetalleReserva WHERE reservaid = ?;

SELECT * FROM Habitaciones
WHERE pisoid NOT IN (SELECT idpiso FROM Pisos);