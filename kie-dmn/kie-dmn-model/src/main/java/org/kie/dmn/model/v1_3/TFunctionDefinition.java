/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.model.v1_3;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.FunctionDefinition;
import org.kie.dmn.model.api.FunctionKind;
import org.kie.dmn.model.api.InformationItem;

public class TFunctionDefinition extends TExpression implements FunctionDefinition {

    protected List<InformationItem> formalParameter;
    protected Expression expression;
    protected FunctionKind kind;

    @Override
    public List<InformationItem> getFormalParameter() {
        if (formalParameter == null) {
            formalParameter = new ArrayList<>();
        }
        return this.formalParameter;
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public void setExpression(Expression value) {
        this.expression = value;
    }

    @Override
    public FunctionKind getKind() {
        if (kind == null) {
            return FunctionKind.FEEL;
        } else {
            return kind;
        }
    }

    @Override
    public void setKind(FunctionKind value) {
        this.kind = value;
    }

}
