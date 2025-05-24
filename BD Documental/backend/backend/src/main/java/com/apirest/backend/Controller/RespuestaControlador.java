package com.apirest.backend.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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

    @PostMapping(value = "/responder/{idSolicitud}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> responderSolicitud(@PathVariable ObjectId idSolicitud,
                                                @RequestParam String comentario,
                                                @RequestParam(required = false) MultipartFile archivo,
                                                @RequestParam String usuario,
                                                @RequestParam String contrasena) {
        AdminModel admin = adminService.buscarPorUsuario(usuario);
        if (admin == null || !admin.getContrasena().equals(contrasena)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Solo administradores pueden cambiar el estado");
        }

        SolicitudesModel solicitud = solicitudService.buscarSolicitudPorId(idSolicitud);
        if (solicitud == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Solicitud no encontrada");
        }

        try {
            RespuestaModel respuesta = new RespuestaModel();
            respuesta.setSolicitudId(solicitud.getId());
            respuesta.setComentario(comentario);
            respuesta.setFechaRespuesta(new Date());
            respuesta.setAutor(new RespuestaModel.Autor("admin", admin.getId()));

            if (archivo != null && !archivo.isEmpty()) {
                String rutaArchivo = almacenamientoService.almacenarArchivo(archivo);
                respuesta.setRutaArchivoPDF(rutaArchivo);
            }

            RespuestaModel guardada = respuestaService.guardarRespuesta(respuesta);

            SolicitudesModel.RespuestaResumen resumen = new SolicitudesModel.RespuestaResumen();
            resumen.setRespuestaId(guardada.getId());
            resumen.setComentario(guardada.getComentario());
            resumen.setFechaRespuesta(guardada.getFechaRespuesta());

            if (solicitud.getRespuestas() == null) {
                solicitud.setRespuestas(new ArrayList<>());
            }
            solicitud.getRespuestas().add(resumen);

            solicitud.setEstado(EstadoSolicitud.Resuelta);
            solicitud.setFechaActualizacion(LocalDateTime.now());
            solicitudService.actualizarSolicitud(idSolicitud, solicitud);

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

        respuesta.setPuntuacion(puntuacion);
        return new ResponseEntity<>(respuestaService.guardarRespuesta(respuesta), HttpStatus.OK);
    }

    @PostMapping("/reabrir/{idSolicitud}")
    public ResponseEntity<?> reabrirSolicitud(@PathVariable ObjectId idSolicitud,
                                              @RequestParam String comentario,
                                              @RequestParam String usuario,
                                              @RequestParam String contrasena,
                                              @RequestParam ObjectId idRespuestaPadre) {
        UsuarioModel user = usuarioService.buscarPorUsuario(usuario);
        if (user == null || !contrasena.equals(user.getContrasena()) || "Anónimo".equals(user.getNombreCompleto())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Solo usuarios registrados pueden reabrir.");
        }

        SolicitudesModel solicitud = solicitudService.buscarSolicitudPorId(idSolicitud);
        RespuestaModel respuestaPadre = respuestaService.buscarRespuestaPorId(idRespuestaPadre);
        if (solicitud == null || respuestaPadre == null) return ResponseEntity.notFound().build();

        RespuestaModel replica = new RespuestaModel();
        replica.setSolicitudId(solicitud.getId());
        replica.setComentario(comentario);
        replica.setFechaRespuesta(new Date());
        replica.setRespuestaPadre(new ObjectId());
        replica.setAutor(new RespuestaModel.Autor("usuario", user.getId()));

        solicitud.setEstado(EstadoSolicitud.Reabierta);
        solicitud.setFechaActualizacion(LocalDateTime.now());
        solicitudService.actualizarSolicitud(idSolicitud, solicitud);

        return new ResponseEntity<>(respuestaService.guardarRespuesta(replica), HttpStatus.CREATED);
    }

    @PostMapping("/responder-replica/{idSolicitud}")
    public ResponseEntity<?> responderAReplica(@PathVariable ObjectId idSolicitud,
                                               @RequestParam ObjectId idRespuestaPadre,
                                               @RequestParam String comentario,
                                               @RequestParam String usuario,
                                               @RequestParam String contrasena) {

        AdminModel admin = adminService.buscarPorUsuario(usuario);
        if (admin == null || !contrasena.equals(admin.getContrasena())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas.");
        }

        SolicitudesModel solicitud = solicitudService.buscarSolicitudPorId(idSolicitud);
        RespuestaModel respuestaPadre = respuestaService.buscarRespuestaPorId(idRespuestaPadre);

        if (solicitud == null || respuestaPadre == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Solicitud o respuesta padre no encontrada.");
        }

        RespuestaModel respuesta = new RespuestaModel();
        respuesta.setSolicitudId(solicitud.getId());
        respuesta.setComentario(comentario);
        respuesta.setFechaRespuesta(new Date());
        respuesta.setAutor(new RespuestaModel.Autor("admin", admin.getId()));
        respuesta.setRespuestaPadre(new ObjectId());

        solicitud.setEstado(EstadoSolicitud.Resuelta);
        solicitud.setFechaActualizacion(LocalDateTime.now());
        solicitudService.actualizarSolicitud(idSolicitud, solicitud);

        return new ResponseEntity<>(respuestaService.guardarRespuesta(respuesta), HttpStatus.CREATED);
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

        solicitud.setEstado(EstadoSolicitud.Cerrada);
        solicitud.setFechaActualizacion(LocalDateTime.now());

        return new ResponseEntity<>(solicitudService.actualizarSolicitud(idSolicitud, solicitud), HttpStatus.OK);
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

        ObjectId idAdmin = new ObjectId(usuarioAutenticado.getUsuario());
        AdminModel admin = adminService.buscarAdminPorId(idAdmin);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Solo el administrador puede actualizar.");
        }

        RespuestaModel existente = respuestaService.buscarRespuestaPorId(id);
        if (existente == null) return ResponseEntity.notFound().build();

        if (existente.getAutor() == null || !existente.getAutor().getAutorId().equals(admin.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes modificar una respuesta de otro administrador.");
        }

        List<RespuestaModel> posiblesReplicas = respuestaService.listarRespuestasPorSolicitud(existente.getSolicitudId());
        boolean tieneReplica = posiblesReplicas.stream().anyMatch(r ->
            r.getRespuestaPadre() != null && r.getRespuestaPadre().equals(existente.getId())
        );
        if (tieneReplica) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede actualizar una respuesta que ya fue replicada por un usuario.");
        }

        existente.setComentario(nuevaRespuesta.getComentario());
        existente.setRutaArchivoPDF(nuevaRespuesta.getRutaArchivoPDF());
        return new ResponseEntity<>(respuestaService.guardarRespuesta(existente), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRespuesta(@PathVariable ObjectId id) {
        try {
            respuestaService.eliminarRespuesta(id);
            return ResponseEntity.ok("Respuesta eliminada exitosamente.");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("No se puede eliminar la respuesta porque tiene réplicas asociadas.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ocurrió un error al intentar eliminar la respuesta.");
        }
    }
}