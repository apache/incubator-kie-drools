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

package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.runtime.functions.CustomFEELFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionDefNode
        extends BaseNode {

    private List<NameDefNode> formalParameters;
    private boolean external;
    private BaseNode body;

    public FunctionDefNode(ParserRuleContext ctx, ListNode formalParameters, boolean external, BaseNode body) {
        super( ctx );
        this.formalParameters = new ArrayList<>(  );
        this.external = external;
        this.body = body;
        if( formalParameters != null ) {
            for( BaseNode name : formalParameters.getElements() ) {
                this.formalParameters.add( (NameDefNode) name );
            }
        }
    }

    public List<NameDefNode> getFormalParameters() {
        return formalParameters;
    }

    public void setFormalParameters(List<NameDefNode> formalParameters) {
        this.formalParameters = formalParameters;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(boolean external) {
        this.external = external;
    }

    public BaseNode getBody() {
        return body;
    }

    public void setBody(BaseNode body) {
        this.body = body;
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        if( external ) {
            throw new UnsupportedOperationException( " not implemented yet " );
        } else {
            List<String> params = formalParameters.stream().map( p -> p.evaluate( ctx ) ).collect( Collectors.toList() );
            return new CustomFEELFunction( "<anonymous>", params, body );
        }
    }
}
