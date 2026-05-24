package com.isptec.economiahistoriaapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDTO {
    private String categoryId;

    @NotBlank(message = "O nome da categoria é obrigatório")
    @Size(max = 100)
    private String name;

    @Size(max = 120)
    private String slug;

    private String description;
    private String parentCategoryId;
    private String parentCategoryName;
}
