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

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.CollectDescr;
import org.drools.compiler.lang.descr.ConditionalBranchDescr;
import org.drools.compiler.lang.descr.DescrVisitor;
import org.drools.compiler.lang.descr.EvalDescr;
import org.drools.compiler.lang.descr.ExistsDescr;
import org.drools.compiler.lang.descr.ForallDescr;
import org.drools.compiler.lang.descr.FromDescr;
import org.drools.compiler.lang.descr.NamedConsequenceDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.PatternSourceDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.visitor.accumulate.AccumulateVisitor;
import org.drools.modelcompiler.builder.generator.visitor.pattern.PatternDSL;
import org.drools.modelcompiler.builder.generator.visitor.pattern.PatternVisitor;

import static org.drools.modelcompiler.builder.generator.DslMethodNames.EXISTS_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.FORALL_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.NOT_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.OR_CALL;

public class ModelGeneratorVisitor implements DescrVisitor {

    private final AccumulateVisitor accumulateVisitor;
    private final AndVisitor andVisitor;
    private final ConditionalElementVisitor conditionalElementVisitor;
    private final OrVisitor orVisitor;
    private final EvalVisitor evalVisitor;
    private final NamedConsequenceVisitor namedConsequenceVisitor;
    private final PatternVisitor patternVisitor;
    private final FromCollectVisitor fromCollectVisitor;

    public ModelGeneratorVisitor(RuleContext context, PackageModel packageModel) {
        accumulateVisitor = new AccumulateVisitor(this, context, packageModel);
        andVisitor = new AndVisitor(this, context);
        conditionalElementVisitor = new ConditionalElementVisitor(this, context);
        orVisitor = new OrVisitor(this, context);
        evalVisitor = new EvalVisitor(context, packageModel);
        namedConsequenceVisitor = new NamedConsequenceVisitor(context, packageModel);
        patternVisitor = new PatternVisitor(context, packageModel);
        fromCollectVisitor = new FromCollectVisitor(this);
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
        andVisitor.visit(descr);
    }

    @Override
    public void visit(NotDescr descr) {
        conditionalElementVisitor.visit(descr, NOT_CALL);
    }

    @Override
    public void visit(ExistsDescr descr) {
        conditionalElementVisitor.visit(descr, EXISTS_CALL);
    }

    @Override
    public void visit(ForallDescr descr) {
        conditionalElementVisitor.visit(descr, FORALL_CALL);
    }

    @Override
    public void visit(OrDescr descr) {
        orVisitor.visit(descr, OR_CALL);
    }

    @Override
    public void visit(EvalDescr descr) {
        evalVisitor.visit(descr);
    }

    @Override
    public void visit(FromDescr descr) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(NamedConsequenceDescr descr) {
        namedConsequenceVisitor.visit(descr);
    }

    @Override
    public void visit(ConditionalBranchDescr descr) {
        namedConsequenceVisitor.visit(descr);
    }

    @Override
    public void visit(PatternDescr descr) {
        final PatternSourceDescr patternSource = descr.getSource();
        if (patternSource instanceof CollectDescr) {
            fromCollectVisitor.trasformFromCollectToCollectList(descr, (CollectDescr) patternSource);
        } else {
            if (patternSource instanceof AccumulateDescr) {
                AccumulateDescr accSource = (AccumulateDescr) patternSource;
                if (accSource.getFunctions().isEmpty() || accSource.getFunctions().get(0).getBind() == null) {
                    patternVisitor.visit(descr).buildPattern();
                    accumulateVisitor.visit(accSource, descr );
                } else {
                    accumulateVisitor.visit(accSource, descr );
                    patternVisitor.visit(descr).buildPattern();
                }
            } else {
                patternVisitor.visit(descr).buildPattern();
            }
        }
    }

    public boolean initPattern(PatternDescr descr) {
        DSLNode dslNode = patternVisitor.visit(descr);
        if (dslNode instanceof PatternDSL) {
            (( PatternDSL ) dslNode).initPattern();
            return true;
        }
        return false;
    }
}
