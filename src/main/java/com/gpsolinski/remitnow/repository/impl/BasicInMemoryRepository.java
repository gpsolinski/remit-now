package com.gpsolinski.remitnow.repository.impl;

import com.google.common.annotations.VisibleForTesting;
import com.gpsolinski.remitnow.domain.Identifiable;
import com.gpsolinski.remitnow.repository.BasicRepository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BasicInMemoryRepository<T extends Identifiable<ID>, ID extends Serializable> implements BasicRepository<T, ID> {

    private final Map<ID, T> dataStore;

    protected BasicInMemoryRepository() {
        dataStore = new ConcurrentHashMap<>();
    }

    @VisibleForTesting
    BasicInMemoryRepository(Map<ID, T> dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(dataStore.get(id));
    }

    @Override
    public Collection<T> getAll() {
        return dataStore.values();
    }

    @Override
    public void save(T t) {
        dataStore.put(t.getId(), t);
    }
}
