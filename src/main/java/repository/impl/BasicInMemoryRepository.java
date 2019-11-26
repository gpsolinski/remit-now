package repository.impl;

import com.google.common.annotations.VisibleForTesting;
import domain.Identifiable;
import repository.BasicRepository;

import java.io.Serializable;
import java.util.Map;
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
    public T findById(ID id) {
        return dataStore.get(id);
    }

    @Override
    public void save(T t) {
        dataStore.put(t.getId(), t);
    }
}
