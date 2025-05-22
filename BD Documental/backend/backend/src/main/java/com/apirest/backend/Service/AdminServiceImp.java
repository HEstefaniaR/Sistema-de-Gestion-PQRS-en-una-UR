package com.apirest.backend.Service;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
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
    public AdminModel buscarAdminPorId(ObjectId id) {
        Optional<AdminModel> adminOptional = adminRepository.findById(id);
        return adminOptional.orElse(null);
    }

    @Override
    public AdminModel buscarPorUsuario(String usuario) {
        return adminRepository.findByUsuario(usuario);
    }
}

