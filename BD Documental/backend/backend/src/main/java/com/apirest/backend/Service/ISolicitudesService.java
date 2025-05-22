package com.apirest.backend.Service;

import java.io.IOException;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.web.multipart.MultipartFile;

import com.apirest.backend.DTO.SolicitudesDTO;
import com.apirest.backend.Model.SolicitudesModel;

public interface ISolicitudesService {
    SolicitudesModel guardarSolicitud(SolicitudesModel solicitud);
    List<SolicitudesModel> listarSolicitudes();
    SolicitudesModel buscarSolicitudPorId(ObjectId id);
    SolicitudesModel buscarSolicitudConEvidencias(ObjectId id);
    SolicitudesModel actualizarSolicitud(ObjectId id, SolicitudesModel solicitudActualizada);
    SolicitudesModel agregarEvidenciaASolicitud(ObjectId idSolicitud, MultipartFile archivo, String descripcion) throws IOException;
    void eliminarEvidenciaDeSolicitud(ObjectId idSolicitud, String idEvidencia) throws IOException;
    List<SolicitudesDTO> listarPorUsuarioId(ObjectId usuarioId);
}