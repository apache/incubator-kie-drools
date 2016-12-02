/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.ast;

import org.kie.dmn.core.api.DMNType;
import org.kie.dmn.feel.model.v1_1.BusinessKnowledgeModel;

public class BusinessKnowledgeModelNode
        extends DMNBaseNode
        implements DMNNode {

    private BusinessKnowledgeModel bkm;
    private DMNExpressionEvaluator evaluator;
    private DMNType                resultType;

    public BusinessKnowledgeModelNode() {
    }

    public BusinessKnowledgeModelNode(BusinessKnowledgeModel bkm, DMNType resultType) {
        super( bkm );
        this.bkm = bkm;
        this.resultType = resultType;
    }

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

    public DMNType getResultType() {
        return resultType;
    }

}
