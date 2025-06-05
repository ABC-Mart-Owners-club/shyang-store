package user.repository;

import user.domain.User;

public interface UserRepository {

    public User save(User user);

    public User findByName(String name);

}
