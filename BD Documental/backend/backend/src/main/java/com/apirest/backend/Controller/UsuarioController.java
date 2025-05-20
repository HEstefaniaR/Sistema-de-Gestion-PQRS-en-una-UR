package com.apirest.backend.Controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.apirest.backend.Model.UsuarioModel;
import com.apirest.backend.Service.IUsuarioService;

@RestController
@RequestMapping("UR/usuarios")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;

    @PostMapping("/insertar")
    public ResponseEntity<String> crearUsuario(@RequestBody UsuarioModel usuario) {
        return new ResponseEntity<>(usuarioService.guardarUsuario(usuario), HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<UsuarioModel>> listarUsuarios() {
        return new ResponseEntity<>(usuarioService.listarUsuarios(), HttpStatus.OK);
    }

    @GetMapping("/buscarporid/{id}")
    public ResponseEntity<UsuarioModel> buscarUsuarioPorId(@PathVariable ObjectId id) {
        return new ResponseEntity<>(usuarioService.buscarUsuarioPorId(id), HttpStatus.OK);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<UsuarioModel> actualizarUsuario(@PathVariable ObjectId id, @RequestBody UsuarioModel usuario) {
        return new ResponseEntity<>(usuarioService.actualizarUsuario(id, usuario), HttpStatus.OK);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable ObjectId id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
