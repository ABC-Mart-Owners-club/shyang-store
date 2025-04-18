package domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
// 유저 정보
public class User {
    private Long id; // 유저 식별 값
    private String name; // 유저 이름
}
