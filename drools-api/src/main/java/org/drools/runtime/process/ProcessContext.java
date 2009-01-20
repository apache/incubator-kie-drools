package org.drools.runtime.process;

import org.drools.runtime.KnowledgeRuntime;

public interface ProcessContext {

    ProcessInstance getProcessInstance();

    NodeInstance getNodeInstance();

    KnowledgeRuntime getKnowledgeRuntime();

    Object getVariable(String variableName);

    void setVariable(String variableName,
                     Object value);

}
