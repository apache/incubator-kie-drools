package org.drools.definition.rule;

import org.drools.definition.KnowledgeDefinition;

public interface Rule
    extends
    KnowledgeDefinition {

    String getName();

    public String getPackageName();

}
