package org.drools.model.codegen.execmodel.generator.visitor.pattern;

import java.util.List;

import org.drools.drl.ast.descr.AccumulateDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.errors.InvalidExpressionErrorResult;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.model.codegen.execmodel.generator.visitor.DSLNode;
import org.drools.model.codegen.execmodel.util.PatternUtil;

import static org.drools.model.codegen.execmodel.generator.QueryGenerator.QUERY_METHOD_PREFIX;
import static org.drools.model.codegen.execmodel.generator.QueryGenerator.toQueryDef;

public class PatternVisitor {

    private final RuleContext context;
    private final PackageModel packageModel;

    public PatternVisitor(RuleContext context, PackageModel packageModel) {
        this.context = context;
        this.packageModel = packageModel;
    }

    public DSLNode visit(PatternDescr pattern) {
        String className = pattern.getObjectType();

        if (className != null) {
            DSLNode constraintDescrs = parsePatternWithClass(pattern, className);
            if (constraintDescrs != null) {
                return constraintDescrs;
            }
        } else {
            pattern = PatternUtil.normalizeOOPathPattern(pattern, context);
            className = pattern.getObjectType();
        }

        List<? extends BaseDescr> constraintDescrs = pattern.getConstraint().getDescrs();

        Class<?> patternType;
        try {
            patternType = context.getTypeResolver().resolveType(className);
            packageModel.addOtnsClass(patternType);
        } catch (ClassNotFoundException e) {
            context.addCompilationError( new InvalidExpressionErrorResult( "Unable to find class: " + className ) );
            return () -> { };
        }

        return new PatternDSLPattern(context, packageModel, pattern, constraintDescrs, patternType);
    }

    private DSLNode parsePatternWithClass(PatternDescr pattern, String className) {
        List<? extends BaseDescr> constraintDescrs = pattern.getConstraint().getDescrs();

        String queryName = QUERY_METHOD_PREFIX + className;
        String queryDef = toQueryDef( className );

        // Expression is a query, get bindings from query parameter type
        if ( packageModel.hasQuery(className) && !context.isRecurisveQuery(queryDef) ) {
            return new Query(context, packageModel, pattern, constraintDescrs, queryName );
        }

        if ( packageModel.getQueryDefWithType().containsKey( queryDef ) ) {
            return new QueryCall(context, packageModel, pattern, queryDef );
        }

        if ( pattern.getIdentifier() == null && className.equals( "Object" ) && pattern.getSource() instanceof AccumulateDescr) {
            return new PatternAccumulateConstraint(context, packageModel, pattern, (( AccumulateDescr ) pattern.getSource()), constraintDescrs );
        }
        return null;
    }
}
