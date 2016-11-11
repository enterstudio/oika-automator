package com.pragmasphere.oika.automator.persistence.ini;

public interface ObjectToStringConverter {
    <T> T toObject(Class<T> targetType, String stringValue);

    String toString(Object objectValue);
}
