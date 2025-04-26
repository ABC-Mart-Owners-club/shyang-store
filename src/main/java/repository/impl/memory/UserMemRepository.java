package repository.impl.memory;

import domain.User;
import repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

public class UserMemRepository implements UserRepository {
    static Map<String, User> userMap = new HashMap<>(Map.of(
            "Simon", new User("Simon"),
            "Ian", new User("Ian"),
            "Potter", new User("Potter"),
            "Ashie", new User("Ashie"),
            "Jack", new User("Jack")
    ));


    @Override
    public User save(String name, int balance) {
        User user = new User(name);
        userMap.put(name, user);
        return user;
    }

    @Override
    public User findByName(String name) {
        return userMap.get(name);
    }

}
