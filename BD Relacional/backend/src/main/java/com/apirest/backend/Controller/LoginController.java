package com.apirest.backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apirest.backend.DTO.Login;
import com.apirest.backend.Model.RolUsuario;
import com.apirest.backend.Model.UsuarioModel;
import com.apirest.backend.Service.IAdminService;
import com.apirest.backend.Service.IUsuarioService;
import com.apirest.backend.Model.AdminModel;

@RestController
@RequestMapping("UR/usuarios")
public class LoginController {

    @Autowired
    IUsuarioService usuarioService;
    @Autowired
    IAdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login loginRequest) {
        
        // Mira si es un admin
        AdminModel admin = adminService.buscarPorUsuario(loginRequest.getUsuario());

        if (admin != null) {
            if (admin.getContrasena().equals(loginRequest.getContrasena())) {
                return ResponseEntity.ok("Login exitoso (admin): " + admin.getUsuario());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta para admin");
            }
        }

        UsuarioModel usuario = usuarioService.buscarPorUsuario(loginRequest.getUsuario());

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
        }

        if (usuario.getRol() == RolUsuario.anonimo) {
            return ResponseEntity.ok("Login anónimo exitoso: " + usuario.getUsuario());
        }

        String contrasenaGuardada = usuario.getContrasena();
        String contrasenaIngresada = loginRequest.getContrasena();

        if (contrasenaGuardada == null || contrasenaIngresada == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña requerida");
        }

        if (contrasenaGuardada.equals(contrasenaIngresada)) {
            return ResponseEntity.ok("Login exitoso (usuario): " + usuario.getNombreCompleto());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
        }
    }
}
