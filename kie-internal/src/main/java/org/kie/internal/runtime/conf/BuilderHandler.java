package org.kie.internal.runtime.conf;

/**
 * Handler responsible for controlling access to writable properties.
 * It's main responsibility is to accept or reject given value depending
 * on the underlying implementation e.g. disallow null values
 *
 * @see DeploymentDescriptorBuilder
 */
public interface BuilderHandler {

    /**
     * Verifies if given <code>value</code> is acceptable to be written via builder
     * @param value
     * @return
     */
    boolean accepted(Object value);
}