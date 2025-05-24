package com.apirest.backend.Service;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.apirest.backend.Model.EstadoSolicitud;
import com.apirest.backend.Model.SolicitudesModel;

@Service
public class SolicitudTimeoutService {

    @Autowired
    private ISolicitudesService solicitudService;

    @Async
    public void cerrarSolicitudConRetraso(ObjectId solicitudId) {
        try {
            Thread.sleep(30_000);
            SolicitudesModel solicitud = solicitudService.buscarSolicitudPorId(solicitudId);
            if (solicitud != null && solicitud.getEstado() == EstadoSolicitud.Resuelta) {
                solicitud.setEstado(EstadoSolicitud.Cerrada);
                solicitud.setFechaActualizacion(LocalDateTime.now());
                solicitudService.guardarSolicitud(solicitud);
                System.out.println("Solicitud cerrada automáticamente después de 30 segundos.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Proceso de cierre interrumpido: " + e.getMessage());
        }
    }
}
