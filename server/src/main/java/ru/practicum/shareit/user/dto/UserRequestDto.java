package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserRequestDto {
    private String name;
    private String email;

    @Data
    @AllArgsConstructor
     public static class UserDtoOnlyId {
         private Long id;
     }
}
