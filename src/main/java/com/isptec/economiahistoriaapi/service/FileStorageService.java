package com.isptec.economiahistoriaapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    @Value("${app.file.max-file-size}")
    private long maxFileSize;

    @Value("${app.file.allowed-video-types}")
    private String allowedVideoTypes;

    @Value("${app.file.allowed-audio-types}")
    private String allowedAudioTypes;

    @Value("${app.file.allowed-image-types}")
    private String allowedImageTypes;

    /**
     * Upload de ficheiro de vídeo
     */
    public String uploadVideoFile(MultipartFile file) throws IOException {
        validateFile(file, allowedVideoTypes);
        return saveFile(file, "videos");
    }

    /**
     * Upload de ficheiro de áudio/podcast
     */
    public String uploadAudioFile(MultipartFile file) throws IOException {
        validateFile(file, allowedAudioTypes);
        return saveFile(file, "audios");
    }

    /**
     * Upload de ficheiro de imagem (thumbnail)
     */
    public String uploadImageFile(MultipartFile file) throws IOException {
        validateFile(file, allowedImageTypes);
        return saveFile(file, "images");
    }

    /**
     * Valida o ficheiro antes do upload
     */
    private void validateFile(MultipartFile file, String allowedTypes) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Ficheiro está vazio");
        }

        if (file.getSize() > maxFileSize * 1024 * 1024) {
            throw new IllegalArgumentException("Tamanho do ficheiro excede o limite de " + maxFileSize + "MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("Nome do ficheiro inválido");
        }

        String contentType = file.getContentType();
        boolean mimeOk = contentType != null && isAllowedType(contentType, allowedTypes);
        boolean extOk = isAllowedExtension(originalFilename, allowedTypes);
        if (!mimeOk && !extOk) {
            throw new IllegalArgumentException("Tipo de ficheiro não permitido: " + contentType);
        }
    }

    private boolean isAllowedExtension(String filename, String allowedTypes) {
        String ext = "";
        int dot = filename.lastIndexOf('.');
        if (dot >= 0) {
            ext = filename.substring(dot + 1).toLowerCase();
        }
        if (ext.isEmpty()) {
            return false;
        }
        String types = allowedTypes.toLowerCase();
        if (types.contains("video")) {
            return List.of("mp4", "mov", "avi", "mkv", "webm").contains(ext);
        }
        if (types.contains("audio")) {
            return List.of("mp3", "wav", "ogg", "m4a", "webm", "aac", "flac").contains(ext);
        }
        if (types.contains("image")) {
            return List.of("jpg", "jpeg", "png", "webp", "gif").contains(ext);
        }
        return false;
    }

    /**
     * Verifica se o tipo de ficheiro é permitido
     */
    private boolean isAllowedType(String contentType, String allowedTypes) {
        String[] types = allowedTypes.split(",");
        for (String type : types) {
            if (contentType.trim().equalsIgnoreCase(type.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Guarda o ficheiro no disco
     */
    private String saveFile(MultipartFile file, String subdirectory) throws IOException {
        Path uploadPath = Paths.get(uploadDir, subdirectory);
        Files.createDirectories(uploadPath);

        String filename = generateUniqueFilename(file.getOriginalFilename());
        Path filePath = uploadPath.resolve(filename);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        log.info("Ficheiro guardado: {}", filePath.toAbsolutePath());
        return filename;
    }

    /**
     * Gera um nome de ficheiro único
     */
    private String generateUniqueFilename(String originalFilename) {
        String timestamp = System.currentTimeMillis() + "-";
        String cleanedFilename = originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");
        return timestamp + cleanedFilename;
    }

    /**
     * Obtém um ficheiro pelo caminho
     */
    public byte[] downloadFile(String subdirectory, String filename) throws IOException {
        Path filePath = Paths.get(uploadDir, subdirectory, filename);

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Ficheiro não encontrado: " + filename);
        }

        return Files.readAllBytes(filePath);
    }

    /**
     * Elimina um ficheiro
     */
    public void deleteFile(String subdirectory, String filename) throws IOException {
        Path filePath = Paths.get(uploadDir, subdirectory, filename);

        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.info("Ficheiro eliminado: {}", filePath.toAbsolutePath());
        }
    }

    /**
     * Obtém o tipo de media baseado na extensão
     */
    public String getMediaTypeFromFilename(String filename) {
        if (filename == null) return "TEXT";

        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        switch (extension) {
            case "mp4":
            case "mov":
            case "avi":
            case "mkv":
            case "webm":
                return "VIDEO";
            case "mp3":
            case "wav":
            case "ogg":
            case "m4a":
            case "flac":
                return "AUDIO";
            default:
                return "TEXT";
        }
    }

    /**
     * Obtém o caminho relativo para servir o ficheiro
     */
    public String getFileUrl(String subdirectory, String filename) {
        return "/api/v1/files/download/" + subdirectory + "/" + filename;
    }

    /**
     * Obtém informações do ficheiro
     */
    public Map<String, Object> getFileInfo(String subdirectory, String filename) throws IOException {
        Path filePath = Paths.get(uploadDir, subdirectory, filename);

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Ficheiro não encontrado: " + filename);
        }

        Map<String, Object> info = new HashMap<>();
        info.put("filename", filename);
        info.put("size", Files.size(filePath));
        info.put("lastModified", Files.getLastModifiedTime(filePath).toMillis());
        info.put("url", getFileUrl(subdirectory, filename));
        info.put("mediaType", getMediaTypeFromFilename(filename));

        return info;
    }

    /**
     * Lista todos os ficheiros de um diretório
     */
    public List<Map<String, Object>> listFiles(String subdirectory) throws IOException {
        Path dirPath = Paths.get(uploadDir, subdirectory);
        List<Map<String, Object>> files = new ArrayList<>();

        if (!Files.exists(dirPath)) {
            return files;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            for (Path filePath : stream) {
                if (Files.isRegularFile(filePath)) {
                    files.add(getFileInfo(subdirectory, filePath.getFileName().toString()));
                }
            }
        }

        return files;
    }
}
