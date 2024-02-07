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
package org.drools.model.codegen.execmodel.generator.visitor.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.drl.ast.descr.AccumulateDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.generator.DrlxParseUtil;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.visitor.DSLNode;
import org.drools.mvel.parser.ast.expr.DrlNameExpr;
import org.drools.mvel.parser.printer.PrintUtil;

import static java.util.stream.Collectors.toSet;

class PatternAccumulateConstraint implements DSLNode {

    private final RuleContext context;
    private final PackageModel packageModel;
    private final PatternDescr pattern;
    private final AccumulateDescr source;
    private final List<? extends BaseDescr> constraintDescrs;

    public PatternAccumulateConstraint( RuleContext context, PackageModel packageModel, PatternDescr pattern, AccumulateDescr source, List<? extends BaseDescr> constraintDescrs) {
        this.context = context;
        this.packageModel = packageModel;
        this.pattern = pattern;
        this.source = source;
        this.constraintDescrs = constraintDescrs;
    }

    @Override
    public void buildPattern() {
        Map<String, List<BaseDescr>> constraintsByVar = new HashMap<>();
        for (BaseDescr constraint : constraintDescrs) {
            Set<String> exprIds = DrlxParseUtil.parseExpression( constraint.getText() ).getExpr()
                    .findAll( DrlNameExpr.class ).stream().map(PrintUtil::printNode).collect(toSet() );
            for (AccumulateDescr.AccumulateFunctionCallDescr accFunc : source.getFunctions()) {
                if ( exprIds.contains( accFunc.getBind() ) ) {
                    constraintsByVar.computeIfAbsent( accFunc.getBind(), s -> new ArrayList<>() ).add(constraint);
                    break;
                }
            }
        }

        constraintsByVar.forEach( (id, constraints) -> {
            pattern.setIdentifier(id);
            new ClassPatternDSL(context, packageModel, pattern, constraints, null).buildPattern();
        });
        pattern.setIdentifier(null);
    }
}
