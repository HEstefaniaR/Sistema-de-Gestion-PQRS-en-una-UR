package com.apirest.backend.Model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document ("Administradores")
public class AdminModel {
    @Id
    private ObjectId id; 
    private String usuario;
    private String contrasena;

    @JsonProperty("id")
    public String getIdString() {
        return id != null ? id.toHexString() : null;
    }
}
