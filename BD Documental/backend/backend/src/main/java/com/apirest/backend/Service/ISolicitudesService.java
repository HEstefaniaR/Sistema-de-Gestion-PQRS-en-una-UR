package com.apirest.backend.Service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.apirest.backend.DTO.SolicitudesDTO;
import com.apirest.backend.Model.SolicitudesModel;

public interface ISolicitudesService {
    SolicitudesModel guardarSolicitud(SolicitudesModel solicitud);
    List<SolicitudesModel> listarSolicitudes();
    SolicitudesModel buscarSolicitudPorId(Integer id);
    SolicitudesModel buscarSolicitudConEvidencias(Integer id);
    SolicitudesModel actualizarSolicitud(Integer id, SolicitudesModel solicitudActualizada);
    SolicitudesModel agregarEvidenciaASolicitud(Integer idSolicitud, MultipartFile archivo, String descripcion) throws IOException;
    void eliminarEvidenciaDeSolicitud(Integer idSolicitud, String idEvidencia) throws IOException;
    List<SolicitudesDTO> listarPorUsuarioId(Integer usuarioId);
}