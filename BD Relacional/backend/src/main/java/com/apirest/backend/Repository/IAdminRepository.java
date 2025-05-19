package com.apirest.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.apirest.backend.Model.AdminModel;

@Repository
public interface IAdminRepository extends JpaRepository<AdminModel, Integer> {
    AdminModel findByUsuario(String usuario);
}