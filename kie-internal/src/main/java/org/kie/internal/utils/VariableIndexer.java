package org.kie.internal.utils;

import java.util.List;

/**
 * Variable indexer that allows to transform variable instance into other representation (usually string)
 * to be able to use it for queries.
 *
 * @param <V> type of the object that will represent indexed variable
 */
public interface VariableIndexer<V> {

    /**
     * Tests if given variable shall be indexed by this indexer
     *
     * NOTE: only one indexer can be used for given variable
     *
     * @param variable variable to be indexed
     * @return true if variable should be indexed with this indexer
     */
    boolean accept(Object variable);

    /**
     * Performs index/transform operation of the variable. Result of this operation can be
     * either single value or list of values to support complex type separation.
     * For example when variable is of type Person that has name, address phone indexer could
     * build three entries out of it to represent individual fields:
     * person = person.name
     * address = person.address.street
     * phone = person.phone
     * that will allow more advanced queries to be used to find relevant entries.
     * @param name name of the variable
     * @param variable actual variable value
     * @return
     */
    List<V> index(String name, Object variable);
}
