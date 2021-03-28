/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.runtime.decisiontables;

import java.util.List;
import java.util.stream.Collectors;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;

public abstract class DecisionTableImpl implements DecisionTable {

    private String name;
    private List<String> parameterNames;

    public FEELFnResult<Object> evaluate(final EvaluationContext ctx,
                                         final Object[] params) {
        throw new UnsupportedOperationException("Not supported in GWT.");
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public String getSignature() {
        return getName() + "( " + parameterNames.stream().collect(Collectors.joining(", ")) + " )";
    }
}
