package repository;

import domain.Product;
import domain.User;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private Long sequence = 4L;
    static Map<Long, User> userMap = new HashMap<>(Map.of(
            0L, new User(0L, "Simon"),
            1L, new User(1L, "Ian"),
            2L, new User(2L, "Potter"),
            3L, new User(3L, "Ashie"),
            4L, new User(4L, "Jack")
    ));

    public User findById(Long id) {
        return userMap.get(id);
    }

    public Long save(String name, int balance) {
        sequence ++;
        User user = new User(sequence, name, balance);
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
