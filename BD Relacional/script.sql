CREATE SCHEMA `SistemaGestionUR`;
USE `SistemaGestionUR`;

-- Tabla de Usuarios
CREATE TABLE `usuario` (
  `idusuario` INT NOT NULL AUTO_INCREMENT,
  `usuario` VARCHAR(15) NOT NULL UNIQUE,
  `nombre_completo` VARCHAR(100) NOT NULL,
  `tipo_documento` VARCHAR(100) NULL,
  `numero_documento` VARCHAR(45) UNIQUE NULL,
  `correo_electronico` VARCHAR(100) UNIQUE NULL,
  `telefono` VARCHAR(20) NULL,
  `direccion_interna` VARCHAR(100) NULL,
  `rol` ENUM('propietario', 'inquilino', 'anonimo') NOT NULL,
  `contrasena` VARCHAR(25) NULL,
  PRIMARY KEY (`idusuario`),
  CHECK (
    (rol = 'anonimo' AND nombre_completo = 'Anónimo' AND tipo_documento IS NULL AND 
     numero_documento IS NULL AND correo_electronico IS NULL AND telefono IS NULL AND 
     direccion_interna IS NULL) 
    OR 
    (rol IN ('propietario', 'inquilino'))
  )
);

-- Tabla de Administradores
CREATE TABLE `admin` (
  `idadmin` INT NOT NULL AUTO_INCREMENT,
  `usuario` VARCHAR(45) NOT NULL UNIQUE,
  `contrasena` VARCHAR(25) NOT NULL,
  PRIMARY KEY (`idadmin`)
);

-- Tabla de Solicitudes PQRS
CREATE TABLE `solicitud` (
  `idsolicitud` INT NOT NULL AUTO_INCREMENT,
  `tipo` ENUM('peticion', 'queja', 'reclamo', 'sugerencia') NOT NULL,
  `categoria` ENUM('serviciosGenerales', 'seguridad', 'areasComunes') NOT NULL,
  `descripcion` LONGTEXT NOT NULL,
  `estado` ENUM('radicada', 'enProceso', 'resuelta', 'cerrada', 'reabierta') NOT NULL DEFAULT 'radicada',
  `fecha_hora_creacion` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
  `idusuario` INT NOT NULL,
  PRIMARY KEY (`idsolicitud`),
  FOREIGN KEY (`idusuario`) REFERENCES `usuario`(`idusuario`)
);

-- Tabla de Respuestas 
CREATE TABLE `respuesta` (
  `idrespuesta` INT NOT NULL AUTO_INCREMENT,
  `ruta_oficio_pdf` VARCHAR(255) NULL,
  `comentario` TEXT NOT NULL,
  `fecha_respuesta` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `puntuacion` INT CHECK (`puntuacion` BETWEEN 1 AND 5),
  `idsolicitud` INT NOT NULL,
  `idadmin` INT NULL,
  `idusuario` INT NULL,
  `respuestaid` INT NULL,
  PRIMARY KEY (`idrespuesta`),
  FOREIGN KEY (`idsolicitud`) REFERENCES `solicitud`(`idsolicitud`),
  FOREIGN KEY (`idadmin`) REFERENCES `admin`(`idadmin`),
  FOREIGN KEY (`idusuario`) REFERENCES `usuario`(`idusuario`),
  FOREIGN KEY (`respuestaid`) REFERENCES `respuesta`(`idrespuesta`)
);

-- Tabla de Evidencias Adjuntas
CREATE TABLE `evidencia` (
  `idevidencia` INT NOT NULL AUTO_INCREMENT,
  `tipo_archivo` ENUM('pdf', 'video', 'imagen', 'audio', 'otro') NOT NULL,
  `rutaarchivo` VARCHAR(255) NULL,
  `descripcion` TEXT NULL,
  `fecha_hora_carga` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `idsolicitud` INT NOT NULL,
  PRIMARY KEY (`idevidencia`),
  FOREIGN KEY (`idsolicitud`) REFERENCES `solicitud`(`idsolicitud`)
);

-- OBJETOS ALMACENADOS
USE `SistemaGestionUR`;

-- Evento: cerrar solicitud tras 15 segundos
CREATE EVENT cerrarSolicitudesSinReplica
ON SCHEDULE EVERY 15 SECOND
DO
  UPDATE solicitud s
  SET s.estado = 'cerrada',
      s.fecha_actualizacion = NOW()
  WHERE s.estado = 'resuelta'
    AND NOT EXISTS (
      SELECT 1
      FROM respuesta r
      JOIN usuario u ON u.idusuario = r.idusuario
      WHERE r.idsolicitud = s.idsolicitud
        AND u.rol IN ('propietario', 'inquilino')
    );

-- Trigger: impedir modificar solicitudes cerradas
DELIMITER //

CREATE TRIGGER evitarCambiosSiCerrada
BEFORE UPDATE ON solicitud
FOR EACH ROW
BEGIN
    IF OLD.estado = 'cerrada' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede modificar una solicitud cerrada.';
    END IF;
END;
//

DELIMITER ;

-- Trigger: Validar que los usuarios anónimos no tengan datos personales
DELIMITER $$

CREATE TRIGGER validarUsuarioAnonimo
BEFORE INSERT ON usuario
FOR EACH ROW
BEGIN
  IF NEW.rol = 'anonimo' THEN
    SET NEW.nombre_completo = 'Anónimo',
        NEW.tipo_documento = NULL,
        NEW.numero_documento = NULL,
        NEW.correo_electronico = NULL,
        NEW.telefono = NULL,
        NEW.direccion_interna = NULL;
  END IF;
END$$

-- Trigger: garantizar que la puntuacion sea entre 1 y 5
DELIMITER $$

CREATE TRIGGER validarPuntuacionRespuesta
BEFORE INSERT ON respuesta
FOR EACH ROW
BEGIN
  IF NEW.puntuacion IS NOT NULL AND (NEW.puntuacion < 1 OR NEW.puntuacion > 5) THEN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = 'La puntuación debe estar entre 1 y 5';
  END IF;
END$$

DELIMITER ;

-- Trigger: pasar a resuelta cuando un admin responde
DELIMITER $$

CREATE TRIGGER marcarComoResueltaPorAdmin
AFTER INSERT ON respuesta
FOR EACH ROW
BEGIN
  IF NEW.idadmin IS NOT NULL AND NEW.respuestaid IS NULL THEN
    UPDATE solicitud
    SET estado = 'resuelta',
        fecha_actualizacion = NOW()
    WHERE idsolicitud = NEW.idsolicitud;
  END IF;
END$$

DELIMITER ;

-- Trigger: validad que no son anonimos par replicar
DELIMITER $$

CREATE TRIGGER validarReplicaPorRol
BEFORE INSERT ON respuesta
FOR EACH ROW
BEGIN
  DECLARE user_rol VARCHAR(20);

  IF NEW.idusuario IS NOT NULL THEN
    SELECT rol INTO user_rol
    FROM usuario
    WHERE idusuario = NEW.idusuario;

    IF user_rol = 'anonimo' THEN
      SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Los usuarios anónimos no pueden enviar réplicas.';
    END IF;
  END IF;
END$$

DELIMITER ;

-- Trigger: Validar calificacion de respuestas de admin
DELIMITER $$

CREATE TRIGGER validarCalificacionRespuesta
BEFORE UPDATE ON respuesta
FOR EACH ROW
BEGIN
  DECLARE rol_usuario VARCHAR(20);

  IF NEW.idusuario IS NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Solo los usuarios registrados pueden calificar respuestas.';
  END IF;

  SELECT rol INTO rol_usuario FROM usuario WHERE idusuario = NEW.idusuario;

  IF rol_usuario = 'anonimo' THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Los usuarios anónimos no pueden calificar respuestas.';
  END IF;

  IF OLD.puntuacion IS NOT NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La respuesta ya ha sido calificada.';
  END IF;

  IF OLD.idusuario IS NOT NULL THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se pueden calificar respuestas hechas por usuarios.';
  END IF;

END$$

DELIMITER ;

-- Trigger: Eliminar solicitudes solo si esta en "radicada"
DELIMITER $$

CREATE TRIGGER validarEliminacionSolicitud
BEFORE DELETE ON solicitud
FOR EACH ROW
BEGIN
  IF OLD.estado != 'radicada' THEN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = 'Solo se pueden eliminar solicitudes en estado radicada.';
  END IF;
END$$

DELIMITER ;

-- TRigger: validar la respuesta inicial
DELIMITER $$

CREATE TRIGGER validarRespuestaInicial
BEFORE INSERT ON respuesta
FOR EACH ROW
BEGIN
  DECLARE estado_actual VARCHAR(20);
  DECLARE solicitante INT;
  DECLARE rol_usuario VARCHAR(20);

  IF NEW.respuestaid IS NULL THEN
    -- Obtener estado y solicitante
    SELECT estado, idusuario INTO estado_actual, solicitante
    FROM solicitud
    WHERE idsolicitud = NEW.idsolicitud;

    -- Verificar estado cerrado
    IF estado_actual = 'cerrada' THEN
      SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede responder a una solicitud cerrada.';
    END IF;

    -- Debe haber un único autor
    IF (NEW.idusuario IS NOT NULL AND NEW.idadmin IS NOT NULL) OR
       (NEW.idusuario IS NULL AND NEW.idadmin IS NULL) THEN
      SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'La respuesta debe tener un único creador: usuario o administrador.';
    END IF;

    -- Si es usuario, validar que sea el creador y no sea anónimo
    IF NEW.idusuario IS NOT NULL THEN
      SELECT rol INTO rol_usuario FROM usuario WHERE idusuario = NEW.idusuario;

      IF rol_usuario = 'anonimo' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Los usuarios anónimos no pueden responder.';
      END IF;

      IF NEW.idusuario != solicitante THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Solo el creador de la solicitud puede responder.';
      END IF;

    END IF;
  END IF;
END$$
DELIMITER ;

-- Trigger: Validar réplica de respuesta
DELIMITER $$

CREATE TRIGGER validarReplicaUnica
BEFORE INSERT ON respuesta
FOR EACH ROW
BEGIN
  DECLARE r_usuario INT;
  DECLARE r_admin INT;

  IF NEW.respuestaid IS NOT NULL THEN
    SELECT idusuario, idadmin INTO r_usuario, r_admin
    FROM respuesta
    WHERE idrespuesta = NEW.respuestaid;

    -- No responderse a sí mismo
    IF (NEW.idusuario IS NOT NULL AND NEW.idusuario = r_usuario) OR
       (NEW.idadmin IS NOT NULL AND NEW.idadmin = r_admin) THEN
      SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'No se puede responder a una respuesta propia.';
    END IF;

    -- Solo una réplica por parte
    IF NEW.idusuario IS NOT NULL THEN
      IF EXISTS (
        SELECT 1 FROM respuesta
        WHERE respuestaid = NEW.respuestaid AND idusuario = NEW.idusuario
      ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario ya respondió a esta respuesta.';
      END IF;
    ELSE
      IF EXISTS (
        SELECT 1 FROM respuesta
        WHERE respuestaid = NEW.respuestaid AND idadmin = NEW.idadmin
      ) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El administrador ya respondió a esta respuesta.';
      END IF;
    END IF;
  END IF;
END$$

DELIMITER ;

-- Validar respuesta a replica (admmin)
DELIMITER $$
CREATE TRIGGER validarSegundaReplicaFinal
BEFORE INSERT ON respuesta
FOR EACH ROW
BEGIN
  DECLARE anterior_usuario INT;
  DECLARE anterior_admin INT;

  IF NEW.respuestaid IS NOT NULL THEN
    SELECT idusuario, idadmin INTO anterior_usuario, anterior_admin
    FROM respuesta
    WHERE idrespuesta = NEW.respuestaid;

    IF NEW.idusuario IS NOT NULL THEN
      IF anterior_usuario IS NOT NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede responder a una respuesta propia (usuario).';
      END IF;
    ELSE
      IF anterior_admin IS NOT NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede responder a una respuesta propia (admin).';
      END IF;
    END IF;

    IF EXISTS (
      SELECT 1 FROM respuesta
      WHERE respuestaid = NEW.respuestaid AND (
        (NEW.idusuario IS NOT NULL AND idusuario IS NOT NULL) OR
        (NEW.idadmin IS NOT NULL AND idadmin IS NOT NULL)
      )
    ) THEN
      SIGNAL SQLSTATE '45000'
      SET MESSAGE_TEXT = 'Solo se permite una réplica por cada parte.';
    END IF;
  END IF;
END$$

DELIMITER ;

-- Procedimiento: Pasar a reabierta 
DELIMITER $$

CREATE PROCEDURE reabrirSolicitud(
  IN p_idsolicitud INT,
  IN p_idusuario INT,
  IN p_comentario TEXT
)
BEGIN
  DECLARE rol_usuario VARCHAR(20);
  DECLARE estado_actual VARCHAR(20);

  SELECT rol INTO rol_usuario
  FROM usuario
  WHERE idusuario = p_idusuario;

  IF rol_usuario = 'anonimo' THEN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = 'Los usuarios anónimos no pueden reabrir solicitudes.';
  END IF;

  SELECT estado INTO estado_actual
  FROM solicitud
  WHERE idsolicitud = p_idsolicitud;

  IF estado_actual != 'resuelta' THEN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = 'Solo se pueden reabrir solicitudes en estado resuelta.';
  END IF;

  UPDATE solicitud
  SET estado = 'reabierta',
      fecha_actualizacion = NOW()
  WHERE idsolicitud = p_idsolicitud;

  INSERT INTO respuesta (comentario, idsolicitud, idusuario)
  VALUES (p_comentario, p_idsolicitud, p_idusuario);
END$$

DELIMITER ;

-- Procedimiento: cantidad de solicitudes por estado y por categoria
DELIMITER $$

CREATE PROCEDURE obtenerEstadisticasPqrs()
BEGIN
  SELECT 
    categoria,
    estado,
    COUNT(*) AS cantidad
  FROM solicitud
  GROUP BY categoria, estado
  ORDER BY categoria, estado;
END$$

DELIMITER ;
