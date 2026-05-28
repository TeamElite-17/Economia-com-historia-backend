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
public class CommentDTO {

    private String commentId;

    @NotBlank(message = "O conteúdo do comentário é obrigatório")
    private String content;

    private String commentedAt;

    /** ID do post do fórum (mutuamente exclusivo com contentItemId) */
    private String postId;

    /** ID do conteúdo didático (mutuamente exclusivo com postId) */
    private String contentItemId;

    private String userId;

    /** Nome do autor — preenchido na resposta, não necessário no pedido */
    private String userName;

    /** Avatar do autor — preenchido na resposta */
    private String userAvatar;
}

