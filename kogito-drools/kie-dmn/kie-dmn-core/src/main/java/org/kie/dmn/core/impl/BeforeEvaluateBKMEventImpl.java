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
