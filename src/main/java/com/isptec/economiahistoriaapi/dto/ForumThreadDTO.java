package com.isptec.economiahistoriaapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumThreadDTO {

    private String threadId;

    @NotBlank(message = "O título da thread é obrigatório")
    private String title;

    private String createdAt;

    private String forumModuleId;
    private String topicId;

    /** ID do utilizador que criou a thread — opcional no pedido, preenchido na resposta */
    private String createdByUserId;

    /** Nome do criador — preenchido na resposta */
    private String createdByUserName;

    /** Avatar do criador — preenchido na resposta */
    private String createdByUserAvatar;

    /** Número de posts na thread — preenchido na resposta */
    private Integer postCount;
}

