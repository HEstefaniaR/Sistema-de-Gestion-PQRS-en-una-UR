package com.apirest.backend.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.apirest.backend.DTO.CambiarEstadoSolicitud;
import com.apirest.backend.Model.AdminModel;
import com.apirest.backend.Model.EstadoSolicitud;
import com.apirest.backend.Model.RolUsuario;
import com.apirest.backend.Model.SolicitudesModel;
import com.apirest.backend.Model.TipoArchivo;
import com.apirest.backend.Model.UsuarioModel;
import com.apirest.backend.Service.AlmacenamientoService;
import com.apirest.backend.Service.IAdminService;
import com.apirest.backend.Service.ISolicitudesService;
import com.apirest.backend.Service.IUsuarioService;

@RestController
@RequestMapping("UR/solicitudes")
public class SolicitudController {

    @Autowired private ISolicitudesService solicitudesService;
    @Autowired private IUsuarioService usuarioService;
    @Autowired private IAdminService adminService;
    @Autowired private AlmacenamientoService almacenamientoService;

    @PostMapping("/insertar")
    public ResponseEntity<?> guardarSolicitud(
            @RequestBody SolicitudesModel solicitud,
            @RequestParam String usuario,
            @RequestParam(required = false) String contrasena) {

        UsuarioModel user = usuarioService.buscarPorUsuario(usuario);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no válido");
        }

        if (user.getRol() != RolUsuario.anonimo && (contrasena == null || !contrasena.equals(user.getContrasena()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }

        solicitud.setUsuarioId(user.getId());
        solicitud.setFechaHoraCreacion(LocalDateTime.now());
        return new ResponseEntity<>(solicitudesService.guardarSolicitud(solicitud), HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<SolicitudesModel>> listarSolicitudes() {
        return new ResponseEntity<>(solicitudesService.listarSolicitudes(), HttpStatus.OK);
    }

    @GetMapping("/buscarporid/{id}")
    public ResponseEntity<SolicitudesModel> buscarSolicitudPorId(@PathVariable ObjectId id) {
        SolicitudesModel solicitud = solicitudesService.buscarSolicitudPorId(id);
        if (solicitud == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(solicitud, HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarSolicitud(
            @PathVariable ObjectId id,
            @RequestBody SolicitudesModel nuevaSolicitud,
            @RequestParam String usuario,
            @RequestParam(required = false) String contrasena) {

        UsuarioModel user = usuarioService.buscarPorUsuario(usuario);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no válido");

        if (user.getRol() != RolUsuario.anonimo && (contrasena == null || !contrasena.equals(user.getContrasena())))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");

        SolicitudesModel solicitud = solicitudesService.buscarSolicitudPorId(id);
        if (solicitud == null) return ResponseEntity.notFound().build();

        if (!solicitud.getUsuarioId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puede editar solicitudes de otros usuarios");

        if (solicitud.getEstado() == EstadoSolicitud.Cerrada)
            return ResponseEntity.badRequest().body("No se puede editar una solicitud que está en estado 'Cerrada'");

        if (nuevaSolicitud.getEstado() != null && !nuevaSolicitud.getEstado().equals(solicitud.getEstado()))
            return ResponseEntity.badRequest().body("No tiene autorización para cambiar el estado");

        solicitud.setCategoria(nuevaSolicitud.getCategoria());
        solicitud.setDescripcion(nuevaSolicitud.getDescripcion());
        solicitud.setTipo(nuevaSolicitud.getTipo());
        solicitud.setFechaActualizacion(LocalDateTime.now());

        solicitudesService.guardarSolicitud(solicitud);
        return new ResponseEntity<>(solicitud, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/evidencias", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> guardarEvidencia(
            @PathVariable ObjectId id,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("usuario") String usuario,
            @RequestParam(value = "contrasena", required = false) String contrasena,
            @RequestParam(value = "descripcion", required = false) String descripcion) {

        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo no puede estar vacío");
        }

        UsuarioModel user = usuarioService.buscarPorUsuario(usuario);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no válido");

        if (user.getRol() != RolUsuario.anonimo &&
            (contrasena == null || !contrasena.equals(user.getContrasena())))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");

        SolicitudesModel solicitud = solicitudesService.buscarSolicitudPorId(id);
        if (solicitud == null) return ResponseEntity.badRequest().body("Solicitud no válida");

        if (!solicitud.getUsuarioId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La solicitud no pertenece al usuario");

        try {
            String nombreArchivo = almacenamientoService.almacenarArchivo(archivo);

            SolicitudesModel.EvidenciaEmbed evidencia = new SolicitudesModel.EvidenciaEmbed();
            evidencia.setEvidenciaId(new ObjectId());
            evidencia.setFechaHoraCarga(LocalDateTime.now());
            evidencia.setRutaArchivo(nombreArchivo);
            evidencia.setDescripcion(descripcion);
            evidencia.setTipoArchivo(detectarTipoArchivo(archivo));

            if (solicitud.getEvidencias() == null) {
                solicitud.setEvidencias(new java.util.ArrayList<>());
            }

            solicitud.getEvidencias().add(evidencia);
            solicitudesService.guardarSolicitud(solicitud);

            return ResponseEntity.status(HttpStatus.CREATED).body(evidencia);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al procesar archivo: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Tipo de archivo no soportado");
        }
    }
    private TipoArchivo detectarTipoArchivo(MultipartFile archivo) {
        String nombre = archivo.getOriginalFilename();
        if (nombre == null || !nombre.contains(".")) throw new IllegalArgumentException("Nombre de archivo inválido");

        String extension = nombre.substring(nombre.lastIndexOf('.') + 1).toLowerCase();

        switch (extension) {
            case "png":
            case "jpeg":
                return TipoArchivo.imagen;

            case "mp4":
                return TipoArchivo.video;

            case "pdf":
                return TipoArchivo.documento;

            default:
                throw new IllegalArgumentException("Tipo de archivo no soportado: " + extension);
        }
    }

    @GetMapping("/{id}/evidencias")
    public ResponseEntity<?> obtenerEvidenciasSolicitud(@PathVariable ObjectId id) {
        SolicitudesModel solicitud = solicitudesService.buscarSolicitudPorId(id);
        if (solicitud == null) {
            return ResponseEntity.badRequest().body("Solicitud no válida");
        }

        return new ResponseEntity<>(solicitud.getEvidencias(), HttpStatus.OK);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstadoSolicitud(
            @PathVariable ObjectId id,
            @RequestBody CambiarEstadoSolicitud estadoDto,
            @RequestParam String usuario,
            @RequestParam String contrasena) {

        AdminModel admin = adminService.buscarPorUsuario(usuario);
        if (admin == null || !admin.getContrasena().equals(contrasena)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Solo administradores pueden cambiar el estado");
        }

        SolicitudesModel solicitud = solicitudesService.buscarSolicitudPorId(id);
        if (solicitud == null) return ResponseEntity.notFound().build();

        if (solicitud.getEstado() == EstadoSolicitud.Cerrada) {
            return ResponseEntity.badRequest().body("No se puede modificar una solicitud cerrada.");
        }

        solicitud.setEstado(estadoDto.getEstado());
        solicitud.setFechaActualizacion(LocalDateTime.now());

        solicitudesService.guardarSolicitud(solicitud);
        return ResponseEntity.ok("Estado actualizado correctamente.");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarSolicitud(@PathVariable("id") String id) {
        try {
            ObjectId objectId = new ObjectId(id);
            boolean eliminada = solicitudesService.eliminarSolicitud(objectId);
            if (eliminada) {
                return ResponseEntity.ok("Solicitud eliminada correctamente.");
            } else {
                return ResponseEntity.badRequest().body("No se puede eliminar: la solicitud no está en estado 'RADICADA' o ya tiene respuestas.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("ID inválido.");
        }
    }
}