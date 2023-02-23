package com.bamdoliro.stupetition.domain.user.presentation.dto.response;

import com.bamdoliro.stupetition.domain.user.domain.User;
import com.bamdoliro.stupetition.domain.user.domain.type.Authority;
import com.bamdoliro.stupetition.domain.user.domain.type.Status;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {

    private Long userId;
    private String schoolName;
    private String email;
    private Authority authority;
    private Status status;

    public static UserResponseDto of(User user) {
        return UserResponseDto.builder()
                .userId(user.getId())
                .schoolName(user.getSchool().getName())
                .email(user.getEmail())
                .authority(user.getAuthority())
                .status(user.getStatus())
                .build();
    }
}
