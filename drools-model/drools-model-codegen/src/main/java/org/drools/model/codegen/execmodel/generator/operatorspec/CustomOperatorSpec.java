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
package org.drools.model.codegen.execmodel.generator.operatorspec;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.base.base.ValueType;
import org.drools.compiler.rule.builder.EvaluatorDefinition;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.functions.Operator;

public class CustomOperatorSpec extends NativeOperatorSpec {

    private Set<String> generatedOperators = new HashSet<>();

    private List<String> operatorDeclarations = new ArrayList<>();

    @Override
    protected Operator addOperatorArgument( RuleContext context, MethodCallExpr methodCallExpr, String opName ) {
        EvaluatorDefinition evalDef = context.getEvaluatorDefinition( opName );
        if (evalDef == null) {
            throw new RuntimeException( "Unknown custom operator: " + opName );
        }

        String operatorInstance = "OPERATOR_" + opName + "_INSTANCE";

        if (generatedOperators.add(opName)) {
            String operatorFieldDeclaration = "public static final " + Operator.class.getCanonicalName() + ".SingleValue<Object, Object> " + operatorInstance +
                    " = new " + CustomOperatorWrapper.class.getCanonicalName() + "( new " + evalDef.getClass().getCanonicalName() + "().getEvaluator(" +
                    ValueType.class.getCanonicalName() + ".OBJECT_TYPE, \"" + opName + "\", false, null), \"" + opName + "\");";
            operatorDeclarations.add(operatorFieldDeclaration);
        }

        methodCallExpr.addArgument( context.getPackageModel().getRulesFileNameWithPackage() + "." + operatorInstance );
        return null;
    }

    public List<String> getOperatorDeclarations() {
        return operatorDeclarations;
    }
}
