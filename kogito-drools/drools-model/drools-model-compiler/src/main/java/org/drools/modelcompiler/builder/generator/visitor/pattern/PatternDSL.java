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
import org.drools.javaparser.ast.drlx.OOPathExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.WindowReferenceGenerator;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseFail;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.drlxparse.ParseResultVoidVisitor;
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

    private Optional<String> findInnerBindingName(List<PatternConstraintParseResult> firstParsedConstraints) {
        return firstParsedConstraints.stream()
                .map(PatternConstraintParseResult::getDrlxParseResult)
                .filter(DrlxParseResult::isSuccess)
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
        if (pattern.getIdentifier() == null) {
            final String generatedName = generateName("pattern_" + patternType.getSimpleName());
            final String patternNameAggregated = findFirstInnerBinding(pattern, constraintDescrs, patternType)
                    .map(ib -> context.getAggregatePatternMap().putIfAbsent(ib, generatedName))
                    .orElse(generatedName);
            pattern.setIdentifier(patternNameAggregated);
        }
    }

    public Optional<String> findFirstInnerBinding(PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, Class<?> patternType) {
        return constraintDescrs.stream()
                .map( constraint -> getExpression( patternType, constraint, isPositional(constraint) ) )
                .map( DrlxParseUtil::parseExpression )
                .filter( drlx -> drlx.getBind() != null )
                .map( drlx -> drlx.getBind().asString() )
                .findFirst();
    }

    protected List<PatternConstraintParseResult> findAllConstraint(PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, Class<?> patternType) {
        ConstraintParser constraintParser = new ConstraintParser(context, packageModel);
        List<PatternConstraintParseResult> patternConstraintParseResults = new ArrayList<>();

        for (BaseDescr constraint : constraintDescrs) {
            String patternIdentifier = pattern.getIdentifier();

            boolean isPositional = isPositional(constraint);
            String expression = getExpression( patternType, constraint, isPositional );

            DrlxParseResult drlxParseResult = constraintParser.drlxParse(patternType, patternIdentifier, expression, isPositional);

            if (drlxParseResult.isSuccess() && (( DrlxParseSuccess ) drlxParseResult).isRequiresSplit()) {
                int splitPos = expression.indexOf( "&&" );
                String expr1 = expression.substring( 0, splitPos ).trim();
                String expr2 = expr1.substring( 0, firstNonIdentifierPos( expr1 ) ) + " " + expression.substring( splitPos+2 ).trim();

                DrlxParseResult drlxParseResult1 = constraintParser.drlxParse(patternType, patternIdentifier, expr1, isPositional);
                patternConstraintParseResults.add(new PatternConstraintParseResult(expr1, patternIdentifier, drlxParseResult1));

                DrlxParseResult drlxParseResult2 = constraintParser.drlxParse(patternType, patternIdentifier, expr2, isPositional);
                patternConstraintParseResults.add(new PatternConstraintParseResult(expr2, patternIdentifier, drlxParseResult2));
            } else {
                patternConstraintParseResults.add(new PatternConstraintParseResult(expression, patternIdentifier, drlxParseResult));
            }
        }

        return patternConstraintParseResults;
    }

    private String getExpression( Class<?> patternType, BaseDescr constraint, boolean isPositional ) {
        String expression = getConstraintExpression(patternType, constraint, isPositional);
        int unifPos = expression.indexOf( ":=" );
        if (unifPos > 0) {
            expression = expression.substring( unifPos+2 ).trim() + " == " + expression.substring( 0, unifPos ).trim();
        }
        return expression;
    }

    private int firstNonIdentifierPos(String expr) {
        for (int i = 0; i < expr.length(); i++) {
            if (!Character.isJavaIdentifierPart( expr.charAt( i ))) {
                return i;
            }
        }
        return expr.length();
    }

    protected void buildConstraint(PatternDescr pattern, Class<?> patternType, PatternConstraintParseResult patternConstraintParseResult) {
        DrlxParseResult drlxParseResult1 = patternConstraintParseResult.getDrlxParseResult();
        String expression = patternConstraintParseResult.getExpression();

        drlxParseResult1.accept(
                new ParseResultVoidVisitor() {
                    @Override
                    public void onSuccess( DrlxParseSuccess drlxParseResult ) {
                        DSLNode constraint = drlxParseResult.getExpr() instanceof OOPathExpr ?
                            new ConstraintOOPath( context, packageModel, pattern, patternType, patternConstraintParseResult, expression, drlxParseResult ) :
                            createSimpleConstraint( drlxParseResult, pattern );
                        constraint.buildPattern();
                    }

                    @Override
                    public void onFail( DrlxParseFail failure ) {
                        context.addCompilationError( failure.getError() );
                    }
                } );
    }

    protected abstract DSLNode createSimpleConstraint( DrlxParseSuccess drlxParseResult, PatternDescr pattern );
}
