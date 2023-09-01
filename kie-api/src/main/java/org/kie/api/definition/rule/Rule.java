package org.kie.api.definition.rule;

import java.util.Map;

import org.kie.api.definition.KieDefinition;

/**
 * Public Rule interface for runtime rule inspection.
 */
public interface Rule
    extends
    KieDefinition {

    /**
     * Returns the package name (namespace) this rule is tied to.
     *
     * @return the package name.
     */
    String getPackageName();

    /**
     * Returns this rule's name.
     *
     * @return the rule name
     */
    String getName();

    /**
     * Returns an immutable Map&lt;String key, Object value&gt; of all meta data attributes associated with
     * this rule object.
     *
     * @return an immutable Map&lt;String key, Object value&gt; of meta data attributes.
     */
    Map<String, Object> getMetaData();

    int getLoadOrder();
}
