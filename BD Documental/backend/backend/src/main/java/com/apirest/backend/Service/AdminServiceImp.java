package com.apirest.backend.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apirest.backend.Model.AdminModel;
import com.apirest.backend.Repository.IAdminRepository;

@Service
public class AdminServiceImp implements IAdminService {

    @Autowired
    private IAdminRepository adminRepository;

    @Override
    public AdminModel guardarAdmin(AdminModel admin) {
        return adminRepository.save(admin);
    }

    @Override
    public List<AdminModel> listarAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public AdminModel buscarAdminPorId(Integer id) {
        Optional<AdminModel> adminOptional = adminRepository.findById(id);
        return adminOptional.orElse(null);
    }


    @Override
    public void eliminarAdminPorId(Integer id) {
        adminRepository.deleteById(id);
    }

    @Override
    public AdminModel actualizarAdminPorId(Integer id, AdminModel adminActualizado) {
        AdminModel adminExistente = adminRepository.findById(id).orElse(null);
        if (adminExistente != null) {
            adminExistente.setUsuario(adminActualizado.getUsuario());
            adminExistente.setContrasena(adminActualizado.getContrasena());
            return adminRepository.save(adminExistente);
        }
        return null;
    }

    @Override
    public AdminModel buscarPorUsuario(String usuario) {
        return adminRepository.findByUsuario(usuario);
    }
}

