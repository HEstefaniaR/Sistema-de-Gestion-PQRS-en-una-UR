package com.apirest.backend.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.apirest.backend.DTO.CambiarEstadoSolicitud;
import com.apirest.backend.Model.EstadoSolicitud;
import com.apirest.backend.Model.SolicitudesModel;
import com.apirest.backend.Repository.ISolicitudesRepository;
import com.apirest.backend.Service.ISolicitudesService;

@RestController
@RequestMapping("UR/solicitudes")
public class SolicitudController {

    @Autowired
    private ISolicitudesService solicitudesService;

    @Autowired
    private ISolicitudesRepository solicitudesRepository;

    // Crear nueva solicitud
    @PostMapping("/insertar")
    public ResponseEntity<SolicitudesModel> guardarSolicitud(@RequestBody SolicitudesModel solicitud) {
        solicitud.setFechaHoraCreacion(LocalDateTime.now());
        return new ResponseEntity<>(solicitudesService.guardarSolicitud(solicitud), HttpStatus.CREATED);
    }

    // Listar todas las solicitudes
    @GetMapping("/listar")
    public ResponseEntity<List<SolicitudesModel>> listarSolicitudes() {
        return new ResponseEntity<>(solicitudesService.listarSolicitudes(), HttpStatus.OK);
    }

    // Buscar solicitud por ID
    @GetMapping("/buscarporid/{id}")
    public ResponseEntity<SolicitudesModel> buscarSolicitudPorId(@PathVariable Integer id) {
        SolicitudesModel solicitud = solicitudesService.buscarSolicitudPorId(id);
        if (solicitud == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(solicitud, HttpStatus.OK);
    }

    // Actualizar solicitud (solo ciertos campos si estado es 'radicada')
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarSolicitud(@PathVariable Integer id, @RequestBody SolicitudesModel nuevaSolicitud) {
        Optional<SolicitudesModel> optionalSolicitud = solicitudesRepository.findById(id);
        if (optionalSolicitud.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SolicitudesModel solicitudExistente = optionalSolicitud.get();

        if (solicitudExistente.getEstado() != EstadoSolicitud.radicada) {
            return ResponseEntity.badRequest().body("Solo se pueden editar solicitudes en estado 'radicada'");
        }

        solicitudExistente.setCategoria(nuevaSolicitud.getCategoria());
        solicitudExistente.setDescripcion(nuevaSolicitud.getDescripcion());
        solicitudExistente.setTipo(nuevaSolicitud.getTipo());
        solicitudExistente.setFechaActualizacion(LocalDateTime.now());

        solicitudesRepository.save(solicitudExistente);
        return new ResponseEntity<>(solicitudExistente, HttpStatus.OK);
    }

    // Eliminar solicitud (solo si está en estado 'radicada')
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarSolicitudPorId(@PathVariable Integer id) {
        SolicitudesModel solicitud = solicitudesService.buscarSolicitudPorId(id);
        if (solicitud == null) {
            return ResponseEntity.notFound().build();
        }

        if (solicitud.getEstado() != EstadoSolicitud.radicada) {
            return ResponseEntity.badRequest().body("Solo se pueden eliminar solicitudes en estado 'radicada'");
        }

        solicitudesRepository.eliminarSolicitudPorId(id);
        return ResponseEntity.noContent().build();
    }

    // Cambiar estado de la solicitud
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstadoSolicitud(
            @PathVariable Integer id,
            @RequestBody CambiarEstadoSolicitud estadoDto) {

        Optional<SolicitudesModel> optionalSolicitud = solicitudesRepository.findById(id);
        if (optionalSolicitud.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SolicitudesModel solicitud = optionalSolicitud.get();

        if (solicitud.getEstado() == EstadoSolicitud.cerrada) {
            return ResponseEntity.badRequest().body("No se puede modificar una solicitud cerrada.");
        }

        solicitud.setEstado(estadoDto.getEstado());
        solicitud.setFechaActualizacion(LocalDateTime.now());

        solicitudesRepository.save(solicitud);

        return ResponseEntity.ok("Estado actualizado correctamente.");
    }
}