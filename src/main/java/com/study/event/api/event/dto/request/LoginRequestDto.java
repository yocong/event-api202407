package com.study.event.api.event.dto.request;

import lombok.*;

// 사용자가 클라이언트에서 서버로 보낸 로그인 정보
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {

    private String email;
    private String password;
    // 자동로그인 여부 ...
}
