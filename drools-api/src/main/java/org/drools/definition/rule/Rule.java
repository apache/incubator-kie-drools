package org.drools.definition.rule;

import java.util.Collection;

import org.drools.definition.KnowledgeDefinition;

public interface Rule
    extends
    KnowledgeDefinition {
    
    String getPackageName();
    
    String getName();

    Collection<String> listMetaAttributes();
    
    String getMetaAttribute(final String identifier);
}
