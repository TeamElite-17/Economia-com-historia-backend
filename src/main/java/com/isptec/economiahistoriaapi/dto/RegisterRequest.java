package com.isptec.economiahistoriaapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "O nome é obrigatório")
    private String name;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "A palavra-passe é obrigatória")
    @Size(min = 6, message = "A palavra-passe deve ter pelo menos 6 caracteres")
    private String password;

    private String preferredLanguage;
}
