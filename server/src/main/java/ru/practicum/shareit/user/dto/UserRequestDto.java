package ru.practicum.shareit.user.dto;

import lombok.*;

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
