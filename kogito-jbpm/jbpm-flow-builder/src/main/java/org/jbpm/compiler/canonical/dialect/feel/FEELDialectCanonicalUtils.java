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

package org.jbpm.compiler.canonical.dialect.feel;

import java.util.Collections;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.jbpm.process.builder.dialect.feel.FeelCompilationException;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.impl.FeelErrorEvaluatorListener;
import org.jbpm.process.instance.impl.FeelReturnValueEvaluator;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.impl.ConnectionRef;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.parser.feel11.profiles.KieExtendedFEELProfile;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

public class FEELDialectCanonicalUtils {

    public static ObjectCreationExpr buildFEELReturnValueEvaluator(VariableScope variableScope, Entry<ConnectionRef, Constraint> entry) {
        verifyFEELbyCompilingExpression(variableScope, entry);
        StringLiteralExpr feelConstraintString = new StringLiteralExpr();
        feelConstraintString.setString(entry.getValue().getConstraint());
        return new ObjectCreationExpr(null,
                StaticJavaParser.parseClassOrInterfaceType(FeelReturnValueEvaluator.class.getCanonicalName()),
                new NodeList<>(feelConstraintString));
    }

    /**
     * Instead of throwing a generic JavaParser compilation error (atm happens for invalid expression of dialect=JAVA)
     * use the FEEL compiler capabilities to verify if mere compilation of the FEEL expression may contain any error.
     */
    private static void verifyFEELbyCompilingExpression(VariableScope variableScope, Entry<ConnectionRef, Constraint> entry) {
        FEEL feel = FEEL.newInstance(Collections.singletonList(new KieExtendedFEELProfile()));
        FeelErrorEvaluatorListener feelErrorListener = new FeelErrorEvaluatorListener();
        feel.addListener(feelErrorListener);
        CompilerContext cc = feel.newCompilerContext();
        for (Variable v : variableScope.getVariables()) {
            cc.addInputVariable(v.getName(), null);
        }
        feel.compile(entry.getValue().getConstraint(), cc);
        if (!feelErrorListener.getErrorEvents().isEmpty()) {
            String exceptionMessage = feelErrorListener.getErrorEvents().stream().map(FeelReturnValueEvaluator::eventToMessage).collect(Collectors.joining(", "));
            throw new FeelCompilationException(exceptionMessage);
        }
    }

    private FEELDialectCanonicalUtils() {
        // only static utility methods.
    }

}
