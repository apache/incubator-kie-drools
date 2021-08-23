/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator.operatorspec;

import org.drools.core.base.ValueType;
import org.drools.core.base.evaluators.EvaluatorDefinition;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.model.functions.Operator;
import org.drools.modelcompiler.builder.generator.RuleContext;

public class CustomOperatorSpec extends NativeOperatorSpec {
    public static final CustomOperatorSpec INSTANCE = new CustomOperatorSpec();

    @Override
    protected Operator addOperatorArgument( RuleContext context, MethodCallExpr methodCallExpr, String opName ) {
        EvaluatorDefinition evalDef = context.getEvaluatorDefinition( opName );
        if (evalDef == null) {
            throw new RuntimeException( "Unknown custom operator: " + opName );
        }

        String arg = "new " + CustomOperatorWrapper.class.getCanonicalName() + "( new " + evalDef.getClass().getCanonicalName() + "().getEvaluator(" +
                ValueType.class.getCanonicalName() + ".OBJECT_TYPE, \"" + opName + "\", false, null), \"" + opName + "\")";

        methodCallExpr.addArgument( arg );
        return null;
    }
}
