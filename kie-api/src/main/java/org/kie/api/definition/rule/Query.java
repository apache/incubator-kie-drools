package org.kie.api.definition.rule;

import java.util.Map;

import org.kie.api.definition.KieDefinition;

/**
 * Public Query interface for runtime query inspection.
 */
public interface Query
    extends
    KieDefinition {

    /**
     * Returns the package name (namespace) this query is tied to.
     *
     * @return the package name.
     */
    String getPackageName();

    /**
     * Returns this query's name.
     *
     * @return the query name
     */
    String getName();

    /**
     * Returns an immutable Map&lt;String key, Object value&gt; of all meta data attributes associated with
     * this query object.
     *
     * @return an immutable Map&lt;String key, Object value&gt; of meta data attributes.
     */
    Map<String, Object> getMetaData();

}
