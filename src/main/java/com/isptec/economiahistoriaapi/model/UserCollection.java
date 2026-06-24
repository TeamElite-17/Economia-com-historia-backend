package com.isptec.economiahistoriaapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

/**
 * Coleções do utilizador: histórico, guardados e subscrições.
 */
@Entity
@Table(name = "user_collections",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "item_type", "item_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "collection_id")
    private String collectionId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    /** HISTORY | SAVED | SUBSCRIPTION */
    @Column(name = "item_type", nullable = false, length = 20)
    private String itemType;

    /** ID do conteúdo, quiz ou autor */
    @Column(name = "item_id", nullable = false, length = 36)
    private String itemId;

    /**
     * Preferência de notificação para subscrições.
     * Valores: "ALL" (receber todas) | "NONE" (não receber).
     * Apenas relevante quando itemType = "SUBSCRIPTION".
     */
    @Column(name = "notification_pref", length = 10)
    @Builder.Default
    private String notificationPref = "ALL";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        if (this.notificationPref == null) {
            this.notificationPref = "ALL";
        }
    }
}

