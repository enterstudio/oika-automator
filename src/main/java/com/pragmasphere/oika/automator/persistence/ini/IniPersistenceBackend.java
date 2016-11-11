package com.pragmasphere.oika.automator.persistence.ini;

import com.pragmasphere.oika.automator.persistence.PersistenceBackend;
import com.pragmasphere.oika.automator.persistence.PersistenceBackendException;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Persistence Backend implementation relying on an INI file configuration from ini4j.
 *
 * @see Ini
 */
@Component
public class IniPersistenceBackend implements PersistenceBackend {

    @Autowired
    private Ini configuration;

    @Autowired
    private IdStrategy idStrategy;

    @Autowired
    private ObjectToStringConverter objectToStringConverter;

    protected Section getSection(final Object object) {
        return getSection(object.getClass(), idStrategy.getId(object));
    }

    protected Section getSection(final Class<?> objectType, final String id) {
        return configuration.get(getSectionName(objectType, id));
    }

    protected Section newSection(final Object object) {
        return newSection(object.getClass(), idStrategy.getId(object));
    }

    protected Section newSection(final Class<?> objectType, final String id) {
        return configuration.add(getSectionName(objectType, id));
    }

    private String getSectionName(final Class<?> objectType, final String id) {
        if (id == null) {
            return objectType.getSimpleName();
        }
        return objectType.getSimpleName() + "-" + id;
    }

    protected boolean hasSection(final Object object) {
        return hasSection(object.getClass(), idStrategy.getId(object));
    }

    protected boolean hasSection(final Class<?> objectType, final String id) {
        if (id == null) {
            return configuration.keySet().contains(objectType.getSimpleName());
        } else {
            return configuration.keySet().contains(objectType.getSimpleName() + "-" + id);
        }
    }

    protected boolean isSectionOfType(final String sectionName, final Class<?> objectType) {
        return sectionName.startsWith(objectType.getSimpleName() + "-") || sectionName.equals(objectType.getSimpleName());
    }

    Ini getConfiguration() {
        return configuration;
    }

    @Override
    public void persist(final Object object) {
        delete(object);

        final Section section = newSection(object);

        final PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(object.getClass());
        for (final PropertyDescriptor property : descriptors) {
            if (property.getReadMethod().getDeclaringClass() == Object.class)
                continue;
            try {
                final Object value = property.getReadMethod().invoke(object);
                final String stringValue = objectToStringConverter.toString(value);
                section.put(property.getName(), stringValue);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new PersistenceBackendException(e);
            }
        }
    }

    @Override
    public <T> T get(final Class<T> objectType, final String id) {
        if (!hasSection(objectType, id)) {
            return null;
        }
        final Section section = getSection(objectType, id);
        return get(objectType, section);

    }

    private <T> T get(final Class<T> objectType, final Section section) {
        if (section.isEmpty())
            return null;

        final T object;
        try {
            object = objectType.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new PersistenceBackendException(e);
        }

        final PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(object.getClass());
        for (final PropertyDescriptor property : descriptors) {
            if (property.getReadMethod().getDeclaringClass() == Object.class)
                continue;
            try {
                final Object value = section.get(property.getName());
                final Object objectValue = objectToStringConverter.toObject(property.getPropertyType(), (String) value);
                property.getWriteMethod().invoke(object, objectValue);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new PersistenceBackendException(e);
            }
        }

        return object;
    }

    @Override
    public <T> List<T> list(final Class<T> objectType) {
        return configuration.values().stream().map(sectionName -> configuration.get(sectionName))
                .filter(section -> isSectionOfType(section.getName(), objectType)).map(section -> get(objectType, section))
                .collect(Collectors.toList());
    }

    @Override
    public boolean delete(final Object object) {
        final Section section = getSection(object);
        if (section != null) {
            return configuration.remove(section) != null;
        }
        return false;
    }

    @Override
    @PreDestroy
    public void flush() {
        try {
            configuration.store();
        } catch (final IOException e) {
            throw new PersistenceBackendException(e);
        }
    }
}
