package com.pragmasphere.oika.automator.persistence.ini;

/**
 * Strategy to generate or retrieve ID from any object.
 */
public interface IdStrategy {
    /**
     * Get the id of the given object.
     *
     * @param object
     */
    String getId(Object object);
}
