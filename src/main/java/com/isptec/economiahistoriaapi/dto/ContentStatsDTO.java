package com.isptec.economiahistoriaapi.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentStatsDTO {
    private String contentId;
    private Long viewCount;
    private Long shareCount;
    private Long likeCount;
    private Long commentCount;
    private LocalDateTime lastUpdated;
    /** Se o utilizador atual já deu gosto (requer autenticação). */
    private Boolean likedByCurrentUser;
}
