package org.drools.modelcompiler.builder.generator.visitor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.PatternSourceDescr;
import org.drools.javaparser.ast.drlx.OOPathExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.ModelGenerator;
import org.drools.modelcompiler.builder.generator.OOPathExprGenerator;
import org.drools.modelcompiler.builder.generator.QueryGenerator;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.WindowReferenceGenerator;
import org.kie.api.definition.type.Position;

import static org.drools.model.impl.NamesGenerator.generateName;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getClassFromContext;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;

public class PatternVisitor {

    static final String INPUT_CALL = "input";
    private final RuleContext context;
    private final PackageModel packageModel;

    public PatternVisitor(RuleContext context, PackageModel packageModel) {
        this.context = context;
        this.packageModel = packageModel;
    }

    public void visit(PatternDescr pattern) {
        String className = pattern.getObjectType();
        List<? extends BaseDescr> constraintDescrs = pattern.getConstraint().getDescrs();

        // Expression is a query, get bindings from query parameter type
        if (QueryGenerator.bindQuery(context, packageModel, pattern, constraintDescrs)) {
            return;
        }

        if (QueryGenerator.createQueryCall(packageModel, context, pattern)) {
            return;
        }

        Class<?> patternType = getClassFromContext(context.getPkg().getTypeResolver(), className);

        final boolean allConstraintsPositional = areAllConstraintsPositional(constraintDescrs);

        if (pattern.getIdentifier() == null) {
            if (pattern.getObjectType().equals( "Object" ) && pattern.getSource() instanceof AccumulateDescr) {
                buildConstraintsForAccumulate(pattern, (( AccumulateDescr ) pattern.getSource()), constraintDescrs);
                return;
            }

            final List<ConstraintParseResult> firstParsedConstraints = findAllConstraint(pattern, constraintDescrs, patternType);
            final Optional<String> innerBindingName = findInnerBindingName(firstParsedConstraints);

            final String generatedName = generateName("pattern_" + patternType.getSimpleName());

            final String patternNameAggregated = innerBindingName
                    .map(ib -> context.getAggregatePatternMap().putIfAbsent(ib, generatedName))
                    .orElse(generatedName);

            pattern.setIdentifier(patternNameAggregated);
        }

        final Optional<Expression> declarationSource = buildFromDeclaration(pattern);
        context.addDeclaration(new DeclarationSpec(pattern.getIdentifier(), patternType, Optional.of(pattern), declarationSource));

        if (constraintDescrs.isEmpty() && pattern.getSource() == null) {
            context.addExpression(createInputExpression(pattern));
        } else {
            final List<ConstraintParseResult> constraintParseResults = findAllConstraint(pattern, constraintDescrs, patternType);
            buildConstraints(pattern, patternType, constraintParseResults, allConstraintsPositional);
        }
    }

    private void buildConstraintsForAccumulate(PatternDescr pattern, AccumulateDescr acc, List<? extends BaseDescr> constraintDescrs) {
        for (BaseDescr constraint : constraintDescrs) {
            String expression = constraint.toString();
            DrlxParseResult drlxParseResult = ModelGenerator.drlxParse(context, packageModel, null, null, expression, false).setSkipThisAsParam( true );
            ModelGenerator.processExpression( context, drlxParseResult );
        }
    }

    private Optional<String> findInnerBindingName(List<ConstraintParseResult> firstParsedConstraints) {
        return firstParsedConstraints.stream()
                .map(c -> c.drlxParseResult)
                .filter(Objects::nonNull)
                .map(DrlxParseResult::getExprBinding)
                .filter(Objects::nonNull)
                .findFirst();
    }

    private List<ConstraintParseResult> findAllConstraint(PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, Class<?> patternType) {
        final List<ConstraintParseResult> constraintParseResults = new ArrayList<>();
        for (BaseDescr constraint : constraintDescrs) {
            final ConstraintParseResult constraintParseResult = parseConstraint(pattern, patternType, constraint);
            constraintParseResults.add(constraintParseResult);
        }
        return constraintParseResults;
    }

    private ConstraintParseResult parseConstraint(PatternDescr pattern, Class<?> patternType, BaseDescr constraint) {
        final boolean isPositional = isPositional(constraint);
        final String patternIdentifier = pattern.getIdentifier();

        String expression = getConstraintExpression(patternType, constraint, isPositional);
        if (expression.contains(":=")) {
            expression = expression.replace(":=", "==");
        }

        final DrlxParseResult drlxParseResult = ModelGenerator.drlxParse(context, packageModel, patternType, patternIdentifier, expression, isPositional);
        return new ConstraintParseResult(expression, patternIdentifier, drlxParseResult);
    }

    private void buildConstraints(PatternDescr pattern, Class<?> patternType, List<ConstraintParseResult> constraintParseResults, boolean allConstraintsPositional) {
        if (allConstraintsPositional) {
            final MethodCallExpr andDSL = new MethodCallExpr(null, "and");
            context.addExpression(andDSL);
            context.pushExprPointer(andDSL::addArgument);
        }

        for (ConstraintParseResult constraintParseResult : constraintParseResults) {
            buildConstraint(pattern, patternType, constraintParseResult);
        }
        if (allConstraintsPositional) {
            context.popExprPointer();
        }
    }

    private void buildConstraint(PatternDescr pattern, Class<?> patternType, ConstraintParseResult constraintParseResult) {
        DrlxParseResult drlxParseResult = constraintParseResult.getDrlxParseResult();
        String patternIdentifier = constraintParseResult.getPatternIdentifier();
        String expression = constraintParseResult.getExpression();
        if (drlxParseResult != null) {
            if (drlxParseResult.getExpr() instanceof OOPathExpr) {

                // If the  outer pattern does not have a binding we generate it
                if (patternIdentifier == null) {
                    patternIdentifier = context.getExprId(patternType, expression);
                    context.addDeclaration(new DeclarationSpec(patternIdentifier, patternType, Optional.of(pattern), Optional.empty()));
                }

                context.addExpression(createInputExpression(pattern));

                new OOPathExprGenerator(context, packageModel).visit(patternType, patternIdentifier, drlxParseResult);
            } else {
                // need to augment the reactOn inside drlxParseResult with the look-ahead properties.
                Collection<String> lookAheadFieldsOfIdentifier = context.getRuleDescr().lookAheadFieldsOfIdentifier(pattern);
                drlxParseResult.getReactOnProperties().addAll(lookAheadFieldsOfIdentifier);
                drlxParseResult.setWatchedProperties(getPatternListenedProperties(pattern));

                if (pattern.isUnification()) {
                    drlxParseResult.setPatternBindingUnification(true);
                }

                ModelGenerator.processExpression(context, drlxParseResult);
            }
        }
    }

    private boolean areAllConstraintsPositional(List<? extends BaseDescr> constraintDescrs) {
        return !constraintDescrs.isEmpty() && constraintDescrs.stream()
                .allMatch(c -> c instanceof ExprConstraintDescr
                        && ((ExprConstraintDescr) c).getType().equals(ExprConstraintDescr.Type.POSITIONAL));
    }

    private Optional<Expression> buildFromDeclaration(PatternDescr pattern) {
        Optional<PatternSourceDescr> source = Optional.ofNullable(pattern.getSource());
        Optional<Expression> declarationSourceFrom = source.flatMap(new FromVisitor(context, packageModel)::visit);
        Optional<Expression> declarationSourceWindow = source.flatMap(new WindowReferenceGenerator(packageModel, context.getPkg())::visit);
        return declarationSourceFrom.isPresent() ? declarationSourceFrom : declarationSourceWindow;
    }

    private MethodCallExpr createInputExpression(PatternDescr pattern) {
        MethodCallExpr dslExpr = new MethodCallExpr(null, INPUT_CALL);
        dslExpr.addArgument(new NameExpr(toVar(pattern.getIdentifier())));
        return dslExpr;
    }

    private static String getConstraintExpression(Class<?> patternType, BaseDescr constraint, boolean isPositional) {
        if (isPositional) {
            int position = ((ExprConstraintDescr) constraint).getPosition();
            return getFieldAtPosition(patternType, position) + " == " + constraint.toString();
        }
        return constraint.toString();
    }

    private static boolean isPositional(BaseDescr constraint) {
        return constraint instanceof ExprConstraintDescr && ((ExprConstraintDescr) constraint).getType() == ExprConstraintDescr.Type.POSITIONAL;
    }

    private static String getFieldAtPosition(Class<?> patternType, int position) {
        for (Field field : patternType.getDeclaredFields()) {
            Position p = field.getAnnotation(Position.class);
            if (p != null && p.value() == position) {
                return field.getName();
            }
        }
        throw new RuntimeException("Cannot find field in position " + position + " for " + patternType);
    }

    private static String[] getPatternListenedProperties(PatternDescr pattern) {
        AnnotationDescr watchAnn = pattern != null ? pattern.getAnnotation("watch") : null;
        return watchAnn == null ? new String[0] : watchAnn.getValue().toString().split(",");
    }

    private class ConstraintParseResult {
        private String expression;
        private String patternIdentifier;
        private DrlxParseResult drlxParseResult;

        public ConstraintParseResult(String expression, String patternIdentifier, DrlxParseResult drlxParseResult) {
            this.expression = expression;
            this.patternIdentifier = patternIdentifier;
            this.drlxParseResult = drlxParseResult;
        }

        public String getExpression() {
            return expression;
        }

        public String getPatternIdentifier() {
            return patternIdentifier;
        }

        public DrlxParseResult getDrlxParseResult() {
            return drlxParseResult;
        }
    }
}
