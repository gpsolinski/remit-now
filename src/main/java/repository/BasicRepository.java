package repository;

import java.io.Serializable;
import java.util.Optional;

public interface BasicRepository<T, ID extends Serializable> {
    Optional<T> findById(ID id);
    void save(T t);
}
