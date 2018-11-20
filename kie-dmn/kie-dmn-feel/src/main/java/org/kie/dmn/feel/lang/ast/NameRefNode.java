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
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.runtime.events.SyntaxErrorEvent;
import org.kie.dmn.feel.runtime.events.UnknownVariableErrorEvent;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.feel.util.Msg;

public class NameRefNode
        extends BaseNode {

    private Type resultType;

    public NameRefNode(ParserRuleContext ctx, Type type) {
        super( ctx );
        this.resultType = type;
    }

    public NameRefNode(ParserRuleContext ctx, String text, Type type) {
        super( ctx );
        this.resultType = type;
        this.setText(text);
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        String varName = EvalHelper.normalizeVariableName( getText() );
        if( ! ctx.isDefined( varName ) ) {
            ctx.notifyEvt( astEvent( FEELEvent.Severity.ERROR, Msg.createMessage( Msg.UNKNOWN_VARIABLE_REFERENCE, getText()), null) );
            return null;
        }
        return ctx.getValue( varName );
    }

    @Override
    public Type getResultType() {
        return resultType;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
