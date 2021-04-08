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

package org.drools.modelcompiler.builder.generator.visitor;

import java.util.Optional;

import com.github.javaparser.ast.expr.Expression;
import org.drools.compiler.lang.descr.EvalDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.SingleDrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.expression.EvalExpressionBuilder;

public class EvalVisitor {

    private final RuleContext context;
    private final PackageModel packageModel;

    public EvalVisitor(RuleContext context, PackageModel packageModel) {
        this.context = context;
        this.packageModel = packageModel;
    }

    public void visit(EvalDescr descr) {
        String expression = descr.getContent().toString();
        DrlxParseResult drlxParseResult = new ConstraintParser(context, packageModel).drlxParse(null, null, expression);

        drlxParseResult.accept(drlxParseSuccess -> {
            SingleDrlxParseSuccess singleResult = (SingleDrlxParseSuccess) drlxParseResult;
            Expression rewriteExprAsLambdaWithoutThisParam = DrlxParseUtil.generateLambdaWithoutParameters(singleResult.getUsedDeclarations(), singleResult.getExpr(), true, Optional.empty(), context);
            singleResult.setExpr(rewriteExprAsLambdaWithoutThisParam); // rewrites the DrlxParserResult expr as directly the lambda to use
            singleResult.setStatic(true);
            new EvalExpressionBuilder(context).processExpression(singleResult);
        });

    }
}
