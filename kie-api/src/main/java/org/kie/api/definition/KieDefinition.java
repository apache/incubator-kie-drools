package org.kie.api.definition;

/**
 * Marker interface for all KnowlegeDefinition's
 */
public interface KieDefinition {

    public KnowledgeType getKnowledgeType();

    public String getNamespace();

    public String getId();

    public enum KnowledgeType {
        RULE, TYPE, WINDOW, ENUM, PROCESS, FUNCTION, QUERY
    }
}
