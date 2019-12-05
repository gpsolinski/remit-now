package com.gpsolinski.remitnow.repository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

public interface BasicRepository<T, ID extends Serializable> {
    Optional<T> findById(ID id);
    Collection<T> getAll();
    void save(T t);
}
