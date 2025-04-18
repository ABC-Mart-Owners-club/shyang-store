package repository;

import domain.Product;
import domain.User;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private Long sequence = 5L;
    static Map<Long, User> userMap = new HashMap<>(Map.of(
            1L, new User(1L, "Simon"),
            2L, new User(2L, "Ian"),
            3L, new User(3L, "Potter"),
            4L, new User(4L, "Ashie"),
            5L, new User(5L, "Jack")
    ));

    public User findById(Long id) {
        return userMap.get(id);
    }

    public Long save(String name) {
        sequence ++;
        User user = new User(sequence, name);
        userMap.put(sequence, user);
        return sequence;
    }

    public User findByName(String name) {
        return userMap.values().stream()
                .filter(user -> user.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public Long getSequence() {
        return sequence;
    }
}
