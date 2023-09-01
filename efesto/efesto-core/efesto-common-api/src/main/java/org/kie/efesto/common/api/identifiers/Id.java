package org.kie.efesto.common.api.identifiers;

/**
 * An identifier for a resource. It always contains a local part.
 */
public interface Id {
    LocalId toLocalId();
}
