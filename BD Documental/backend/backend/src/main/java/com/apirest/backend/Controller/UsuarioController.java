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
    private IUsuarioService usuarioService;

    @PostMapping("/insertar")
    public ResponseEntity<?> crearUsuario(@RequestBody UsuarioModel usuario) {
        try {
            UsuarioModel creado = usuarioService.guardarUsuario(usuario);
            return new ResponseEntity<>(creado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error al crear usuario: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<UsuarioModel>> listarUsuarios() {
        return new ResponseEntity<>(usuarioService.listarUsuarios(), HttpStatus.OK);
    }
    
    @GetMapping("/buscarporusuario/{usuario}")
    public ResponseEntity<?> buscarPorUsuario(@PathVariable String usuario) {
        UsuarioModel usuarioEncontrado = usuarioService.buscarPorUsuario(usuario);
        if (usuarioEncontrado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario no encontrado");
        }
        return ResponseEntity.ok(usuarioEncontrado);
    }
}