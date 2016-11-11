package com.pragmasphere.oika.automator.persistence.ini;

import com.pragmasphere.oika.automator.persistence.PersistenceBackendException;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class DefaultObjectToStringConverter implements ObjectToStringConverter {
    @Override
    public <T> T toObject(final Class<T> targetType, final String stringValue) {
        if (stringValue == null)
            return null;
        if (targetType == String.class)
            return (T) stringValue;
        if (targetType == Path.class) {
            return (T) Paths.get(stringValue);
        }
        if (targetType == Integer.class || targetType == int.class) {
            return (T) (Integer) Integer.parseInt(stringValue);
        }
        if (targetType == Long.class || targetType == long.class) {
            return (T) (Long) Long.parseLong(stringValue);
        }
        throw new PersistenceBackendException("Unsupported type: " + targetType);
    }

    @Override
    public String toString(final Object objectValue) {
        if (objectValue == null)
            return null;
        if (objectValue instanceof String) {
            return (String) objectValue;
        }
        if (objectValue instanceof Path) {
            return ((Path) objectValue).toFile().getPath();
        }
        if (objectValue instanceof Integer) {
            return objectValue.toString();
        }
        if (objectValue instanceof Long) {
            return objectValue.toString();
        }
        throw new PersistenceBackendException("Unsupported type: " + objectValue.getClass());
    }
}
