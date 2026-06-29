/*
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

import org.drools.drl.ast.descr.AccumulateDescr;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.CollectDescr;
import org.drools.drl.ast.descr.ConditionalBranchDescr;
import org.drools.drl.ast.descr.DescrVisitor;
import org.drools.drl.ast.descr.EvalDescr;
import org.drools.drl.ast.descr.ExistsDescr;
import org.drools.drl.ast.descr.ForallDescr;
import org.drools.drl.ast.descr.FromDescr;
import org.drools.drl.ast.descr.GroupByDescr;
import org.drools.drl.ast.descr.NamedConsequenceDescr;
import org.drools.drl.ast.descr.NotDescr;
import org.drools.drl.ast.descr.OrDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.ast.descr.PatternSourceDescr;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.generator.DeclarationSpec;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.visitor.accumulate.AccumulateVisitor;
import org.drools.model.codegen.execmodel.generator.visitor.accumulate.GroupByVisitor;
import org.drools.model.codegen.execmodel.generator.visitor.pattern.PatternDSL;
import org.drools.model.codegen.execmodel.generator.visitor.pattern.PatternVisitor;

import static org.drools.model.codegen.execmodel.generator.DslMethodNames.EXISTS_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.FORALL_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.NOT_CALL;
import static org.drools.model.codegen.execmodel.util.lambdareplace.ReplaceTypeInLambda.replaceTypeInExprLambdaAndIndex;

public class ModelGeneratorVisitor implements DescrVisitor {

    private final RuleContext context;
    private final PackageModel packageModel;

    public ModelGeneratorVisitor(RuleContext context, PackageModel packageModel) {
        this.context = context;
        this.packageModel = packageModel;
    }

    @Override
    public void visit(BaseDescr descr) {
        throw new UnsupportedOperationException("Unknown descr" + descr);
    }

    @Override
    public void visit(AccumulateDescr descr) {
       throw new UnsupportedOperationException("AccumulateDescr are always nested in pattern and need their context anyway");
    }

    @Override
    public void visit(AndDescr descr) {
        new AndVisitor(this, context).visit(descr);
    }

    @Override
    public void visit(NotDescr descr) {
        new ConditionalElementVisitor(this, context).visit(descr, NOT_CALL);
    }

    @Override
    public void visit(ExistsDescr descr) {
        new ConditionalElementVisitor(this, context).visit(descr, EXISTS_CALL);
    }

    @Override
    public void visit(ForallDescr descr) {
        new ConditionalElementVisitor(this, context).visit(descr, FORALL_CALL);
    }

    @Override
    public void visit(OrDescr descr) {
        new OrVisitor(this, context).visit(descr);
    }

    @Override
    public void visit(EvalDescr descr) {
        new EvalVisitor(context, packageModel).visit(descr);
    }

    @Override
    public void visit(FromDescr descr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(NamedConsequenceDescr descr) {
        new NamedConsequenceVisitor(context, packageModel).visit(descr);
    }

    @Override
    public void visit(ConditionalBranchDescr descr) {
        new NamedConsequenceVisitor(context, packageModel).visit(descr);
    }

    @Override
    public void visit(PatternDescr descr) {
        final PatternSourceDescr patternSource = descr.getSource();
        if (patternSource instanceof CollectDescr) {
            new FromCollectVisitor(this).transformFromCollectToCollectList(descr, (CollectDescr) patternSource);
        } else if (patternSource instanceof GroupByDescr) {
            new GroupByVisitor(this, context, packageModel).visit((GroupByDescr) patternSource, descr);
            new PatternVisitor(context, packageModel).visit(descr).buildPattern();
        } else {
            if (patternSource instanceof AccumulateDescr) {
                AccumulateDescr accSource = (AccumulateDescr) patternSource;
                if (accSource.getFunctions().isEmpty() || accSource.getFunctions().get(0).getBind() == null) {
                    new PatternVisitor(context, packageModel).visit(descr).buildPattern();
                    new AccumulateVisitor(this, context, packageModel).visit(accSource, descr );
                } else {
                    new AccumulateVisitor(this, context, packageModel).visit(accSource, descr );
                    new PatternVisitor(context, packageModel).visit(descr).buildPattern();
                    this.retypeInlineAccumulateResultConstraints(accSource);
                }
            } else {
                new PatternVisitor(context, packageModel).visit(descr).buildPattern();
            }
        }
    }

    /**
     * For an inline-binding accumulate (e.g. "$list : collectList(...) ; $list.size > 0"), the result
     * pattern keeps its Object type node for matching, but a result constraint that uses the binding
     * (here "$list.size > 0") must be typed against the accumulate function's result type. The
     * constraint lambdas are only built by the PatternVisitor above, so retype the binding references
     * in them now (without altering the pattern's object type / matching). The "List(...) from
     * accumulate(...)" form has no function binding and is unaffected.
     */
    private void retypeInlineAccumulateResultConstraints(AccumulateDescr accSource) {
        for (AccumulateDescr.AccumulateFunctionCallDescr function : accSource.getFunctions()) {
            if (function.getBind() == null) {
                continue;
            }
            context.getTypedDeclarationById(function.getBind())
                   .map(DeclarationSpec.class::cast)
                   .ifPresent(declaration -> {
                       Class<?> resultType = declaration.getDeclarationClass();
                       if (resultType != null && resultType != Object.class) {
                           context.getExpressions().forEach(expression ->
                                   replaceTypeInExprLambdaAndIndex(declaration.getBindingId(), resultType, expression));
                       }
                   });
        }
    }

    public boolean initPattern(PatternDescr descr) {
        DSLNode dslNode = new PatternVisitor(context, packageModel).visit(descr);
        if (dslNode instanceof PatternDSL) {
            (( PatternDSL ) dslNode).initPattern();
            return true;
        }
        return false;
    }
}
