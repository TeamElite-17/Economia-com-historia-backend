package com.isptec.economiahistoriaapi.controller;

import com.isptec.economiahistoriaapi.service.FileStorageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "14. Gestão de Ficheiros", description = "Upload, download e gestão de ficheiros de conteúdo multimídia")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    /**
     * Upload de ficheiro de vídeo
     */
    @PostMapping("/upload/video")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESCRITOR', 'REVISOR', 'APROVADOR', 'SUPERADMIN')")
    @Operation(summary = "Upload de ficheiro de vídeo", description = "Permite o upload de ficheiros de vídeo (MP4, MOV, AVI, etc.)")
    @ApiResponse(responseCode = "201", description = "Vídeo enviado com sucesso", content = @Content(schema = @Schema(implementation = Map.class)))
    @ApiResponse(responseCode = "400", description = "Ficheiro inválido ou não suportado")
    @ApiResponse(responseCode = "413", description = "Ficheiro demasiado grande")
    public ResponseEntity<Map<String, Object>> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            String filename = fileStorageService.uploadVideoFile(file);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("filename", filename);
            response.put("url", fileStorageService.getFileUrl("videos", filename));
            response.put("message", "Vídeo enviado com sucesso!");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            log.error("Erro ao enviar vídeo", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Erro ao enviar vídeo: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Upload de ficheiro de áudio/podcast
     */
    @PostMapping("/upload/audio")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESCRITOR', 'REVISOR', 'APROVADOR', 'SUPERADMIN')")
    @Operation(summary = "Upload de ficheiro de áudio/podcast", description = "Permite o upload de ficheiros de áudio (MP3, WAV, OGG, etc.)")
    @ApiResponse(responseCode = "201", description = "Áudio enviado com sucesso")
    @ApiResponse(responseCode = "400", description = "Ficheiro inválido ou não suportado")
    public ResponseEntity<Map<String, Object>> uploadAudio(@RequestParam("file") MultipartFile file) {
        try {
            String filename = fileStorageService.uploadAudioFile(file);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("filename", filename);
            response.put("url", fileStorageService.getFileUrl("audios", filename));
            response.put("message", "Áudio enviado com sucesso!");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            log.error("Erro ao enviar áudio", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Erro ao enviar áudio: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Upload de ficheiro de imagem/thumbnail
     */
    @PostMapping("/upload/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESCRITOR', 'REVISOR', 'APROVADOR', 'SUPERADMIN')")
    @Operation(summary = "Upload de imagem/thumbnail", description = "Permite o upload de imagens para thumbail de conteúdo")
    @ApiResponse(responseCode = "201", description = "Imagem enviada com sucesso")
    @ApiResponse(responseCode = "400", description = "Ficheiro inválido ou não suportado")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String filename = fileStorageService.uploadImageFile(file);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("filename", filename);
            response.put("url", fileStorageService.getFileUrl("images", filename));
            response.put("message", "Imagem enviada com sucesso!");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            log.error("Erro ao enviar imagem", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Erro ao enviar imagem: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * Download de ficheiro
     */
    @GetMapping("/download/{subdirectory}/{filename}")
    @Operation(summary = "Download de ficheiro", description = "Permite o download de ficheiros de conteúdo")
    @ApiResponse(responseCode = "200", description = "Ficheiro descarregado com sucesso")
    @ApiResponse(responseCode = "404", description = "Ficheiro não encontrado")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String subdirectory,
            @PathVariable String filename) {
        try {
            // Validação de segurança: apenas permitir diretórios específicos
            if (!subdirectory.matches("^(videos|audios|images)$")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Evitar directory traversal attacks
            if (filename.contains("..") || filename.contains("/")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            byte[] fileData = fileStorageService.downloadFile(subdirectory, filename);
            Resource resource = new org.springframework.core.io.ByteArrayResource(fileData);

            // Determinar o tipo de conteúdo com base no ficheiro
            String contentType = Files.probeContentType(Paths.get(filename));
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (IOException e) {
            log.error("Erro ao descarregar ficheiro: {}/{}", subdirectory, filename, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Informações sobre um ficheiro
     */
    @GetMapping("/info/{subdirectory}/{filename}")
    @Operation(summary = "Informações do ficheiro", description = "Obtém informações detalhadas sobre um ficheiro")
    @ApiResponse(responseCode = "200", description = "Informações do ficheiro")
    @ApiResponse(responseCode = "404", description = "Ficheiro não encontrado")
    public ResponseEntity<Map<String, Object>> getFileInfo(
            @PathVariable String subdirectory,
            @PathVariable String filename) {
        try {
            return ResponseEntity.ok(fileStorageService.getFileInfo(subdirectory, filename));
        } catch (IOException e) {
            log.error("Erro ao obter informações do ficheiro: {}/{}", subdirectory, filename, e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Ficheiro não encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Eliminar um ficheiro
     */
    @DeleteMapping("/delete/{subdirectory}/{filename}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @Operation(summary = "Eliminar ficheiro", description = "Elimina um ficheiro do servidor")
    @ApiResponse(responseCode = "200", description = "Ficheiro eliminado com sucesso")
    @ApiResponse(responseCode = "404", description = "Ficheiro não encontrado")
    public ResponseEntity<Map<String, Object>> deleteFile(
            @PathVariable String subdirectory,
            @PathVariable String filename) {
        try {
            fileStorageService.deleteFile(subdirectory, filename);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Ficheiro eliminado com sucesso!");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Erro ao eliminar ficheiro: {}/{}", subdirectory, filename, e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Erro ao eliminar ficheiro: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
