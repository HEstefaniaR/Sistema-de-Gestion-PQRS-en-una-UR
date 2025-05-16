package com.apirest.backend.Controller;

import java.util.List;

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
    IUsuarioService usuarioService;

    // Crear un usuario
    @PostMapping("/registrar")
    public ResponseEntity<UsuarioModel> registrarUsuario(@RequestBody UsuarioModel usuario) {
        return new ResponseEntity<>(usuarioService.registrarUsuario(usuario), HttpStatus.CREATED);
    }

    // Listar usuarios
    @GetMapping("/listar")
    public ResponseEntity<List<UsuarioModel>> listarUsuarios() {
        return new ResponseEntity<>(usuarioService.listarUsuarios(), HttpStatus.OK);
    }

    // Buscar por ID
    @GetMapping("/buscarporid/{id}")
    public ResponseEntity<UsuarioModel> buscarUsuarioPorId(@PathVariable Integer id) {
        return new ResponseEntity<>(usuarioService.buscarUsuarioPorId(id), HttpStatus.OK);
    }

    // Actualizar información
    @PutMapping("/actualizarporid/{id}")
    public ResponseEntity<UsuarioModel> actualizarUsuarioPorId(@PathVariable Integer id, @RequestBody UsuarioModel usuario) {
        return new ResponseEntity<>(usuarioService.actualizarUsuarioPorId(id, usuario), HttpStatus.OK);
    }

    // Eliminar
    @DeleteMapping("/eliminarporid/{id}")
    public ResponseEntity<Void> eliminarUsuarioPorId(@PathVariable Integer id) {
        usuarioService.eliminarUsuarioPorId(id);
        return ResponseEntity.noContent().build();
    }
}