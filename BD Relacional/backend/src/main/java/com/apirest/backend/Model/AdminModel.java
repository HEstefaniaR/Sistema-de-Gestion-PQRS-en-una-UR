package com.apirest.backend.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idadmin;

    private String usuario;
    private String contrasena;
}