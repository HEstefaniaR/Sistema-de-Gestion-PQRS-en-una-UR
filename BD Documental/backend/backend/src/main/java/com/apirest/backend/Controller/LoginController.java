package com.apirest.backend.Controller;

import com.apirest.backend.DTO.Login;
import com.apirest.backend.Model.AdminModel;
import com.apirest.backend.Model.UsuarioModel;
import com.apirest.backend.Service.IAdminService;
import com.apirest.backend.Service.IUsuarioService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/UR/usuarios")
public class LoginController {
    @Autowired IUsuarioService usuarioService;
    
    @PostMapping("/insertar")
    public ResponseEntity<String> crearUsuario(@RequestBody UsuarioModel usuario) {
        return new ResponseEntity<>(usuarioService.guardarUsuario(usuario),HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<UsuarioModel>> listarUsuario() {
        return new ResponseEntity<List<UsuarioModel>>(usuarioService.listarUsuarios(),HttpStatus.OK);
    }
    
       
}
