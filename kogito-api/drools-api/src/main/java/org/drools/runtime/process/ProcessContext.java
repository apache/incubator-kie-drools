package org.drools.runtime.process;

import org.drools.runtime.KnowledgeContext;

public interface ProcessContext  extends KnowledgeContext {

    ProcessInstance getProcessInstance();

    NodeInstance getNodeInstance();

    Object getVariable(String variableName);

    void setVariable(String variableName,
                     Object value);

}
