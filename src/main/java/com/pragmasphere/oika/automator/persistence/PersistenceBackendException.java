package com.pragmasphere.oika.automator.persistence;

public class PersistenceBackendException extends RuntimeException {
    public PersistenceBackendException() {
    }

    public PersistenceBackendException(final String message) {
        super(message);
    }

    public PersistenceBackendException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public PersistenceBackendException(final Throwable cause) {
        super(cause);
    }
}
