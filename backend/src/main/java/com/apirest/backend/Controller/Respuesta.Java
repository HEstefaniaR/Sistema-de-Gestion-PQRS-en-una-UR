package com.apirest.backend.Controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.apirest.backend.Model.*;
import com.apirest.backend.Service.*;

@RestController
@RequestMapping("/respuestas")
public class RespuestaControlador {

    @Autowired
    private IRespuestaService respuestaService;

    @Autowired
    private ISolicitudService solicitudService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IAdminService adminService;

    // Admin responde una solicitud
    @PostMapping("/responder/{idSolicitud}")
    public RespuestaModel responderSolicitud(@PathVariable Integer idSolicitud,
                                             @RequestParam String comentario,
                                             @RequestParam(required = false) String rutaPdf,
                                             @RequestParam Integer idAdmin) {
        SolicitudModel solicitud = solicitudService.buscarSolicitudPorId(idSolicitud);
        AdminModel admin = adminService.buscarAdminPorId(idAdmin);

        if (solicitud == null || admin == null) return null;

        RespuestaModel respuesta = new RespuestaModel();
        respuesta.setSolicitud(solicitud);
        respuesta.setComentario(comentario);
        respuesta.setRutaOficioPdf(rutaPdf);
        respuesta.setFechaRespuesta(LocalDateTime.now());
        respuesta.setAdmin(admin);

        solicitud.setEstado(EstadoSolicitud.resuelta);
        solicitud.setFechaActualizacion(LocalDateTime.now());

        solicitudService.actualizarSolicitudPorId(idSolicitud, solicitud);

        return respuestaService.guardarRespuesta(respuesta);
    }

    // Usuario ve todas las respuestas de su solicitud
    @GetMapping("/por-solicitud/{idSolicitud}")
    public List<RespuestaModel> obtenerRespuestas(@PathVariable Integer idSolicitud) {
        SolicitudModel solicitud = solicitudService.buscarSolicitudPorId(idSolicitud);
        return respuestaService.listarRespuestasPorSolicitud(solicitud);
    }

    // Usuario califica una respuesta
    @PutMapping("/calificar/{idRespuesta}")
    public RespuestaModel calificarRespuesta(@PathVariable Integer idRespuesta,
                                             @RequestParam int puntuacion) {
        RespuestaModel respuesta = respuestaService.buscarRespuestaPorId(idRespuesta);
        if (respuesta == null) return null;

        respuesta.setPuntuacion(puntuacion);
        return respuestaService.guardarRespuesta(respuesta);
    }

    // Usuario reabre una solicitud con un comentario
    @PostMapping("/reabrir/{idSolicitud}")
    public RespuestaModel reabrirSolicitud(@PathVariable Integer idSolicitud,
                                           @RequestParam String comentario,
                                           @RequestParam Integer idUsuario,
                                           @RequestParam Integer idRespuestaPadre) {
        SolicitudModel solicitud = solicitudService.buscarSolicitudPorId(idSolicitud);
        UsuarioModel usuario = usuarioService.buscarUsuarioPorId(idUsuario);
        RespuestaModel respuestaPadre = respuestaService.buscarRespuestaPorId(idRespuestaPadre);

        if (solicitud == null || usuario == null || respuestaPadre == null) return null;

        RespuestaModel replica = new RespuestaModel();
        replica.setSolicitud(solicitud);
        replica.setComentario(usuario.getUsuario() + ": " + comentario);
        replica.setFechaRespuesta(LocalDateTime.now());
        replica.setRespuestaPadre(respuestaPadre);

        solicitud.setEstado(EstadoSolicitud.reabierta);
        solicitud.setFechaActualizacion(LocalDateTime.now());
        solicitudService.actualizarSolicitudPorId(idSolicitud, solicitud);

        return respuestaService.guardarRespuesta(replica);
    }

    // Admin cierra definitivamente una solicitud (después de réplica)
    @PutMapping("/cerrar-definitivo/{idSolicitud}")
    public SolicitudModel cerrarSolicitud(@PathVariable Integer idSolicitud) {
        SolicitudModel solicitud = solicitudService.buscarSolicitudPorId(idSolicitud);
        if (solicitud == null) return null;

        solicitud.setEstado(EstadoSolicitud.cerrada);
        solicitud.setFechaActualizacion(LocalDateTime.now());

        return solicitudService.actualizarSolicitudPorId(idSolicitud, solicitud);
    }

    // Buscar respuesta por ID
    @GetMapping("/{id}")
    public RespuestaModel obtenerRespuestaPorId(@PathVariable Integer id) {
        return respuestaService.buscarRespuestaPorId(id);
    }

    // Actualizar una respuesta existente
    @PutMapping("/actualizar/{id}")
    public RespuestaModel actualizarRespuesta(@PathVariable Integer id,
                                              @RequestBody RespuestaModel nuevaRespuesta) {
        RespuestaModel existente = respuestaService.buscarRespuestaPorId(id);
        if (existente == null) return null;

        existente.setComentario(nuevaRespuesta.getComentario());
        existente.setRutaOficioPdf(nuevaRespuesta.getRutaOficioPdf());
        existente.setPuntuacion(nuevaRespuesta.getPuntuacion());

        return respuestaService.guardarRespuesta(existente);
    }

    // Eliminar una respuesta
    @DeleteMapping("/{id}")
    public void eliminarRespuesta(@PathVariable Integer id) {
        respuestaService.eliminarRespuestaPorId(id);
    }
}
