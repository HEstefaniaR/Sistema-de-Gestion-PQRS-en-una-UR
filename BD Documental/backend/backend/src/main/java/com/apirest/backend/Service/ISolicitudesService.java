package com.apirest.backend.Service;

import java.util.List;

import com.apirest.backend.DTO.SolicitudesDTO;
import com.apirest.backend.Model.SolicitudesModel;

public interface ISolicitudesService {
    SolicitudesModel guardarSolicitud(SolicitudesModel solicitud);
    List<SolicitudesModel> listarSolicitudes();
    SolicitudesModel buscarSolicitudPorId(Integer id);
    SolicitudesModel actualizarSolicitud(Integer id, SolicitudesModel solicitud);
    List<SolicitudesDTO> listarPorUsuarioId(Integer usuarioId);
}