package com.isptec.economiahistoriaapi.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class RoleUpdateRequest {

    @NotBlank(message = "O novo role é obrigatório")
    private String role;
}
