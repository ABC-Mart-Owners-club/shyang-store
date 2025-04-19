package repository;

import domain.User;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    static Map<String, User> userMap = new HashMap<>(Map.of(
            "Simon", new User("Simon", 5_000_000),
            "Ian", new User("Ian", 2_000_000),
            "Potter", new User("Potter", 3_000_000),
            "Ashie", new User("Ashie", 1_000_000),
            "Jack", new User("Jack", 200_000)
    ));


    public User save(String name, int balance) {
        User user = new User(name, balance);
        userMap.put(name, user);
        return user;
    }

    public User findByName(String name) {
        return userMap.get(name);
    }

}
