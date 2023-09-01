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

import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.BaseDescr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.model.codegen.execmodel.generator.RuleContext;

import static org.drools.model.codegen.execmodel.generator.DslMethodNames.AND_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.createDslTopLevelMethod;

public class AndVisitor {

    private final ModelGeneratorVisitor visitor;
    private final RuleContext context;

    public AndVisitor(ModelGeneratorVisitor visitor, RuleContext context) {
        this.visitor = visitor;
        this.context = context;
    }

    public void visit(AndDescr descr) {
        int exprStackSize = this.context.getExprPointerLevel();

        // if it's the first (implied) `and` wrapping the first level of patterns, skip adding it to the DSL.
        if (exprStackSize != 1) {
            final MethodCallExpr andDSL = createDslTopLevelMethod(AND_CALL);
            this.context.addExpression(andDSL);
            this.context.pushExprPointer(andDSL::addArgument);
            exprStackSize++;
        }

        this.context.setParentDescr( descr );
        for (BaseDescr subDescr : descr.getDescrs()) {
            subDescr.accept(visitor);
        }

        if (exprStackSize != this.context.getExprPointerLevel()) {
            throw new RuntimeException( "Non paired number of push and pop expression on context stack in " + descr );
        }

        if (exprStackSize != 1) {
            this.context.popExprPointer();
        }
    }
}
