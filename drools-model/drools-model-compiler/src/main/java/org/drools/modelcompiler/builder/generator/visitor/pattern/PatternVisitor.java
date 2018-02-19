package org.drools.modelcompiler.builder.generator.visitor.pattern;

import java.util.List;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.javaparser.ast.body.MethodDeclaration;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getClassFromContext;
import static org.drools.modelcompiler.builder.generator.QueryGenerator.toQueryDef;

public class PatternVisitor {

    private final RuleContext context;
    private final PackageModel packageModel;
    private final boolean isPattern;

    public PatternVisitor(RuleContext context, PackageModel packageModel, boolean isPattern) {
        this.context = context;
        this.packageModel = packageModel;
        this.isPattern = isPattern;
    }

    public DSLNode visit(PatternDescr pattern) {
        String className = pattern.getObjectType();
        List<? extends BaseDescr> constraintDescrs = pattern.getConstraint().getDescrs();

        String queryName = "query_" + pattern.getObjectType();
        final MethodDeclaration queryMethod = packageModel.getQueryMethod(queryName);
        // Expression is a query, get bindings from query parameter type
        if (queryMethod != null) {
            return new Query(context, packageModel, pattern, constraintDescrs, queryName);
        }

        String queryDef = toQueryDef(pattern.getObjectType());
        if (packageModel.getQueryDefWithType().containsKey(queryDef)) {
            return new QueryCall(context, packageModel, pattern, queryDef);
        }

        if (pattern.getIdentifier() == null && pattern.getObjectType().equals("Object") && pattern.getSource() instanceof AccumulateDescr) {
            return new Accumulate(context, packageModel, pattern, ((AccumulateDescr) pattern.getSource()), constraintDescrs);
        }

        final boolean allConstraintsPositional = areAllConstraintsPositional(constraintDescrs);
        final Class<?> patternType = getClassFromContext(context.getTypeResolver(), className);
        if(isPattern) {
            return new PatternDSLPattern(context, packageModel, pattern, constraintDescrs, patternType, allConstraintsPositional);
        } else {
            return new FlowDSLPattern(context, packageModel, pattern, constraintDescrs, patternType, allConstraintsPositional);
        }
    }



    private boolean areAllConstraintsPositional(List<? extends BaseDescr> constraintDescrs) {
        return !constraintDescrs.isEmpty() && constraintDescrs.stream()
                .allMatch(c -> c instanceof ExprConstraintDescr
                        && ((ExprConstraintDescr) c).getType().equals(ExprConstraintDescr.Type.POSITIONAL));
    }
}
