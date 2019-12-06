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
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.util.Msg;

public class AtLiteralNode
        extends BaseNode {

    StringNode stringLiteral;

    public AtLiteralNode(ParserRuleContext ctx, StringNode stringLiteral) {
        super( ctx );
        this.stringLiteral = stringLiteral;
    }

    public StringNode getStringLiteral() {
        return stringLiteral;
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        String value = (String) stringLiteral.evaluate(ctx);
        String functionName = null;
        if (value.startsWith("P")) {
            functionName = "duration";
        } else if (value.contains("T")) {
            functionName = "date and time";
        } else if (value.contains(":")) {
            functionName = "time";
        } else if (value.contains("-")) {
            functionName = "date";
        }
        if (functionName == null) {
            ctx.notifyEvt(astEvent(Severity.ERROR, Msg.createMessage(Msg.MALFORMED_AT_LITERAL, getText())));
            return null;
        }
        Object function = ctx.getValue(functionName);
        if (function instanceof FEELFunction) {
            FEELFunction f = (FEELFunction) function;
            return f.invokeReflectively(ctx, new Object[]{value});
        } else {
            ctx.notifyEvt(astEvent(Severity.ERROR, Msg.createMessage(Msg.MALFORMED_AT_LITERAL, getText())));
        }
        return null;
    }

    @Override
    public Type getResultType() {
        try {
            String value = (String) stringLiteral.evaluate(null);
            if (value.startsWith("P")) {
                return BuiltInType.DURATION;
            } else if (value.contains("T")) {
                return BuiltInType.DATE_TIME;
            } else if (value.contains(":")) {
                return BuiltInType.TIME;
            } else if (value.contains("-")) {
                return BuiltInType.DATE;
            }
            return BuiltInType.UNKNOWN;
        } catch (Exception e) {
            return BuiltInType.UNKNOWN;
        }
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

}
