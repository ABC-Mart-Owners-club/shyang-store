package user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
// 유저 정보
public class User {
    private String name; // 유저 이름 (유니크라고 가정)
}
