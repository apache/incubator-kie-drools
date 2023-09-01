package org.kie.internal.runtime.manager;

import java.util.Collection;

/**
 * Allows to apply filtering on runtime manager identifiers to find only those matching
 *
 */
public interface RuntimeManagerIdFilter {

    /**
     * Filters given <code>identifiers</code> based on given pattern and return only those matching.
     * @param pattern pattern used to filter identifiers
     * @param identifiers all available identifiers
     * @return returns only matched identifiers or empty list in case of no match found
     */
    Collection<String> filter(String pattern, Collection<String> identifiers);
}
