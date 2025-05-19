package com.apirest.backend.Service;

import java.util.List;

import com.apirest.backend.Model.SolicitudModel;

public interface ISolicitudService {
    public SolicitudModel guardarSolicitud(SolicitudModel solicitud);
    public List<SolicitudModel>listarSolicitudes();
    public SolicitudModel buscarSolicitudPorId(Integer id);
    public void eliminarSolicitudPorId(Integer id);
    public SolicitudModel actualizarSolicitudPorId(Integer id, SolicitudModel solicitud);
}
