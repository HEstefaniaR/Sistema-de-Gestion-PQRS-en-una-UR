package com.apirest.backend.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.apirest.backend.Model.*;
import com.apirest.backend.Service.*;

@RestController
@RequestMapping("UR/respuestas")
public class RespuestaControlador {

    @Autowired private IRespuestaService respuestaService;
    @Autowired private ISolicitudesService solicitudService;
    @Autowired private IUsuarioService usuarioService;
    @Autowired private IAdminService adminService;
    @Autowired private AlmacenamientoService almacenamientoService;
    @Autowired private SolicitudTimeoutService timeoutService;

    @PostMapping(value = "/responder/{idSolicitud}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> responderSolicitud(@PathVariable ObjectId idSolicitud,
                                                @RequestParam String comentario,
                                                @RequestParam(required = false) MultipartFile archivo,
                                                @RequestParam String usuario,
                                                @RequestParam String contrasena) {
        AdminModel admin = adminService.buscarPorUsuario(usuario);
        if (admin == null || !admin.getContrasena().equals(contrasena)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Solo administradores pueden responder solicitudes.");
        }

        SolicitudesModel solicitud = solicitudService.buscarSolicitudPorId(idSolicitud);
        if (solicitud == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Solicitud no encontrada.");
        }
        RespuestaModel existente = respuestaService.buscarPrimerRespuestaAdminPorSolicitudId(idSolicitud);
        if (existente != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("La solicitud ya ha recibido una respuesta inicial y no se puede responder de nuevo.");
        }

        try {
            RespuestaModel respuesta = new RespuestaModel();
            respuesta.setSolicitudId(solicitud.getId());
            respuesta.setComentario(comentario);
            respuesta.setFechaRespuesta(new Date());
            respuesta.setAutor(new RespuestaModel.Autor("admin", admin.getId()));
            respuesta.setRespuestaPadre(null); 

            if (archivo != null && !archivo.isEmpty()) {
                String rutaArchivo = almacenamientoService.almacenarArchivo(archivo);
                respuesta.setRutaArchivoPDF(rutaArchivo);
            }
            
            RespuestaModel guardada = respuestaService.guardarRespuesta(respuesta);

            solicitud.setRespuestaEnviada(true);
            solicitud.setEstado(EstadoSolicitud.Resuelta);
            solicitud.setFechaActualizacion(LocalDateTime.now());

            SolicitudesModel.RespuestaResumen resumen = new SolicitudesModel.RespuestaResumen();
            resumen.setRespuestaId(guardada.getId());
            resumen.setComentario(guardada.getComentario());
            resumen.setFechaRespuesta(guardada.getFechaRespuesta());

            if (solicitud.getRespuestas() == null) {
                solicitud.setRespuestas(new ArrayList<>());
            }
            solicitud.getRespuestas().add(resumen);

            solicitudService.guardarSolicitudCompleta(solicitud);
            timeoutService.cerrarSolicitudConRetraso(solicitud.getId());
            
            return new ResponseEntity<>(guardada, HttpStatus.CREATED);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al guardar el archivo PDF: " + e.getMessage());
        }
    }

    @GetMapping("/por-solicitud/{idSolicitud}")
    public ResponseEntity<?> obtenerRespuestas(@PathVariable ObjectId idSolicitud) {
        SolicitudesModel solicitud = solicitudService.buscarSolicitudPorId(idSolicitud);
        if (solicitud == null) return ResponseEntity.notFound().build();
        return new ResponseEntity<>(respuestaService.listarRespuestasPorSolicitud(solicitud.getId()), HttpStatus.OK);
    }

    @PutMapping("/calificar/{idRespuesta}")
    public ResponseEntity<?> calificarRespuesta(@PathVariable ObjectId idRespuesta,
                                                @RequestParam int puntuacion,
                                                @RequestParam String usuario,
                                                @RequestParam String contrasena) {
        UsuarioModel user = usuarioService.buscarPorUsuario(usuario);
        if (user == null || !contrasena.equals(user.getContrasena()) || "Anónimo".equals(user.getNombreCompleto())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Solo usuarios registrados pueden calificar.");
        }

        if (puntuacion < 1 || puntuacion > 5) {
            return ResponseEntity.badRequest().body("La puntuación debe estar entre 1 y 5.");
        }

        RespuestaModel respuesta = respuestaService.buscarRespuestaPorId(idRespuesta);
        if (respuesta == null) return ResponseEntity.notFound().build();
        
        SolicitudesModel solicitud = solicitudService.buscarSolicitudPorId(respuesta.getSolicitudId());
        if (solicitud == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Solicitud asociada a la respuesta no encontrada.");
        }

        if (respuesta.getPuntuacion() != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La respuesta ya ha sido calificada.");
        }
        
        if (!"admin".equals(respuesta.getAutor().getTipo()) || respuesta.getRespuestaPadre() != null) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Solo se puede calificar la respuesta inicial del administrador.");
        }

        respuesta.setPuntuacion(puntuacion);
        respuestaService.guardarRespuesta(respuesta); 

        solicitud.setCalificada(true);
        solicitud.setFechaActualizacion(LocalDateTime.now());
        solicitudService.guardarSolicitudCompleta(solicitud); 

        return new ResponseEntity<>("Respuesta calificada exitosamente.", HttpStatus.OK);
    }

 @PostMapping("/reabrir/{idSolicitud}")
    public ResponseEntity<?> reabrirSolicitud(@PathVariable ObjectId idSolicitud,
                                            @RequestParam String comentario,
                                            @RequestParam String usuario,
                                            @RequestParam String contrasena) {

        UsuarioModel user = usuarioService.buscarPorUsuario(usuario);
        if (user == null || !contrasena.equals(user.getContrasena()) || "Anónimo".equals(user.getNombreCompleto())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Solo usuarios registrados pueden reabrir solicitudes.");
        }

        SolicitudesModel solicitud = solicitudService.buscarSolicitudPorId(idSolicitud);
        if (solicitud == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Solicitud no encontrada.");

        RespuestaModel respuestaPadre = respuestaService
            .buscarPrimerRespuestaAdminPorSolicitudId(idSolicitud);

        if (respuestaPadre == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró una respuesta inicial del administrador.");
        }

        List<RespuestaModel> replicasUsuario = respuestaService
            .listarRespuestasPorSolicitud(idSolicitud)
            .stream()
            .filter(r -> "usuario".equals(r.getAutor().getTipo()) && r.getRespuestaPadre() != null)
            .toList();

        if (!replicasUsuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La solicitud ya fue reabierta anteriormente.");
        }

        RespuestaModel replica = new RespuestaModel();
        replica.setSolicitudId(solicitud.getId());
        replica.setComentario(comentario);
        replica.setFechaRespuesta(new Date());
        replica.setRespuestaPadre(respuestaPadre.getId());
        replica.setAutor(new RespuestaModel.Autor("usuario", user.getId()));

        RespuestaModel guardada = respuestaService.guardarRespuesta(replica);

        solicitudService.cambiarEstadoSolicitud(idSolicitud, EstadoSolicitud.Reabierta, true);

        return new ResponseEntity<>(guardada, HttpStatus.CREATED);
    }

    @PostMapping("/responder-replica/{idSolicitud}")
    public ResponseEntity<?> responderAReplica(@PathVariable ObjectId idSolicitud,
                                            @RequestParam String comentario,
                                            @RequestParam String usuario,
                                            @RequestParam String contrasena) {

        AdminModel admin = adminService.buscarPorUsuario(usuario);
        if (admin == null || !contrasena.equals(admin.getContrasena())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas.");
        }

        SolicitudesModel solicitud = solicitudService.buscarSolicitudPorId(idSolicitud);
        if (solicitud == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Solicitud no encontrada.");

        RespuestaModel respuestaPadre = respuestaService.buscarUltimaReplicaUsuarioPorSolicitudId(idSolicitud);
        if (respuestaPadre == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró una réplica del usuario para responder.");
        }

        if (!"usuario".equals(respuestaPadre.getAutor().getTipo()) || respuestaPadre.getRespuestaPadre() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La respuesta objetivo no es una réplica válida.");
        }

        boolean yaRespondida = respuestaService.listarRespuestasPorSolicitud(idSolicitud).stream()
            .anyMatch(r -> "admin".equals(r.getAutor().getTipo()) && r.getRespuestaPadre() != null &&
                        r.getRespuestaPadre().equals(respuestaPadre.getId()));

        if (yaRespondida) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La réplica ya fue respondida por un administrador.");
        }

        RespuestaModel respuesta = new RespuestaModel();
        respuesta.setSolicitudId(solicitud.getId());
        respuesta.setComentario(comentario);
        respuesta.setFechaRespuesta(new Date());
        respuesta.setAutor(new RespuestaModel.Autor("admin", admin.getId()));
        respuesta.setRespuestaPadre(respuestaPadre.getId());

        RespuestaModel guardada = respuestaService.guardarRespuesta(respuesta);

        solicitud.setReplicaRespondida(true);
        solicitud.setFechaActualizacion(LocalDateTime.now());

        SolicitudesModel.RespuestaResumen resumen = new SolicitudesModel.RespuestaResumen();
        resumen.setRespuestaId(guardada.getId());
        resumen.setComentario(guardada.getComentario());
        resumen.setFechaRespuesta(guardada.getFechaRespuesta());

        if (solicitud.getRespuestas() == null) {
            solicitud.setRespuestas(new ArrayList<>());
        }
        solicitud.getRespuestas().add(resumen);

        solicitudService.guardarSolicitudCompleta(solicitud);

        return new ResponseEntity<>(guardada, HttpStatus.CREATED);
    }

    @PutMapping("/cerrar-definitivo/{idSolicitud}")
    public ResponseEntity<?> cerrarSolicitud(@PathVariable ObjectId idSolicitud,
                                            @RequestParam String usuario,
                                            @RequestParam String contrasena) {

        AdminModel admin = adminService.buscarPorUsuario(usuario);
        if (admin == null || !contrasena.equals(admin.getContrasena())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas.");
        }

        SolicitudesModel solicitud = solicitudService.buscarSolicitudPorId(idSolicitud);
        if (solicitud == null) return ResponseEntity.notFound().build();

        if (solicitud.getEstado() == EstadoSolicitud.Cerrada) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La solicitud ya está cerrada.");
        }

        solicitud.setEstado(EstadoSolicitud.Cerrada);
        solicitud.setFechaActualizacion(LocalDateTime.now());

        return new ResponseEntity<>(solicitudService.guardarSolicitud(solicitud), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerRespuestaPorId(@PathVariable ObjectId id) {
        RespuestaModel respuesta = respuestaService.buscarRespuestaPorId(id);
        if (respuesta == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(respuesta);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarRespuesta(@PathVariable ObjectId id,
                                                 @RequestBody RespuestaModel nuevaRespuesta,
                                                 @RequestParam String usuario,
                                                 @RequestParam String contrasena) {
        UsuarioModel usuarioAutenticado = usuarioService.buscarPorUsuario(usuario);
        if (usuarioAutenticado == null || !contrasena.equals(usuarioAutenticado.getContrasena())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas.");
        }


        AdminModel admin = adminService.buscarPorUsuario(usuario); 
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Solo el administrador puede actualizar respuestas.");
        }

        RespuestaModel existente = respuestaService.buscarRespuestaPorId(id);
        if (existente == null) return ResponseEntity.notFound().build();

        existente.setComentario(nuevaRespuesta.getComentario());

        if (existente.getAutor() == null || !existente.getAutor().getAutorId().equals(admin.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes modificar una respuesta de otro administrador.");
        }

        SolicitudesModel solicitud = solicitudService.buscarSolicitudPorId(existente.getSolicitudId());
        if (solicitud == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Solicitud asociada a la respuesta no encontrada.");
        }

        if (solicitud.isCalificada()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede actualizar una respuesta de una solicitud que ya ha sido calificada.");
        }
        
        List<RespuestaModel> posiblesReplicas = respuestaService.listarRespuestasPorSolicitud(existente.getSolicitudId());
        boolean tieneReplica = posiblesReplicas.stream().anyMatch(r ->
            r.getRespuestaPadre() != null && r.getRespuestaPadre().equals(existente.getId())
        );
        if (tieneReplica) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede actualizar esta respuesta porque ya fue replicada por un usuario.");
        }
        


        return new ResponseEntity<>(respuestaService.guardarRespuesta(existente), HttpStatus.OK);
    }
}