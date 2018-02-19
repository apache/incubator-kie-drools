package org.drools.modelcompiler.builder.generator.visitor.pattern;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.PatternSourceDescr;
import org.drools.javaparser.ast.drlx.OOPathExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.WindowReferenceGenerator;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseFail;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.drlxparse.ParseResultVisitor;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;
import org.drools.modelcompiler.builder.generator.visitor.FromVisitor;
import org.kie.api.definition.type.Position;

import static org.drools.model.impl.NamesGenerator.generateName;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getPatternListenedProperties;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;

class SimplePattern implements DSLNode {

    private static final String INPUT_CALL = "input";

    private final RuleContext context;
    private final PackageModel packageModel;
    private final PatternDescr pattern;
    private final List<? extends BaseDescr> constraintDescrs;
    private final Class<?> patternType;
    private final boolean allConstraintsPositional;

    public SimplePattern(RuleContext context, PackageModel packageModel, PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, Class<?> patternType, boolean allConstraintsPositional) {
        this.context = context;
        this.packageModel = packageModel;
        this.pattern = pattern;
        this.constraintDescrs = constraintDescrs;
        this.patternType = patternType;
        this.allConstraintsPositional = allConstraintsPositional;
    }

    @Override
    public void buildPattern() {
        if(pattern.getIdentifier() == null) {
            final List<PatternConstraintParseResult> firstParsedConstraints = findAllConstraint(pattern, constraintDescrs, patternType);
            final Optional<String> innerBindingName = findInnerBindingName(firstParsedConstraints);

            final String generatedName = generateName("pattern_" + patternType.getSimpleName());

            final String patternNameAggregated = innerBindingName
                    .map(ib -> context.getAggregatePatternMap().putIfAbsent(ib, generatedName))
                    .orElse(generatedName);

            pattern.setIdentifier(patternNameAggregated);
        }

        final Optional<Expression> declarationSource = buildFromDeclaration(pattern);
        context.addDeclaration(new DeclarationSpec(pattern.getIdentifier(), patternType, Optional.of(pattern), declarationSource));

        if (constraintDescrs.isEmpty() && !(pattern.getSource() instanceof AccumulateDescr)) {
            context.addExpression(createInputExpression(pattern));
        } else {
            if (!context.hasErrors()) {
                final List<PatternConstraintParseResult> patternConstraintParseResults = findAllConstraint(pattern, constraintDescrs, patternType);
                if(shouldAddInputPattern(patternConstraintParseResults)) {
                    context.addExpression(createInputExpression(pattern));
                }
                buildConstraints(pattern, patternType, patternConstraintParseResults, allConstraintsPositional);
            }
        }
    }

    private boolean shouldAddInputPattern(List<PatternConstraintParseResult> parseResults) {
        final Predicate<? super PatternConstraintParseResult> hasOneOOPathExpr = (Predicate<PatternConstraintParseResult>) patternConstraintParseResult -> {
            return patternConstraintParseResult.getDrlxParseResult().acceptWithReturnValue(new ParseResultVisitor<Boolean>() {
                @Override
                public Boolean onSuccess(DrlxParseSuccess drlxParseResult) {
                    return drlxParseResult.getExpr() instanceof OOPathExpr;
                }

                @Override
                public Boolean onFail(DrlxParseFail failure) {
                    return false;
                }
            });
        };

        return parseResults
                .stream()
                .anyMatch(hasOneOOPathExpr);
    }


    private Optional<String> findInnerBindingName(List<PatternConstraintParseResult> firstParsedConstraints) {
        return firstParsedConstraints.stream()
                .map(PatternConstraintParseResult::getDrlxParseResult)
                .filter(o -> o instanceof DrlxParseSuccess)
                .map(d -> (DrlxParseSuccess)d)
                .map(DrlxParseSuccess::getExprBinding)
                .filter(Objects::nonNull)
                .findFirst();
    }


    private Optional<Expression> buildFromDeclaration(PatternDescr pattern) {
        Optional<PatternSourceDescr> source = Optional.ofNullable(pattern.getSource());
        Optional<Expression> declarationSourceFrom = source.flatMap(new FromVisitor(context, packageModel)::visit);
        Optional<Expression> declarationSourceWindow = source.flatMap(new WindowReferenceGenerator(packageModel, context.getTypeResolver())::visit);
        return declarationSourceFrom.isPresent() ? declarationSourceFrom : declarationSourceWindow;
    }

    private MethodCallExpr createInputExpression(PatternDescr pattern) {
        MethodCallExpr exprDSL = new MethodCallExpr(null, INPUT_CALL);
        exprDSL.addArgument(new NameExpr(toVar(pattern.getIdentifier())));

        Set<String> watchedProperties = new HashSet<>();
        watchedProperties.addAll(context.getRuleDescr().lookAheadFieldsOfIdentifier(pattern));
        watchedProperties.addAll(getPatternListenedProperties(pattern));
        if (!watchedProperties.isEmpty()) {
            exprDSL = new MethodCallExpr(exprDSL, "watch");
            watchedProperties.stream()
                    .map( StringLiteralExpr::new )
                    .forEach( exprDSL::addArgument );
        }

        return exprDSL;
    }

    public List<PatternConstraintParseResult> findAllConstraint(PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, Class<?> patternType) {
        final List<PatternConstraintParseResult> patternConstraintParseResults = new ArrayList<>();
        for (BaseDescr constraint : constraintDescrs) {
            final PatternConstraintParseResult patternConstraintParseResult = parseConstraint(pattern, patternType, constraint);
            patternConstraintParseResults.add(patternConstraintParseResult);
        }
        return patternConstraintParseResults;
    }

    private PatternConstraintParseResult parseConstraint(PatternDescr pattern, Class<?> patternType, BaseDescr constraint) {
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

    private void buildConstraints(PatternDescr pattern, Class<?> patternType, List<PatternConstraintParseResult> patternConstraintParseResults, boolean allConstraintsPositional) {
        if (allConstraintsPositional) {
            final MethodCallExpr andDSL = new MethodCallExpr(null, "and");
            context.addExpression(andDSL);
            context.pushExprPointer(andDSL::addArgument);
        }

        for (PatternConstraintParseResult patternConstraintParseResult : patternConstraintParseResults) {
            buildConstraint(pattern, patternType, patternConstraintParseResult);
        }
        if (allConstraintsPositional) {
            context.popExprPointer();
        }
    }

    private void buildConstraint(PatternDescr pattern, Class<?> patternType, PatternConstraintParseResult patternConstraintParseResult) {
        DrlxParseResult drlxParseResult1 = patternConstraintParseResult.getDrlxParseResult();
        String expression = patternConstraintParseResult.getExpression();

        drlxParseResult1.accept(drlxParseResult -> {
            DSLNode constraint;
            if (drlxParseResult.getExpr() instanceof OOPathExpr) {
                constraint = new ConstraintOOPath(context, packageModel, pattern, patternType, patternConstraintParseResult, expression, drlxParseResult);
            } else {
                constraint = new SimpleConstraint(context, pattern, drlxParseResult);
            }
            constraint.buildPattern();
        });
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
}
