package com.apirest.backend.DTO;

import com.apirest.backend.Model.EstadoSolicitud;

public class CambiarEstadoSolicitud {
    private EstadoSolicitud estado;

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }
}