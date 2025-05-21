package com.apirest.backend.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class AlmacenamientoService {

    private final Path directorioBase;

    public AlmacenamientoService(@Value("${app.upload.dir:uploads}") String uploadDir) throws IOException {
        this.directorioBase = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(directorioBase);
    }

    public String almacenarArchivo(MultipartFile archivo) throws IOException {
        if (archivo.isEmpty()) {
            throw new IOException("El archivo está vacío");
        }
        String nombreOriginal = Objects.requireNonNull(archivo.getOriginalFilename());
        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        String nombreUnico = UUID.randomUUID() + extension;
        Path destino = directorioBase.resolve(nombreUnico);
        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        return nombreUnico;
    }

    public Path cargarArchivo(String nombreArchivo) {
        return directorioBase.resolve(nombreArchivo);
    }

    public void eliminarArchivo(String nombreArchivo) throws IOException {
        Path archivo = directorioBase.resolve(nombreArchivo);
        Files.deleteIfExists(archivo);
    }
}