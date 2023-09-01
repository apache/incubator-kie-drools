package org.kie.efesto.common.api.identifiers;

/**
 * An identifier for a local resource
 */
public interface LocalId extends Id {
    LocalUri asLocalUri();
}
