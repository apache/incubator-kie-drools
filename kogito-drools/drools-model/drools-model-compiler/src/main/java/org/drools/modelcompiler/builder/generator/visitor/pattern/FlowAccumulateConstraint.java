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

package org.drools.modelcompiler.builder.generator.visitor.pattern;

import java.util.List;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.SingleDrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.expression.FlowExpressionBuilder;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;

class FlowAccumulateConstraint implements DSLNode {

    private final RuleContext context;
    private final PackageModel packageModel;
    private final PatternDescr pattern;
    private final AccumulateDescr source;
    private final List<? extends BaseDescr> constraintDescrs;

    public FlowAccumulateConstraint( RuleContext context, PackageModel packageModel, PatternDescr pattern, AccumulateDescr source, List<? extends BaseDescr> constraintDescrs) {
        this.context = context;
        this.packageModel = packageModel;
        this.pattern = pattern;
        this.source = source;
        this.constraintDescrs = constraintDescrs;
    }

    @Override
    public void buildPattern() {
        for (BaseDescr constraint : constraintDescrs) {
            String expression = constraint.toString();
            final DrlxParseResult drlxParseResult = new ConstraintParser(context, packageModel)
                    .drlxParse(null, null, expression, false);

            drlxParseResult.accept(success -> {
                (( SingleDrlxParseSuccess ) success).setSkipThisAsParam(true);
                new FlowExpressionBuilder(context).processExpression(success );
            });
        }
    }
}
