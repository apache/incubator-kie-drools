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

package org.kie.dmn.feel.lang.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.FEELFunction.Param;
import org.kie.dmn.feel.runtime.functions.CustomFEELFunction;
import org.kie.dmn.feel.util.Msg;

public class FunctionDefNode extends BaseNode {

    private static final String ANONYMOUS = "<anonymous>";
    private static final RegExp METHOD_PARSER = RegExp.compile("(.+)\\((.*)\\)");
    private static final RegExp PARAMETER_PARSER = RegExp.compile("([^, ]+)");

    private List<FormalParameterNode> formalParameters;
    private boolean external;
    private BaseNode body;

    public FunctionDefNode(final ParserRuleContext ctx,
                           final ListNode formalParameters,
                           final boolean external, BaseNode body) {
        super(ctx);
        this.formalParameters = new ArrayList<>();
        this.external = external;
        this.body = body;
        if (formalParameters != null) {
            for (final BaseNode name : formalParameters.getElements()) {
                this.formalParameters.add((FormalParameterNode) name);
            }
        }
    }

    public List<FormalParameterNode> getFormalParameters() {
        return formalParameters;
    }

    public void setFormalParameters(final List<FormalParameterNode> formalParameters) {
        this.formalParameters = formalParameters;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(final boolean external) {
        this.external = external;
    }

    public BaseNode getBody() {
        return body;
    }

    public void setBody(final BaseNode body) {
        this.body = body;
    }

    @Override
    public Object evaluate(final EvaluationContext ctx) {
        final List<Param> params = formalParameters.stream().map(p -> p.evaluate(ctx)).collect(Collectors.toList());
        if (external) {
            try {
                // creating a simple algorithm to find the method in java
                // without using any external libraries in this initial implementation
                final Map<String, Object> conf = (Map<String, Object>) this.body.evaluate(ctx);
                final Map<String, Object> java = (Map<String, Object>) conf.get("java");
                if (java != null) {
                    // this is a java function
                    String clazzName = (String) java.get("class");
                    String methodSignature = (String) java.get("method signature");
                    if (clazzName != null && methodSignature != null) {
                        ctx.notifyEvt(astEvent(Severity.ERROR, Msg.createMessage(Msg.PARAMETER_COUNT_MISMATCH_ON_FUNCTION_DEFINITION, getText())));
                        return null;
                    }
                }
                ctx.notifyEvt(astEvent(Severity.ERROR, Msg.createMessage(Msg.UNABLE_TO_FIND_EXTERNAL_FUNCTION_AS_DEFINED_BY, getText())));
            } catch (Exception e) {
                ctx.notifyEvt(astEvent(Severity.ERROR, Msg.createMessage(Msg.ERROR_RESOLVING_EXTERNAL_FUNCTION_AS_DEFINED_BY, getText()), e));
            }
            return null;
        } else {
            return new CustomFEELFunction(ANONYMOUS, params, body, ctx.current()); // DMN spec, 10.3.2.13.2 User-defined functions: FEEL functions are lexical closures
        }
    }

    public static String[] parseMethod(final String signature) {
        final MatchResult m = METHOD_PARSER.exec(signature);
        if (m != null) {
            String[] result = new String[2];
            result[0] = m.getGroup(1);
            result[1] = m.getGroup(2);
            return result;
        }
        return null;
    }

    public static String[] parseParams(final String params) {
        final List<String> ps = new ArrayList<>();
        if (params.trim().length() > 0) {
            final MatchResult m = PARAMETER_PARSER.exec(params.trim());
            if (m != null) {
                for (int i = 0; i < m.getGroupCount(); i++) {
                    ps.add(m.getGroup(i).trim());
                }
            }
        }
        return ps.toArray(new String[ps.size()]);
    }

    private static Class<?> convertPrimitiveNameToType(final String typeName) {
        if (typeName.equals("int")) {
            return int.class;
        }
        if (typeName.equals("boolean")) {
            return boolean.class;
        }
        if (typeName.equals("char")) {
            return char.class;
        }
        if (typeName.equals("byte")) {
            return byte.class;
        }
        if (typeName.equals("short")) {
            return short.class;
        }
        if (typeName.equals("float")) {
            return float.class;
        }
        if (typeName.equals("long")) {
            return long.class;
        }
        if (typeName.equals("double")) {
            return double.class;
        }
        return null;
    }

    @Override
    public Type getResultType() {
        return BuiltInType.FUNCTION;
    }

    @Override
    public ASTNode[] getChildrenNode() {
        final ASTNode[] children = new ASTNode[formalParameters.size() + 1];
        System.arraycopy(formalParameters.toArray(new ASTNode[]{}), 0, children, 0, formalParameters.size());
        children[children.length - 1] = body;
        return children;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
