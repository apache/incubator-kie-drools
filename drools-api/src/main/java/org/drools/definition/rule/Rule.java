package org.drools.definition.rule;

import java.util.Collection;
import java.util.Map;

import org.drools.definition.KnowledgeDefinition;

/**
 * Public Rule interface for runtime rule inspection.
 */
public interface Rule
    extends
    KnowledgeDefinition {

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
     * Returns an immutable Map<String key, Object value> of all meta data attributes associated with 
     * this rule object.
     * 
     * @return an immutable Map<String key, Object value> of meta data attributes.
     */
    Map<String, Object> getMetaData();

    /**
     * This method is deprecated. Please use {@link Rule#getMetaAttributes()} instead.
     * 
     * @return a collection with all the meta attribute keys associated with this Rule.
     * @deprecated
     */
    @Deprecated
    Collection<String> listMetaAttributes();
    
    /**
     * Returns an immutable Map<String key, String value> of all meta attributes associated with this rule object.
     * 
     * @return an immutable Map<String key, String value> of meta attributes.
     * @deprecated
     */
    @Deprecated
    Map<String, Object> getMetaAttributes();

    /**
     * Returns the value of the meta attribute identified by the "key"
     * 
     * @param key the meta attribute key
     * 
     * @return the meta attribute value or null if there is no value for that key.
     * @deprecated
     */
    @Deprecated
    String getMetaAttribute(final String key);
}
