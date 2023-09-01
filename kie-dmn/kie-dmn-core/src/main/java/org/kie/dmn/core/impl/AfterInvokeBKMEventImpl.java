package org.kie.dmn.core.impl;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.event.AfterInvokeBKMEvent;

public class AfterInvokeBKMEventImpl
        implements AfterInvokeBKMEvent {

    private BusinessKnowledgeModelNode bkm;
    private DMNResult                  result;
    private Object invocationResult;

    public AfterInvokeBKMEventImpl(BusinessKnowledgeModelNode bkm, DMNResult result, Object invocationResult) {
        this.bkm = bkm;
        this.result = result;
        this.invocationResult = invocationResult;
    }

    @Override
    public BusinessKnowledgeModelNode getBusinessKnowledgeModel() {
        return this.bkm;
    }

    @Override
    public DMNResult getResult() {
        return this.result;
    }

    @Override
    public Object getInvocationResult() {
        return invocationResult;
    }

    @Override
    public String toString() {
        return "AfterInvokeBKMEvent{ name='"+bkm.getName()+"' id='"+bkm.getId()+"' }";
    }

}
