package com.apirest.backend.Service;

import java.io.IOException;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import com.apirest.backend.Model.EstadoSolicitud;
import com.apirest.backend.Model.SolicitudesModel;

public interface ISolicitudesService {
    SolicitudesModel guardarSolicitud(SolicitudesModel solicitud);
    List<SolicitudesModel> listarSolicitudes();
    SolicitudesModel buscarSolicitudPorId(ObjectId idSolicitud);
    SolicitudesModel buscarSolicitudConEvidencias(ObjectId id);
    SolicitudesModel actualizarSolicitud(ObjectId id, SolicitudesModel solicitudActualizada);
    SolicitudesModel agregarEvidenciaASolicitud(ObjectId idSolicitud, MultipartFile archivo, String descripcion) throws IOException;
    void eliminarEvidenciaDeSolicitud(ObjectId idSolicitud, String idEvidencia) throws IOException;
    List<SolicitudesModel.SolicitudResumen> listarResumenesPorUsuarioId(ObjectId usuarioId);
    SolicitudesModel guardarSolicitudCompleta(SolicitudesModel solicitud);
    void cambiarEstadoSolicitud(ObjectId idSolicitud, EstadoSolicitud nuevoEstado, boolean reabierta);
}