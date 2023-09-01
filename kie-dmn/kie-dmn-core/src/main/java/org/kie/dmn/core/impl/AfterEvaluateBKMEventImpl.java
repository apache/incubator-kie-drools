package org.kie.dmn.core.impl;

import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.event.AfterEvaluateBKMEvent;

public class AfterEvaluateBKMEventImpl
        implements AfterEvaluateBKMEvent {

    private BusinessKnowledgeModelNode bkm;
    private DMNResult                  result;

    public AfterEvaluateBKMEventImpl(BusinessKnowledgeModelNode bkm, DMNResult result) {
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
        return "AfterEvaluateBKMEvent{ name='"+bkm.getName()+"' id='"+bkm.getId()+"' }";
    }

}
