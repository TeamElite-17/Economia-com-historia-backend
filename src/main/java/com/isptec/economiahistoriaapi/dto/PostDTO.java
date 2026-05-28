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
public class PostDTO {

    private String postId;

    @NotBlank(message = "O conteúdo do post é obrigatório")
    private String content;

    private String postedAt;

    private String threadId;

    private String userId;

    /** Nome do autor — preenchido na resposta */
    private String userName;

    /** Avatar do autor — preenchido na resposta */
    private String userAvatar;

    /** Número de likes no post */
    private Long likeCount;

    /** Se o utilizador atual já deu like — null se não autenticado */
    private Boolean likedByCurrentUser;
}

