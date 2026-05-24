package com.isptec.economiahistoriaapi.model;

import com.isptec.economiahistoriaapi.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.Date;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // Substitui a String userId por um UUID robusto automático
    @Column(name = "user_id")
    private String userId;

    @NotBlank(message = "O nome é obrigatório")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Formato de email inválido")
    @Column(nullable = false, unique = true) // Garante que não há emails duplicados no MySQL
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING) // Guarda o texto do Enum (STUDENT, ADMIN...) no banco de dados
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "preferred_language")
    private String preferredLanguage;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "registration_date", nullable = false, updatable = false)
    private Date registrationDate;

    // Método especial do JPA para preencher a data de registo automaticamente antes de salvar
    @PrePersist
    protected void onCreate() {
        this.registrationDate = new Date();
    }
}
