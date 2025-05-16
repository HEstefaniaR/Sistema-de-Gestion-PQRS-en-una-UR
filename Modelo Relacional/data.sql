-- Usuarios
INSERT INTO usuario (usuario, nombre_completo, tipo_documento, numero_documento, correo_electronico, telefono, direccion_interna, rol, contrasena)
VALUES
('jlopez', 'Juan López', 'CC', '12345678', 'juan@example.com', '3123456789', 'Apartamento 101', 'propietario', 'pass123'),
('mruiz', 'María Ruiz', 'CC', '87654321', 'maria@example.com', '3129876543', 'Casa 23', 'inquilino', 'maria321'), 
('cperez', 'Carlos Pérez', 'CC', '11223344', 'carlos@example.com', '3101112233', 'Apartamento 202', 'propietario', 'carlos456'),
('avillamizar', 'Ana Villamizar', 'CC', '33445566', 'ana@example.com', '3103344556', 'Casa 15', 'propietario', 'ana789'),
('jrodriguez', 'Jorge Rodríguez', 'CE', '55667788', 'jorge@example.com', '3115566778', 'Apartamento 305', 'inquilino', 'jorge123'),
('llopez', 'Laura López', 'CC', '99887766', 'laura@example.com', '3129988776', 'Apartamento 103', 'inquilino', 'laura321'),
('fmedina', 'Felipe Medina', 'CC', '66554433', 'felipe@example.com', '3116655443', 'Casa 6', 'propietario', 'felipepass'),
('cgarcia', 'Camila García', 'CC', '22113344', 'camila@example.com', '3102211334', 'Casa 12', 'propietario', 'camila123'),
('aramos', 'Andrés Ramos', 'CE', '44556677', 'andres@example.com', '3114455667', 'Apartamento 401', 'inquilino', 'andres456'),
('nluna', 'Natalia Luna', 'CC', '77889900', 'natalia@example.com', '3137788990', 'Apartamento 104', 'propietario', 'natalia789'),
('omartinez', 'Oscar Martínez', 'CC', '99882211', 'oscar@example.com', '3129988221', 'Casa 9', 'inquilino', 'oscarpass'),
('dquintero', 'Diana Quintero', 'CC', '55443322', 'diana@example.com', '3115544332', 'Apartamento 207', 'propietario', 'diana456'),
('jperez', 'Juan Pérez', 'CC', '1234567890', 'juanperez@example.com', '3110000000', 'Calle 123', 'inquilino', '1234');

-- Anónimos
INSERT INTO usuario (usuario, nombre_completo, rol)
VALUES 
('anon_1', 'Anónimo', 'anonimo'),
('anon_2', 'Anónimo', 'anonimo'),
('anon_3', 'Anónimo', 'anonimo'),
('anon_4', 'Anónimo', 'anonimo'),
('anon_5', 'Anónimo', 'anonimo'),
('anon_6', 'Anónimo', 'anonimo'),
('anon_7', 'Anónimo', 'anonimo'),
('anon_8', 'Anónimo', 'anonimo'),
('anon_9', 'Anónimo', 'anonimo');

-- Admins
INSERT INTO admin (usuario, contrasena)
VALUES 
('admin1', 'admin123'),
('admin2', 'secure456');

-- Solicitudes
INSERT INTO solicitud (tipo, categoria, descripcion, estado, fecha_hora_creacion, fecha_actualizacion, idusuario) VALUES
('reclamo', 'serviciosGenerales', 'La recolección de basura no se está haciendo en los horarios establecidos.', 'radicada', '2025-04-03 09:10:00', '2025-04-04 10:00:00', 4),
('peticion', 'areasComunes', 'Se solicita colocar una banca adicional en la zona de juegos.', 'radicada', '2025-04-04 11:30:00', '2025-04-06 09:00:00', 5),
('queja', 'seguridad', 'Se han visto personas sospechosas rondando la entrada trasera.', 'radicada', '2025-04-05 20:00:00', '2025-04-06 14:30:00', 6),
('sugerencia', 'areasComunes', 'Sería útil tener un calendario de uso del salón comunal.', 'radicada', '2025-04-06 13:15:00', '2025-04-08 08:20:00', 7),
('reclamo', 'serviciosGenerales', 'La presión del agua en mi casa es muy baja desde hace días.', 'radicada', '2025-04-07 08:25:00', '2025-04-08 09:30:00', 8),
('peticion', 'seguridad', 'Solicito la instalación de cámaras en el parqueadero.', 'radicada', '2025-04-08 17:45:00', '2025-04-09 12:00:00', 1),
('queja', 'areasComunes', 'Los juegos infantiles están oxidados y podrían causar accidentes.', 'radicada', '2025-04-09 10:50:00', '2025-04-10 16:00:00', 2),
('sugerencia', 'serviciosGenerales', 'Podríamos usar energía solar para las luces externas.', 'radicada', '2025-04-10 14:40:00', '2025-04-12 10:45:00', 3),
('peticion', 'serviciosGenerales', 'Solicito revisión del sistema eléctrico del edificio 3.', 'radicada', '2025-04-11 09:00:00', '2025-04-12 08:50:00', 4),
('queja', 'seguridad', 'El vigilante de la noche suele quedarse dormido.', 'radicada', '2025-04-12 00:30:00', '2025-04-13 02:00:00', 5),
('sugerencia', 'seguridad', 'Proponer patrullajes internos cada 2 horas.', 'radicada', '2025-04-13 18:10:00', '2025-04-14 12:00:00', 6),
('reclamo', 'areasComunes', 'El gimnasio permanece cerrado sin aviso.', 'radicada', '2025-04-14 15:35:00', '2025-04-16 09:10:00', 7),
('peticion', 'serviciosGenerales', 'Solicito limpieza urgente de las escaleras del bloque B.', 'radicada', '2025-04-15 07:10:00', '2025-04-15 15:00:00', 8),
('queja', 'seguridad', 'Las cámaras del parque están dañadas.', 'radicada', '2025-04-16 06:55:00', '2025-04-17 08:30:00', 1),
('sugerencia', 'areasComunes', 'Instalar señalética para ubicar zonas comunes fácilmente.', 'radicada', '2025-04-17 10:20:00', '2025-04-18 09:45:00', 2),
('peticion', 'serviciosGenerales', 'Solicitud de mantenimiento del ascensor de la torre 3.', 'radicada', '2025-03-01 08:30:00', '2025-03-03 10:00:00', 2),
('queja', 'seguridad', 'Ruido excesivo en el apartamento 502 después de las 11 PM.', 'radicada', '2025-03-02 21:15:00', '2025-03-04 08:00:00', 1),
('reclamo', 'serviciosGenerales', 'Cobro indebido en la factura de administración de marzo.', 'radicada', '2025-03-03 10:00:00', '2025-03-05 09:00:00', 3),
('sugerencia', 'seguridad', 'Instalación de más cámaras de seguridad en el parqueadero.', 'radicada', '2025-03-04 14:45:00', '2025-03-06 11:15:00', 2),
('peticion', 'serviciosGenerales', 'Aumento de frecuencia en la recolección de basuras.', 'radicada', '2025-03-05 16:20:00', '2025-03-06 18:00:00', 6),
('queja', 'serviciosGenerales', 'Problemas con la presión del agua en la torre 2.', 'radicada', '2025-04-02 07:50:00', '2025-04-03 09:00:00', 5),
('queja', 'serviciosGenerales', 'El ascensor se detiene entre pisos frecuentemente.', 'radicada', '2025-04-18 09:00:00', '2025-04-19 10:00:00', 5),
('reclamo', 'areasComunes', 'El techo de la portería presenta filtraciones.', 'radicada', '2025-04-19 14:30:00', '2025-04-20 11:00:00', 5);

-- Respuestas admin
INSERT INTO respuesta (comentario, idsolicitud, idadmin, puntuacion)
VALUES
('Se ha notificado a la empresa encargada para ajustar los horarios de recolección.', 1, 1, 2),
('La banca adicional será instalada la próxima semana.', 2, 2, 3),
('Estamos coordinando con la policía local para mejorar la vigilancia.', 3, 1, 2),
('La propuesta ha sido enviada al comité de convivencia.', 4, 2, 4),
('Un técnico revisará el sistema hidráulico mañana.', 5, 1, 1),
('Se está evaluando la instalación de cámaras en el parqueadero.', 6, 2, 3),
('El área de mantenimiento revisará los juegos esta semana.', 7, 1, 2),
('El tema será incluido en la próxima reunión de copropietarios.', 8, 1, 4),
('Se asignó un técnico para revisar el sistema eléctrico hoy.', 9, 2, 5),
('Se hablará con el vigilante nocturno y se tomarán medidas.', 10, 1, 4),
('Estamos verificando el cierre del gimnasio con el área correspondiente.', 12, 2, 2),
('Se enviará un técnico a revisar el ascensor esta semana.', 22, 2, 2),
('El problema del techo será evaluado en el próximo mes.', 23, 1, 1),
('Se hará seguimiento semanal al área común reportada.', 12, 1, 3);
-- Réplicas usuarios
INSERT INTO respuesta (comentario, idsolicitud, respuestaid, idusuario)
VALUES
('Han pasado varios días y la basura sigue acumulada. ¿Cuándo lo resolverán?', 1, 1, 1),
('La banca aún no ha sido instalada. ¿Hubo algún inconveniente?', 2, 2, 2),
('Hoy volvieron a verse personas sospechosas. ¿Se pueden tomar medidas urgentes?', 3, 3, 3),
('La presión del agua sigue muy baja. El técnico no vino.', 5, 5, 5),
('Los juegos siguen igual. Nadie ha venido a revisarlos.', 7, 7, 7),
('El gimnasio sigue cerrado. No se ha informado nada nuevo.', 12, 11, 12);

-- Respuestas admin
INSERT INTO respuesta (comentario, idsolicitud, idadmin, respuestaid, puntuacion)
VALUES
('Hemos cambiado de proveedor. El nuevo operador iniciará mañana.', 1, 1, 15, 4),
('Tuvimos retraso por lluvias. La banca será instalada esta semana.', 2, 2, 16, 3),
('Se reforzará la vigilancia con rondas cada hora en la noche.', 3, 1, 17, 4),
('El técnico tuvo una emergencia. Irá sin falta mañana a primera hora.', 5, 1, 18, 5),
('Ya se asignó al personal de mantenimiento para esta tarde.', 7, 1, 19, 5),
('Se publicará un comunicado hoy con los horarios de reapertura.', 12, 2, 20, 4);

-- Evidencias
INSERT INTO evidencia (tipo_archivo, rutaarchivo, descripcion, idsolicitud)
VALUES
('imagen', 'uploads/3c4fd158-1e74-4cc7-a993-33bd7778d69a_placeholder.png', 'Foto 1: Entrada con poca visibilidad', 1),
('imagen', 'uploads/8a817637-e48e-4f10-8da1-9626a1813a9c_placeholder.png', 'Foto 2: Pasillo sin iluminación', 2),
('imagen', 'uploads/54ed2bbb-6645-4df3-a304-7fd8e69f837e_placeholder.png', 'Foto 3: Área de juegos rota', 3),
('imagen', 'uploads/cd959b5f-8f47-4d12-b4ae-84e685bc853b_placeholder.png', 'Foto 4: Rejas dañadas', 1),
('imagen', 'uploads/e6eb1a96-700d-42b7-9861-c4acbf9b5536_placeholder.png', 'Foto 5: Vidrio roto en escaleras', 2),
('imagen', 'uploads/fecb071c-9dc4-4f4a-9e9f-cc385017c2fc_placeholder.png', 'Foto 6: Zona verde descuidada', 3);