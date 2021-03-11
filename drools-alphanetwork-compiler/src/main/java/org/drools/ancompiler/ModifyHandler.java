/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.ancompiler;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.core.reteoo.Sink;

import static com.github.javaparser.ast.NodeList.nodeList;

public class ModifyHandler extends PropagatorCompilerHandler {

    public ModifyHandler(String factClassName, boolean alphaNetContainsHashedField) {
        super(alphaNetContainsHashedField, factClassName);
    }

    @Override
    protected Statement propagateMethod(Sink sink) {
        Statement modifyStatement = StaticJavaParser.parseStatement("ALPHATERMINALNODE.modifyObject(handle, modifyPreviousTuples, context, wm);");
        replaceNameExpr(modifyStatement, "ALPHATERMINALNODE", getVariableName(sink));
        return modifyStatement;
    }

    @Override
    protected NodeList<Parameter> methodParameters() {
        return nodeList(new Parameter(factHandleType(), FACT_HANDLE_PARAM_NAME),
                        new Parameter(modifyPreviousTuplesType(), MODIFY_PREVIOUS_TUPLE_PARAM_NAME),
                        new Parameter(propagationContextType(), PROP_CONTEXT_PARAM_NAME),
                        new Parameter(workingMemoryType(), WORKING_MEMORY_PARAM_NAME));
    }

    @Override
    protected NodeList<Expression> arguments() {
        return nodeList(new NameExpr(FACT_HANDLE_PARAM_NAME),
                        new NameExpr(MODIFY_PREVIOUS_TUPLE_PARAM_NAME),
                        new NameExpr(PROP_CONTEXT_PARAM_NAME),
                        new NameExpr(WORKING_MEMORY_PARAM_NAME));
    }


    @Override
    protected String propagateMethodName() {
        return "propagateModifyObject";
    }
}
