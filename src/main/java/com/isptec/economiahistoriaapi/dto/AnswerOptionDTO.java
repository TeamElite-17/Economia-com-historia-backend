package com.isptec.economiahistoriaapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerOptionDTO {
    private String optionId;
    private String text;
    private Boolean correct;
}
