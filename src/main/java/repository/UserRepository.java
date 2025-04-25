package repository;

import domain.User;

public interface UserRepository {

    public User save(String name, int balance);

    public User findByName(String name);

}
