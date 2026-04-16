package ru.practicum.ewm.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewCategoryDto {
    @NotBlank(message = "Название категории не может быть пустым")
    @Size(max = 50, message = "Название категории не должно превышать 50 символов")
    private String name;
}