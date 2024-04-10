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

import org.drools.base.base.ValueType;
import org.drools.compiler.rule.builder.EvaluatorDefinition;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.model.functions.Operator;
import org.drools.model.codegen.execmodel.generator.RuleContext;



public class CustomOperatorSpec extends NativeOperatorSpec {
    public static final CustomOperatorSpec INSTANCE = new CustomOperatorSpec();

    /*
     Three different cases for generating code:
       1) if EvaluatorDefinition evalDef contains the method getInstance and implements Operator.SingleValue use the getInstance(opName) and do not create a CustomOperatorWrapper
       2) if EvaluatorDefinition evalDef contains the method getInstance use getInstance(opName) vs creating a new instance of the class
       3) none of the above :   new CustomOperatorWrapper( .. new evalDef)
    */
    @Override
    protected Operator addOperatorArgument( RuleContext context, MethodCallExpr methodCallExpr, String opName ) {
        EvaluatorDefinition evalDef = context.getEvaluatorDefinition( opName );
        if (evalDef == null) {
            throw new RuntimeException( "Unknown custom operator: " + opName );
        }

        String arg;
        try {
            evalDef.getClass().getMethod("getInstance",String.class);
            boolean needWrapper = !Operator.SingleValue.class.isAssignableFrom(evalDef.getClass());
            if (needWrapper) {
                // case 2
                arg = "new " + CustomOperatorWrapper.class.getCanonicalName() + "( "+ evalDef.getClass().getCanonicalName() + ".getInstance(\"" + opName+ "\").getEvaluator(" +
                        ValueType.class.getCanonicalName() + ".OBJECT_TYPE, \"" + opName + "\", false, null), \"" + opName + "\")";
            } else {
                // case 1
                arg = evalDef.getClass().getCanonicalName()  + ".getInstance(\"" + opName+ "\")";
            }
        } catch (NoSuchMethodException e) {
            // case 3
            arg = "new " + CustomOperatorWrapper.class.getCanonicalName() + "( new " + evalDef.getClass().getCanonicalName() + "().getEvaluator(" +
                    ValueType.class.getCanonicalName() + ".OBJECT_TYPE, \"" + opName + "\", false, null), \"" + opName + "\")";
        }

        methodCallExpr.addArgument( arg );
        return null;
    }
}
