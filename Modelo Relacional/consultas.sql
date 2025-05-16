-- Personas con mas de 2 solicitudes y una calificacion menor a 3
SELECT  U.nombre_completo, COUNT(S.idsolicitud) AS totalSolHechas, 
MIN(R.puntuacion) AS peorCalificacion
FROM usuario AS U
INNER JOIN solicitud AS S USING (idusuario)
INNER JOIN respuesta AS R USING (idsolicitud)
GROUP BY U.idusuario, U.nombre_completo
HAVING COUNT(S.idsolicitud) > 2 AND MIN(R.puntuacion) IN (1, 2);

-- Administrador que ha respondido más solicitudes
SELECT A.idadmin, A.usuario AS usuarioAdmin, COUNT(R.idrespuesta) AS totalRespuestas
FROM admin AS A
INNER JOIN respuesta AS R USING (idadmin)
GROUP BY A.idadmin
HAVING totalRespuestas = (
    SELECT MAX(respuestasPorAdmin) 
    FROM (
        SELECT COUNT(idrespuesta) AS respuestasPorAdmin
        FROM respuesta
        GROUP BY idadmin
    ) AS subconsulta
);

-- Promedio general de calificaciones y promedio de respuestas por solicitud
SELECT (SELECT ROUND(AVG(puntuacion), 2) FROM respuesta) AS promedio_general_calificaciones,
    (SELECT ROUND(AVG(conteo), 2) 
     FROM (SELECT COUNT(*) AS conteo 
           FROM respuesta 
           GROUP BY idsolicitud) AS temp) AS promedio_respuestas_por_solicitud;

-- Solicitudes con más respuestas de administradores que el promedio
SELECT 
    s.idsolicitud,
    s.tipo,
    s.descripcion,
    s.estado,
    COUNT(r.idrespuesta) AS num_respuestas_admin,
    (
        SELECT AVG(conteo) 
        FROM (
            SELECT COUNT(*) AS conteo 
            FROM respuesta 
            WHERE idadmin IS NOT NULL
            GROUP BY idsolicitud
        ) AS promedioRespuestas
    ) AS promedio_sistema
FROM solicitud s
INNER JOIN respuesta r ON s.idsolicitud = r.idsolicitud
WHERE r.idadmin IS NOT NULL
GROUP BY s.idsolicitud
HAVING COUNT(r.idrespuesta) > promedio_sistema
ORDER BY num_respuestas_admin DESC;

-- Clientes con mayor cantidad de quejas en el ultimo mes
SELECT u.nombre_completo AS cliente, COUNT(*) AS total_quejas
FROM solicitud s
INNER JOIN usuario u USING (idusuario)
WHERE s.tipo = 'queja' 
  AND s.fecha_hora_creacion >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)
GROUP BY u.idusuario, u.nombre_completo
HAVING total_quejas = (
    SELECT MAX(quejas_por_usuario) FROM (
        SELECT COUNT(*) AS quejas_por_usuario
        FROM solicitud
        WHERE tipo = 'queja'
          AND fecha_hora_creacion >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)
        GROUP BY idusuario
    ) AS sub
)
ORDER BY total_quejas DESC;

-- Clientes con mayor cantidad de reclamos
SELECT u.nombre_completo AS cliente, COUNT(s.idsolicitud) AS total_reclamos
FROM solicitud s
INNER JOIN usuario u USING (idusuario)
WHERE s.tipo = 'reclamo'
GROUP BY u.idusuario, u.nombre_completo
ORDER BY total_reclamos DESC;

-- Tiempo promedio, mínimo y máximo de las solicitudes con evidencia resueltas (sin valores negativos)
SELECT s.tipo, 
       AVG(DATEDIFF(s.fecha_actualizacion, s.fecha_hora_creacion)) AS tiempo_promedio_dias,
       MIN(DATEDIFF(s.fecha_actualizacion, s.fecha_hora_creacion)) AS tiempo_minimo_dias,
       MAX(DATEDIFF(s.fecha_actualizacion, s.fecha_hora_creacion)) AS tiempo_maximo_dias,
       COUNT(*) AS total_solicitudes_resueltas_con_evidencia
FROM solicitud s
JOIN evidencia e ON s.idsolicitud = e.idsolicitud
WHERE s.estado = 'resuelta'
  AND s.fecha_actualizacion >= s.fecha_hora_creacion
GROUP BY s.tipo
ORDER BY tiempo_promedio_dias DESC;

-- Cantidad de solicitudes resueltas atendidas por cada administrador
SELECT a.usuario, COUNT(DISTINCT s.idsolicitud) AS solicitudes_resueltas
FROM admin a
INNER JOIN respuesta r USING (idadmin)
INNER JOIN solicitud s USING (idsolicitud)
WHERE s.estado = 'resuelta'
GROUP BY a.idadmin
ORDER BY solicitudes_resueltas DESC;

-- Usuarios que han subido evidencias
SELECT u.idusuario, u.nombre_completo, COUNT(e.idevidencia) AS total_evidencias
FROM usuario u
INNER JOIN solicitud s USING (idusuario)
INNER JOIN evidencia e USING (idsolicitud)
GROUP BY u.idusuario
HAVING total_evidencias > 0
ORDER BY total_evidencias DESC;

-- Calificacion promedio de respuesta por tipo de solicitud
SELECT s.tipo, ROUND(AVG(r.puntuacion),2) AS calificacion_promedio
FROM solicitud s
INNER JOIN respuesta r USING (idsolicitud)
GROUP BY s.tipo
ORDER BY calificacion_promedio DESC;