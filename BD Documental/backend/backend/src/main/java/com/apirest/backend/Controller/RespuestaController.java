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
@RequestMapping("UR/respuestas")
public class RespuestaController {

    @Autowired
    private IRespuestaService respuestaService;

    @PostMapping("/insertar")
    public ResponseEntity<String> crearRespuesta(@RequestBody RespuestaModel respuesta) {
        return new ResponseEntity<>(respuestaService.guardarRespuesta(respuesta), HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<RespuestaModel>> listarRespuestas() {
        return new ResponseEntity<>(respuestaService.listarRespuestas(), HttpStatus.OK);
    }

    @GetMapping("/buscarporid/{id}")
    public ResponseEntity<RespuestaModel> buscarRespuestaPorId(@PathVariable ObjectId id) {
        return new ResponseEntity<>(respuestaService.buscarRespuestaPorId(id), HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<RespuestaModel> actualizarRespuesta(@PathVariable ObjectId id, @RequestBody RespuestaModel respuesta) {
        return new ResponseEntity<>(respuestaService.actualizarRespuesta(id, respuesta), HttpStatus.OK);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarRespuesta(@PathVariable ObjectId id) {
        respuestaService.eliminarRespuesta(id);
        return ResponseEntity.noContent().build();
    }
}
