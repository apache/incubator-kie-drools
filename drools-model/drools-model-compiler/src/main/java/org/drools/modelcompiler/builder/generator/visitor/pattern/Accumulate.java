package org.drools.modelcompiler.builder.generator.visitor.pattern;

import java.util.List;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.ModelGenerator;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;

class Accumulate implements DSLNode {

    private final RuleContext context;
    private final PackageModel packageModel;
    private final PatternDescr pattern;
    private final AccumulateDescr source;
    private final List<? extends BaseDescr> constraintDescrs;

    public Accumulate(RuleContext context, PackageModel packageModel, PatternDescr pattern, AccumulateDescr source, List<? extends BaseDescr> constraintDescrs) {
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
                success.setSkipThisAsParam(true);
                ModelGenerator.processExpression(context, success );
            });
        }
    }
}
