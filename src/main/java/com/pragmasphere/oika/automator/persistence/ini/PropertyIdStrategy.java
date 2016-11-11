package com.pragmasphere.oika.automator.persistence.ini;

import com.pragmasphere.oika.automator.persistence.PersistenceBackendException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

@Component
public class PropertyIdStrategy implements IdStrategy {
    @Override
    public String getId(final Object object) {
        final PropertyDescriptor nameProperty = BeanUtils.getPropertyDescriptor(object.getClass(), "id");
        if (nameProperty == null || nameProperty.getReadMethod() == null)
            return null;
        try {
            final Object invoke = nameProperty.getReadMethod().invoke(object);
            if (invoke instanceof String)
                return (String) invoke;
            if (invoke == null)
                return null;
            throw new PersistenceBackendException(
                    object + " can't be persisted because it's \"id\" property doesn't return a string.");
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new PersistenceBackendException("An error has occured while persisting this object " + object + ".", e);
        }
    }
}
