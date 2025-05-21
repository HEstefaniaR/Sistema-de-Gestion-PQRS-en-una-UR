package com.apirest.backend.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.apirest.backend.Model.AdminModel;
import com.apirest.backend.Service.IAdminService;

@RestController
@RequestMapping("UR/administradores")
public class AdminController {

    @Autowired
    private IAdminService adminService;

    @PostMapping("/insertar")
    public ResponseEntity<AdminModel> insertarAdmin(@RequestBody AdminModel admin) {
        return new ResponseEntity<>(adminService.guardarAdmin(admin), HttpStatus.CREATED);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<AdminModel>> listarAdmins() {
        return new ResponseEntity<>(adminService.listarAdmins(), HttpStatus.OK);
    }

    @GetMapping("/buscarporid/{id}")
    public ResponseEntity<AdminModel> buscarPorId(@PathVariable Integer id) {
        AdminModel admin = adminService.buscarAdminPorId(id);
        if (admin != null) {
            return new ResponseEntity<>(admin, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}