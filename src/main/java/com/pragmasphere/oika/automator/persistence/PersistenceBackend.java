package com.pragmasphere.oika.automator.persistence;

import java.util.List;

/**
 * Persistence backend interface used to persist, list and delete objects.
 */
public interface PersistenceBackend {
    /**
     * Save the given object.
     *
     * @param object object to persist
     */
    void persist(Object object);

    /**
     * Get an object from it's identifier.
     *
     * @param objectType Type of the object to get
     * @param id         Identifier of the object to get
     * @param <T>        Type of the object to get
     * @return The found object, or null if not found.
     */
    <T> T get(Class<T> objectType, String id);

    /**
     * List existing objects of the given type.
     *
     * @param objectType type of the object to list
     * @param <T>        Type of the object to list
     * @return list of existing objects
     */
    <T> List<T> list(Class<T> objectType);

    /**
     * Delete an object from the store.
     *
     * @param object
     */
    boolean delete(Object object);

    /**
     * Flush configuration to underlying storage.
     */
    void flush();
}
