/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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

    public AtLiteralNode(StringNode stringLiteral, String text) {
        this.stringLiteral = stringLiteral;
        this.setText(text);
    }

    public StringNode getStringLiteral() {
        return stringLiteral;
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        String value = (String) stringLiteral.evaluate(ctx);
        String functionName = fromAtValue(value).fnName;
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
            return fromAtValue(value).type;
        } catch (Exception e) {
            return BuiltInType.UNKNOWN;
        }
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    private static final TypeAndFn AT_UNKNOWN = new TypeAndFn(BuiltInType.UNKNOWN, null);
    private static final TypeAndFn AT_DATE = new TypeAndFn(BuiltInType.DATE, "date");
    private static final TypeAndFn AT_TIME = new TypeAndFn(BuiltInType.TIME, "time");
    private static final TypeAndFn AT_DATEANDTIME = new TypeAndFn(BuiltInType.DATE_TIME, "date and time");
    private static final TypeAndFn AT_DURATION = new TypeAndFn(BuiltInType.DURATION, "duration");

    public static TypeAndFn fromAtValue(String value) {
        int indexOfAt = value.indexOf("@");
        final String literalBeforeAt = indexOfAt >= 0 ? value.substring(0, indexOfAt) : value ;
        if (literalBeforeAt.startsWith("P") || literalBeforeAt.startsWith("-P")) {
            return AT_DURATION;
        } else if (literalBeforeAt.contains("T")) {
            return AT_DATEANDTIME;
        } else if (literalBeforeAt.contains(":")) {
            return AT_TIME;
        } else if (literalBeforeAt.contains("-")) {
            return AT_DATE;
        }
        return AT_UNKNOWN;
    }

    public static class TypeAndFn {

        public final Type type;
        public final String fnName;

        public TypeAndFn(Type type, String fnName) {
            this.type = type;
            this.fnName = fnName;
        }

    }
}
