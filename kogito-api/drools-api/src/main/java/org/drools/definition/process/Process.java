package org.drools.definition.process;

import java.util.Map;

import org.drools.definition.KnowledgeDefinition;

public interface Process
    extends
    KnowledgeDefinition {

    String getId();

    String getName();

    String getVersion();

    String getPackageName();

    String getType();
    
    Map<String, Object> getMetaData();

    @Deprecated Object getMetaData(String name);

}
