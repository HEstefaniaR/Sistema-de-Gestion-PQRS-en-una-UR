package com.apirest.backend.Controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.apirest.backend.Model.RespuestaModel;
import com.apirest.backend.Service.IRespuestaService;

@RestController
@RequestMapping("/UR/respuestas")
public class RespuestaController {

    @Autowired
    private IRespuestaService respuestaService;

    @PostMapping("/insertar")
    public ResponseEntity<String> crearRespuesta(@RequestBody RespuestaModel respuesta) {
        try {
            String mensaje = respuestaService.guardarRespuesta(respuesta);
            return new ResponseEntity<>(mensaje, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear la respuesta", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<RespuestaModel>> listarRespuestas() {
        List<RespuestaModel> respuestas = respuestaService.listarRespuestas();
        return new ResponseEntity<>(respuestas, HttpStatus.OK);
    }

    @GetMapping("/buscarporid/{id}")
    public ResponseEntity<?> buscarRespuestaPorId(@PathVariable String id) {
        try {
            ObjectId objectId = new ObjectId(id);
            RespuestaModel respuesta = respuestaService.buscarRespuestaPorId(objectId);
            if (respuesta == null) {
                return new ResponseEntity<>("Respuesta no encontrada", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(respuesta, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("ID inválido", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<String> actualizarRespuesta(@PathVariable String id, @RequestBody RespuestaModel respuesta

