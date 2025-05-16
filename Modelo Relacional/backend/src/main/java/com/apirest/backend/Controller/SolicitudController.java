package com.apirest.backend.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.apirest.backend.DTO.CambiarEstadoSolicitud;
import com.apirest.backend.Model.AdminModel;
import com.apirest.backend.Model.EstadoSolicitud;
import com.apirest.backend.Model.EvidenciaModel;
import com.apirest.backend.Model.RolUsuario;
import com.apirest.backend.Model.SolicitudModel;
import com.apirest.backend.Model.UsuarioModel;
import com.apirest.backend.Repository.ISolicitudRepository;
import com.apirest.backend.Service.ISolicitudService;
import com.apirest.backend.Service.IUsuarioService;
import com.apirest.backend.Service.IAdminService;
import com.apirest.backend.Service.IEvidenciaService;

@RestController
@RequestMapping ("UR/solicitudes")

public class SolicitudController {
    @Autowired ISolicitudService solicitudService;
    @Autowired IEvidenciaService evidenciaService;
    @Autowired private IUsuarioService usuarioService;
    @Autowired private ISolicitudRepository solicitudRepository;
    @Autowired private IAdminService adminService;

    @PostMapping("/insertar")
    public ResponseEntity<?> guardarSolicitud(
            @RequestBody SolicitudModel solicitud,
            @RequestParam String usuario,
            @RequestParam(required = false) String contrasena) {

        UsuarioModel user = usuarioService.buscarPorUsuario(usuario);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no válido");
        }

        if (user.getRol() != RolUsuario.anonimo) {
            // Si no es anónimo, se debe validar la contraseña
            if (contrasena == null || !contrasena.equals(user.getContrasena())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
            }
        }

        solicitud.setUsuario(user);
        return new ResponseEntity<>(solicitudService.guardarSolicitud(solicitud), HttpStatus.CREATED);
    }

    @GetMapping ("/listar")
    public ResponseEntity<List<SolicitudModel>> listarSolicitudes() {
        return new ResponseEntity<List<SolicitudModel>>(solicitudService.listarSolicitudes(),HttpStatus.OK);
    }

    @GetMapping ("/buscarporid/{id}")
    public ResponseEntity<SolicitudModel> buscarSolicitudPorId(@PathVariable Integer id) {
        return new ResponseEntity<SolicitudModel>(solicitudService.buscarSolicitudPorId(id),HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizarSolicitud(
            @PathVariable Integer id,
            @RequestBody SolicitudModel nuevaSolicitud,
            @RequestParam String usuario,
            @RequestParam(required = false) String contrasena) {

        UsuarioModel user = usuarioService.buscarPorUsuario(usuario);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no válido");
        }
        if (user.getRol() != RolUsuario.anonimo &&
            (contrasena == null || !contrasena.equals(user.getContrasena()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }

        Optional<SolicitudModel> optionalSolicitud = solicitudRepository.findById(id);
        if (optionalSolicitud.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SolicitudModel solicitudExistente = optionalSolicitud.get();

        if (!solicitudExistente.getUsuario().getIdUsuario().equals(user.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puede editar solicitudes de otros usuarios");
        }

        if (solicitudExistente.getEstado() != EstadoSolicitud.radicada) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Solo se pueden editar solicitudes en estado 'radicada'");
        }


        if (nuevaSolicitud.getEstado() != null &&
            !nuevaSolicitud.getEstado().equals(solicitudExistente.getEstado())) {
            return ResponseEntity.badRequest().body("No tiene autorización para cambiar el estado");
        }

        solicitudExistente.setCategoria(nuevaSolicitud.getCategoria());
        solicitudExistente.setDescripcion(nuevaSolicitud.getDescripcion());
        solicitudExistente.setTipo(nuevaSolicitud.getTipo());
        solicitudExistente.setFechaActualizacion(LocalDateTime.now());

        solicitudRepository.save(solicitudExistente);
        return new ResponseEntity<>(solicitudExistente, HttpStatus.OK);
    }
    
    @DeleteMapping("eliminar/{id}")
    public ResponseEntity<?> eliminarSolicitudPorId(
            @PathVariable Integer id,
            @RequestParam String usuario,
            @RequestParam(required = false) String contrasena) {

        UsuarioModel user = usuarioService.buscarPorUsuario(usuario);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no válido");
        }
        if (user.getRol() != RolUsuario.anonimo &&
            (contrasena == null || !contrasena.equals(user.getContrasena()))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }

        SolicitudModel solicitud = solicitudService.buscarSolicitudPorId(id);
        if (solicitud == null) {
            return ResponseEntity.notFound().build();
        }

        if (!solicitud.getUsuario().getIdUsuario().equals(user.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puede eliminar solicitudes de otros usuarios");
        }

        if (solicitud.getEstado() != EstadoSolicitud.radicada) {
            return ResponseEntity.badRequest().body("Solo se pueden eliminar solicitudes en estado 'radicada'");
        }

        solicitudService.eliminarSolicitudPorId(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "{id}/evidencias", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> guardarEvidencia(
        @PathVariable Integer id,
        @RequestParam("archivo") MultipartFile archivo,
        @RequestParam("usuario") String usuario,
        @RequestParam(value = "contrasena", required = false) String contrasena,
        @RequestParam(value = "descripcion", required = false) String descripcion
    ) {
        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().body("Archivo no puede estar vacío");
        }

        UsuarioModel user = usuarioService.buscarPorUsuario(usuario);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no válido");
        }

        if (user.getRol() != RolUsuario.anonimo) {
            // Si no es anónimo, validar contraseña
            if (contrasena == null || !contrasena.equals(user.getContrasena())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
            }
        }

        SolicitudModel solicitud = solicitudService.buscarSolicitudPorId(id);
        if (solicitud == null) {
            return ResponseEntity.badRequest().body("Solicitud no válida");
        }

        if (!solicitud.getUsuario().getIdUsuario().equals(user.getIdUsuario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("La solicitud no pertenece al usuario");
        }

        try {
            EvidenciaModel evidencia = new EvidenciaModel();
            evidencia.setDescripcion(descripcion);
            evidencia.setSolicitud(solicitud);
            evidencia.setFecha_hora_carga(LocalDateTime.now());

            EvidenciaModel evidenciaGuardada = evidenciaService.guardarEvidencia(evidencia, archivo);

            return ResponseEntity.status(HttpStatus.CREATED).body(evidenciaGuardada);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error al procesar archivo: " + e.getMessage());
        }
    }

    @GetMapping (value = "{id}/evidencias")
    public ResponseEntity<?> obtenerEvidenciasSolicitud(@PathVariable Integer id) {
        SolicitudModel solicitud = solicitudService.buscarSolicitudPorId(id);
        if (solicitud == null) {
            return ResponseEntity.badRequest().body("Solicitud no válidos");
        }

        return new ResponseEntity<List<EvidenciaModel>>(evidenciaService.obtenerEvidenciasPorSolicitud(solicitud), HttpStatus.OK);
    }
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstadoSolicitud(
            @PathVariable Integer id,
            @RequestBody CambiarEstadoSolicitud estadoDto,
            @RequestParam String usuario,
            @RequestParam String contrasena) {

        AdminModel admin = adminService.buscarPorUsuario(usuario);
        if (admin == null || !admin.getContrasena().equals(contrasena)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Solo administradores pueden cambiar el estado");
        }

        Optional<SolicitudModel> optionalSolicitud = solicitudRepository.findById(id);
        if (optionalSolicitud.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        SolicitudModel solicitud = optionalSolicitud.get();

        if (solicitud.getEstado() == EstadoSolicitud.cerrada) {
            return ResponseEntity.badRequest().body("No se puede modificar una solicitud cerrada.");
        }

        solicitud.setEstado(estadoDto.getEstado());
        solicitud.setFechaActualizacion(LocalDateTime.now());

        solicitudRepository.save(solicitud);

        return ResponseEntity.ok("Estado actualizado correctamente.");
    }

}