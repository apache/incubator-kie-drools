package org.kie.dmn.core.ast;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.model.api.BusinessKnowledgeModel;

public class BusinessKnowledgeModelNodeImpl
        extends DMNBaseNode
        implements BusinessKnowledgeModelNode {

    private BusinessKnowledgeModel bkm;
    private DMNExpressionEvaluator evaluator;
    private DMNType                type;
    private DMNType                resultType;

    public BusinessKnowledgeModelNodeImpl() {
    }

    public BusinessKnowledgeModelNodeImpl(BusinessKnowledgeModel bkm) {
        this(bkm, null, null);
    }

    public BusinessKnowledgeModelNodeImpl(BusinessKnowledgeModel bkm, DMNType type, DMNType resultType) {
        super( bkm );
        this.bkm = bkm;
        this.type = type;
        this.resultType = resultType;
    }

    @Override
    public BusinessKnowledgeModel getBusinessKnowledModel() {
        return bkm;
    }

    public void setBusinessKnowledgeModel(BusinessKnowledgeModel bkm) {
        this.bkm = bkm;
    }

    public DMNExpressionEvaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(DMNExpressionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public DMNType getResultType() {
        return resultType;
    }

    public void setResultType(DMNType resultType) {
        this.resultType = resultType;
    }

    public void setType(DMNType type) {
        this.type = type;
    }

    @Override
    public DMNType getType() {
        return this.type;
    }
}
