package com.apirest.backend.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class AlmacenamientoService {
    private final Path destinoDir = Paths.get("uploads").toAbsolutePath().normalize();

    public AlmacenamientoService() throws IOException {
        Files.createDirectories(destinoDir);
    }

    public String guardarArchivo(MultipartFile archivo) throws IOException {
        String nombreArchivo = UUID.randomUUID() + "_" + 
            Objects.requireNonNull(archivo.getOriginalFilename());
        
        Path destino = destinoDir.resolve(nombreArchivo);
        archivo.transferTo(destino);
        
        return "uploads/" + nombreArchivo;
    }
}