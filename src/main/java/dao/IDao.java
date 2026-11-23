package dao;

import java.util.List;

public interface IDao<T> {
    void create(T obj);

    T read(int id);

    void update(T obj);

    void delete(int id);

    List<T> findAll();
}
