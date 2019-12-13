package org.drools.modelcompiler.builder.generator.visitor.pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;
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
                    .findAll( DrlNameExpr.class ).stream().map(PrintUtil::printConstraint).collect(toSet() );
            for (AccumulateDescr.AccumulateFunctionCallDescr accFunc : source.getFunctions()) {
                if ( exprIds.contains( accFunc.getBind() ) ) {
                    constraintsByVar.computeIfAbsent( accFunc.getBind(), s -> new ArrayList<>() ).add(constraint);
                    break;
                }
            }
        }

        constraintsByVar.forEach( (id, constraints) -> {
            pattern.setIdentifier(id);
            new PatternDSLPattern(context, packageModel, pattern, constraints, null).buildPattern();
        });
        pattern.setIdentifier(null);
    }
}
