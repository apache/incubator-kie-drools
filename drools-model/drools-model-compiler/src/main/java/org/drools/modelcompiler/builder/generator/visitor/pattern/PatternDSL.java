package org.drools.modelcompiler.builder.generator.visitor.pattern;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.PatternSourceDescr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.WindowReferenceGenerator;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;
import org.drools.modelcompiler.builder.generator.visitor.FromVisitor;
import org.kie.api.definition.type.Position;

import static org.drools.model.impl.NamesGenerator.generateName;

abstract class PatternDSL implements DSLNode {

    protected final RuleContext context;
    protected final PackageModel packageModel;
    protected final PatternDescr pattern;
    protected final List<? extends BaseDescr> constraintDescrs;
    protected final Class<?> patternType;
    protected final boolean allConstraintsPositional;

    PatternDSL(RuleContext context, PackageModel packageModel, PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, boolean allConstraintsPositional, Class<?> patternType) {
        this.context = context;
        this.packageModel = packageModel;
        this.pattern = pattern;
        this.constraintDescrs = constraintDescrs;
        this.allConstraintsPositional = allConstraintsPositional;
        this.patternType = patternType;
    }

    protected static String getConstraintExpression(Class<?> patternType, BaseDescr constraint, boolean isPositional) {
        if (isPositional) {
            int position = ((ExprConstraintDescr) constraint).getPosition();
            return getFieldAtPosition(patternType, position) + " == " + constraint.toString();
        }
        return constraint.toString();
    }

    protected static boolean isPositional(BaseDescr constraint) {
        return constraint instanceof ExprConstraintDescr && ((ExprConstraintDescr) constraint).getType() == ExprConstraintDescr.Type.POSITIONAL;
    }

    static String getFieldAtPosition(Class<?> patternType, int position) {
        for (Field field : patternType.getDeclaredFields()) {
            Position p = field.getAnnotation(Position.class);
            if (p != null && p.value() == position) {
                return field.getName();
            }
        }
        throw new RuntimeException("Cannot find field in position " + position + " for " + patternType);
    }

    protected Optional<String> findInnerBindingName(List<PatternConstraintParseResult> firstParsedConstraints) {
        return firstParsedConstraints.stream()
                .map(PatternConstraintParseResult::getDrlxParseResult)
                .filter(o -> o instanceof DrlxParseSuccess)
                .map(d -> (DrlxParseSuccess)d)
                .map(DrlxParseSuccess::getExprBinding)
                .filter(Objects::nonNull)
                .findFirst();
    }

    protected Optional<Expression> buildFromDeclaration(PatternDescr pattern) {
        Optional<PatternSourceDescr> source = Optional.ofNullable(pattern.getSource());
        Optional<Expression> declarationSourceFrom = source.flatMap(new FromVisitor(context, packageModel)::visit);
        Optional<Expression> declarationSourceWindow = source.flatMap(new WindowReferenceGenerator(packageModel, context.getTypeResolver())::visit);
        return declarationSourceFrom.isPresent() ? declarationSourceFrom : declarationSourceWindow;
    }

    protected void generatePatternIdentifierIfMissing() {
        if(pattern.getIdentifier() == null) {
            final List<PatternConstraintParseResult> firstParsedConstraints = findAllConstraint(pattern, constraintDescrs, patternType);
            final Optional<String> innerBindingName = findInnerBindingName(firstParsedConstraints);

            final String generatedName = generateName("pattern_" + patternType.getSimpleName());

            final String patternNameAggregated = innerBindingName
                    .map(ib -> context.getAggregatePatternMap().putIfAbsent(ib, generatedName))
                    .orElse(generatedName);

            pattern.setIdentifier(patternNameAggregated);
        }
    }

    public List<PatternConstraintParseResult> findAllConstraint(PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, Class<?> patternType) {
        final List<PatternConstraintParseResult> patternConstraintParseResults = new ArrayList<>();
        for (BaseDescr constraint : constraintDescrs) {
            final PatternConstraintParseResult patternConstraintParseResult = parseConstraint(pattern, patternType, constraint);
            patternConstraintParseResults.add(patternConstraintParseResult);
        }
        return patternConstraintParseResults;
    }

    PatternConstraintParseResult parseConstraint(PatternDescr pattern, Class<?> patternType, BaseDescr constraint) {
        final boolean isPositional = isPositional(constraint);
        final String patternIdentifier = pattern.getIdentifier();

        String expression = getConstraintExpression(patternType, constraint, isPositional);
        int unifPos = expression.indexOf( ":=" );
        if (unifPos > 0) {
            expression = expression.substring( unifPos+2 ).trim() + " == " + expression.substring( 0, unifPos ).trim();
        }

        final DrlxParseResult drlxParseResult = new ConstraintParser(context, packageModel).drlxParse(patternType, patternIdentifier, expression, isPositional);
        return new PatternConstraintParseResult(expression, patternIdentifier, drlxParseResult);
    }
}
