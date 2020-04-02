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

package org.kie.dmn.feel.runtime.functions;

import java.util.List;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.BaseNode;

public class CustomFEELFunction extends AbstractCustomFEELFunction<BaseNode> {

    public CustomFEELFunction(String name, List<Param> parameters, BaseNode body, EvaluationContext evaluationContext) {
        super(name, parameters, body, evaluationContext);
    }

    @Override
    protected Object internalInvoke(EvaluationContext ctx) {
        return this.body.evaluate(ctx);
    }

}
