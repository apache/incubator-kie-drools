package org.kie.dmn.core.impl;

import java.util.List;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.event.BeforeInvokeBKMEvent;

public class BeforeInvokeBKMEventImpl
        implements BeforeInvokeBKMEvent {

    private BusinessKnowledgeModelNode bkm;
    private DMNResult                  result;
    private List<Object> invocationParameters;

    public BeforeInvokeBKMEventImpl(BusinessKnowledgeModelNode bkm, DMNResult result, List<Object> invocationParameters) {
        this.bkm = bkm;
        this.result = result;
        this.invocationParameters = invocationParameters;
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
    public List<Object> getInvocationParameters() {
        return this.invocationParameters;
    }

    @Override
    public String toString() {
        return "BeforeInvokeBKMEvent{ name='" + bkm.getName() + "' id='" + bkm.getId() + "' }";
    }
}
