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
package org.drools.model.codegen.execmodel.generator.visitor;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ConditionalElementDescr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.model.codegen.execmodel.generator.RuleContext;

import static org.drools.model.codegen.execmodel.generator.DslMethodNames.AND_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.OR_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.createDslTopLevelMethod;

public class OrVisitor {

    final ModelGeneratorVisitor modelGeneratorVisitor;
    final RuleContext context;

    public OrVisitor(ModelGeneratorVisitor modelGeneratorVisitor, RuleContext context) {
        this.modelGeneratorVisitor = modelGeneratorVisitor;
        this.context = context;
    }

    public void visit(ConditionalElementDescr descr) {
        final MethodCallExpr ceDSL = createDslTopLevelMethod(OR_CALL);
        context.addExpression(ceDSL);

        for (BaseDescr subDescr : descr.getDescrs()) {
            final MethodCallExpr andDSL = createDslTopLevelMethod(AND_CALL);
            context.setNestedInsideOr(true);
            context.pushExprPointer(andDSL::addArgument);
            subDescr.accept(modelGeneratorVisitor);
            context.popExprPointer();
            ceDSL.addArgument( andDSL );
            context.setNestedInsideOr(false);
        }


        for(String k : context.getBindingOr()) {
            if(context.getBindingOr().sizeFor(k) != descr.getDescrs().size())  {
                context.getUnusableOrBinding().add(k);
            }
        }
    }
}
