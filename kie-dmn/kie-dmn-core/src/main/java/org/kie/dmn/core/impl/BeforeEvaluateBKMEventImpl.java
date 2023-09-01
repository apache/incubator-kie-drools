package org.kie.dmn.core.impl;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.event.BeforeEvaluateBKMEvent;

public class BeforeEvaluateBKMEventImpl
        implements BeforeEvaluateBKMEvent {

    private BusinessKnowledgeModelNode bkm;
    private DMNResult                  result;

    public BeforeEvaluateBKMEventImpl(BusinessKnowledgeModelNode bkm, DMNResult result) {
        this.bkm = bkm;
        this.result = result;
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
    public String toString() {
        return "BeforeEvaluateBKMEvent{ name='" + bkm.getName() + "' id='" + bkm.getId() + "' }";
    }
}
