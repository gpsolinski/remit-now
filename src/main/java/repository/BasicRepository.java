package repository;

import java.io.Serializable;

public interface BasicRepository<T, ID extends Serializable> {
    T findById(ID id);
    void save(T t);
}
