package com.apirest.backend.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

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
    @Autowired private ISolicitudService solicitudService;
    @Autowired private IUsuarioService usuarioService;
    @Autowired private IAdminService adminService;
    @Autowired private AlmacenamientoService almacenamientoService;

    @PostMapping(value = "/responder/{idSolicitud}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> responderSolicitud(@PathVariable Integer idSolicitud,
                                                @RequestParam String comentario,
                                                @RequestParam(required = false) MultipartFile archivo,
                                                @RequestParam String usuario,
                                                @RequestParam String contrasena) {
        AdminModel admin = adminService.buscarPorUsuario(usuario);
        if (admin == null || !admin.getContrasena().equals(contrasena)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Solo administradores pueden cambiar el estado");
        }
    
        SolicitudModel solicitud = solicitudService.buscarSolicitudPorId(idSolicitud);
        if (solicitud == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Solicitud no encontrada");
        }
    
        try {
            RespuestaModel respuesta = new RespuestaModel();
            respuesta.setSolicitud(solicitud);
            respuesta.setComentario(comentario);
            respuesta.setFechaRespuesta(LocalDateTime.now());
            respuesta.setAdmin(admin);
    
            if (archivo != null && !archivo.isEmpty()) {
                String rutaArchivo = almacenamientoService.guardarArchivo(archivo);
                respuesta.setRutaOficioPdf(rutaArchivo);
            }
    
            solicitud.setEstado(EstadoSolicitud.resuelta);
            solicitud.setFechaActualizacion(LocalDateTime.now());
            solicitudService.actualizarSolicitudPorId(idSolicitud, solicitud);
    
            return new ResponseEntity<>(respuestaService.guardarRespuesta(respuesta), HttpStatus.CREATED);
    
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al guardar el archivo PDF: " + e.getMessage());
        }
    }

    @GetMapping("/por-solicitud/{idSolicitud}")
    public ResponseEntity<?> obtenerRespuestas(@PathVariable Integer idSolicitud) {
        SolicitudModel solicitud = solicitudService.buscarSolicitudPorId(idSolicitud);
        if (solicitud == null) return ResponseEntity.notFound().build();
        return new ResponseEntity<>(respuestaService.listarRespuestasPorSolicitud(solicitud), HttpStatus.OK);
    }

    @PutMapping("/calificar/{idRespuesta}")
    public ResponseEntity<?> calificarRespuesta(@PathVariable Integer idRespuesta,
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
    public ResponseEntity<?> reabrirSolicitud(@PathVariable Integer idSolicitud,
                                              @RequestParam String comentario,
                                              @RequestParam String usuario,
                                              @RequestParam String contrasena,
                                              @RequestParam Integer idRespuestaPadre) {
        UsuarioModel user = usuarioService.buscarPorUsuario(usuario);
        if (user == null || !contrasena.equals(user.getContrasena()) || "Anónimo".equals(user.getNombreCompleto())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Solo usuarios registrados pueden reabrir.");
        }

        SolicitudModel solicitud = solicitudService.buscarSolicitudPorId(idSolicitud);
        RespuestaModel respuestaPadre = respuestaService.buscarRespuestaPorId(idRespuestaPadre);
        if (solicitud == null || respuestaPadre == null) return ResponseEntity.notFound().build();

        RespuestaModel replica = new RespuestaModel();
        replica.setSolicitud(solicitud);
        replica.setComentario(comentario);
        replica.setFechaRespuesta(LocalDateTime.now());
        replica.setRespuestaPadre(respuestaPadre);
        replica.setUsuario(user);

        solicitud.setEstado(EstadoSolicitud.reabierta);
        solicitud.setFechaActualizacion(LocalDateTime.now());
        solicitudService.actualizarSolicitudPorId(idSolicitud, solicitud);

        return new ResponseEntity<>(respuestaService.guardarRespuesta(replica), HttpStatus.CREATED);
    }

    @PostMapping("/responder-replica/{idSolicitud}")
    public ResponseEntity<?> responderAReplica(@PathVariable Integer idSolicitud,
                                            @RequestParam Integer idRespuestaPadre,
                                            @RequestParam String comentario,
                                            @RequestParam String usuario,
                                            @RequestParam String contrasena) {

        AdminModel admin = adminService.buscarPorUsuario(usuario);
        if (admin == null || !contrasena.equals(admin.getContrasena())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas.");
        }

        SolicitudModel solicitud = solicitudService.buscarSolicitudPorId(idSolicitud);
        RespuestaModel respuestaPadre = respuestaService.buscarRespuestaPorId(idRespuestaPadre);

        if (solicitud == null || respuestaPadre == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Solicitud o respuesta padre no encontrada.");
        }

        RespuestaModel respuesta = new RespuestaModel();
        respuesta.setSolicitud(solicitud);
        respuesta.setComentario(comentario);
        respuesta.setFechaRespuesta(LocalDateTime.now());
        respuesta.setAdmin(admin);
        respuesta.setRespuestaPadre(respuestaPadre);

        solicitud.setEstado(EstadoSolicitud.resuelta);
        solicitud.setFechaActualizacion(LocalDateTime.now());
        solicitudService.actualizarSolicitudPorId(idSolicitud, solicitud);

        return new ResponseEntity<>(respuestaService.guardarRespuesta(respuesta), HttpStatus.CREATED);
    }

    @PutMapping("/cerrar-definitivo/{idSolicitud}")
    public ResponseEntity<?> cerrarSolicitud(@PathVariable Integer idSolicitud,
                                             @RequestParam String usuario,
                                             @RequestParam String contrasena) {

        AdminModel admin = adminService.buscarPorUsuario(usuario);
        if (admin == null || !contrasena.equals(admin.getContrasena())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas.");
        }


        SolicitudModel solicitud = solicitudService.buscarSolicitudPorId(idSolicitud);
        if (solicitud == null) return ResponseEntity.notFound().build();

        solicitud.setEstado(EstadoSolicitud.cerrada);
        solicitud.setFechaActualizacion(LocalDateTime.now());

        return new ResponseEntity<>(solicitudService.actualizarSolicitudPorId(idSolicitud, solicitud), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public RespuestaModel obtenerRespuestaPorId(@PathVariable Integer id) {
        return respuestaService.buscarRespuestaPorId(id);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarRespuesta(@PathVariable Integer id,
                                                 @RequestBody RespuestaModel nuevaRespuesta,
                                                 @RequestParam String usuario,
                                                 @RequestParam String contrasena) {
        UsuarioModel usuarioAutenticado = usuarioService.buscarPorUsuario(usuario);
        if (usuarioAutenticado == null || !contrasena.equals(usuarioAutenticado.getContrasena())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas.");
        }

        AdminModel admin = adminService.buscarAdminPorId(usuarioAutenticado.getIdUsuario());
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Solo el administrador puede actualizar.");
        }

        RespuestaModel existente = respuestaService.buscarRespuestaPorId(id);
        if (existente == null) return ResponseEntity.notFound().build();

        if (existente.getAdmin() == null || !existente.getAdmin().getIdadmin().equals(admin.getIdadmin())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes modificar una respuesta de otro administrador.");
        }

        List<RespuestaModel> posiblesReplicas = respuestaService.listarRespuestasPorSolicitud(existente.getSolicitud());
        boolean tieneReplica = posiblesReplicas.stream().anyMatch(r ->
            r.getRespuestaPadre() != null && r.getRespuestaPadre().getIdRespuesta().equals(existente.getIdRespuesta())
        );
        if (tieneReplica) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("No se puede actualizar una respuesta que ya fue replicada por un usuario.");
        }

        existente.setComentario(nuevaRespuesta.getComentario());
        existente.setRutaOficioPdf(nuevaRespuesta.getRutaOficioPdf());
        return new ResponseEntity<>(respuestaService.guardarRespuesta(existente), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarRespuesta(@PathVariable Integer id) {
        try {
            respuestaService.eliminarRespuestaPorId(id);
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