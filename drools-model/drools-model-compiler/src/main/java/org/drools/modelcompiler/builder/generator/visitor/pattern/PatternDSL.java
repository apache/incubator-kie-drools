package org.drools.modelcompiler.builder.generator.visitor.pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.expr.BinaryExpr;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.PatternSourceDescr;
import org.drools.core.util.ClassUtils;
import org.drools.mvel.parser.ast.expr.OOPathExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.WindowReferenceGenerator;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintExpression;
import org.drools.modelcompiler.builder.generator.drlxparse.ConstraintParser;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseFail;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.drlxparse.ParseResultVoidVisitor;
import org.drools.modelcompiler.builder.generator.visitor.DSLNode;
import org.drools.modelcompiler.builder.generator.visitor.FromVisitor;

import static org.drools.model.impl.NamesGenerator.generateName;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getPatternListenedProperties;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.validateDuplicateBindings;
import static org.drools.mvel.parser.printer.PrintUtil.printConstraint;

public abstract class PatternDSL implements DSLNode {

    protected final RuleContext context;
    protected final PackageModel packageModel;
    protected final PatternDescr pattern;
    private final List<? extends BaseDescr> constraintDescrs;
    protected Class<?> patternType;

    PatternDSL(RuleContext context, PackageModel packageModel, PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, Class<?> patternType) {
        this.context = context;
        this.packageModel = packageModel;
        this.pattern = pattern;
        this.constraintDescrs = constraintDescrs;
        this.patternType = patternType;
    }

    public DeclarationSpec initPattern() {
        generatePatternIdentifierIfMissing();
        final Optional<Expression> declarationSource = buildFromDeclaration(pattern);
        return context.addDeclaration(pattern.getIdentifier(), patternType, Optional.of(pattern), declarationSource);
    }

    private static boolean isPositional(BaseDescr constraint) {
        return constraint instanceof ExprConstraintDescr &&
                ((ExprConstraintDescr) constraint).getType() == ExprConstraintDescr.Type.POSITIONAL &&
                !constraint.getText().contains( ":=" );
    }

    private Optional<Expression> buildFromDeclaration(PatternDescr pattern) {
        Optional<PatternSourceDescr> source = Optional.ofNullable(pattern.getSource());
        try {
            patternType = context.getTypeResolver().resolveType( pattern.getObjectType() );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException( e );
        }
        Optional<Expression> declarationSourceFrom = source.flatMap(new FromVisitor(context, packageModel, patternType)::visit);
        if (declarationSourceFrom.isPresent()) {
            return declarationSourceFrom;
        }
        return source.flatMap(new WindowReferenceGenerator(packageModel, context.getTypeResolver())::visit);
    }

    private void generatePatternIdentifierIfMissing() {
        if (pattern.getIdentifier() == null) {
            final String generatedName = generateName("pattern_" + patternType.getSimpleName());
            final String patternNameAggregated = findFirstInnerBinding(constraintDescrs, patternType)
                    .map(ib -> context.getAggregatePatternMap().putIfAbsent(ib, generatedName))
                    .orElse(generatedName);
            pattern.setIdentifier(patternNameAggregated);
        }
    }

    private Optional<String> findFirstInnerBinding(List<? extends BaseDescr> constraintDescrs, Class<?> patternType) {
        return constraintDescrs.stream()
                .map( constraint -> ConstraintExpression.createConstraintExpression( patternType, constraint, isPositional(constraint) ).getExpression() )
                .map( DrlxParseUtil::parseExpression )
                .filter( drlx -> drlx.getBind() != null )
                .map( drlx -> drlx.getBind().asString() )
                .findFirst();
    }

    private List<PatternConstraintParseResult> findAllConstraint(PatternDescr pattern, List<? extends BaseDescr> constraintDescrs, Class<?> patternType) {
        ConstraintParser constraintParser = new ConstraintParser(context, packageModel);
        List<PatternConstraintParseResult> patternConstraintParseResults = new ArrayList<>();

        for (BaseDescr constraint : constraintDescrs) {
            String patternIdentifier = pattern.getIdentifier();

            boolean isPositional = isPositional(constraint);

            ConstraintExpression constraintExpression = ConstraintExpression.createConstraintExpression(patternType, constraint, isPositional);

            DrlxParseResult drlxParseResult = constraintParser.drlxParse(patternType, patternIdentifier, constraintExpression, isPositional);

            String expression = constraintExpression.getExpression();
            if (drlxParseResult.isSuccess() && (( DrlxParseSuccess ) drlxParseResult).isRequiresSplit() && (( DrlxParseSuccess ) drlxParseResult).getExpr().isBinaryExpr()) {
                BinaryExpr expr = ((DrlxParseSuccess) drlxParseResult).getExpr().asBinaryExpr();
                String leftExpression = printConstraint(expr.asBinaryExpr().getLeft());
                DrlxParseResult leftExpressionReparsed = constraintParser.drlxParse(patternType, patternIdentifier, leftExpression, isPositional);
                patternConstraintParseResults.add(new PatternConstraintParseResult(leftExpression, patternIdentifier, leftExpressionReparsed));

                String rightExpression = printConstraint(expr.asBinaryExpr().getRight());
                DrlxParseResult rightExpressionReparsed = constraintParser.drlxParse(patternType, patternIdentifier, rightExpression, isPositional);
                patternConstraintParseResults.add(new PatternConstraintParseResult(rightExpression, patternIdentifier, rightExpressionReparsed));
            } else {
                patternConstraintParseResults.add(new PatternConstraintParseResult(expression, patternIdentifier, drlxParseResult));
            }
        }

        return patternConstraintParseResults;
    }

    void buildConstraint(PatternDescr pattern, Class<?> patternType, PatternConstraintParseResult patternConstraintParseResult) {
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
                        registerUsedBindingInOr(drlxParseResult.getExprBinding());
                    }

                    @Override
                    public void onFail( DrlxParseFail failure ) {
                        if (failure.getError() != null) {
                            context.addCompilationError( failure.getError() );
                        }
                    }
                } );
    }

    private void registerUsedBindingInOr(String exprBinding) {
        if(context.isNestedInsideOr()) {
            context.getBindingOr().add(exprBinding);
        }
    }

    @Override
    public void buildPattern() {
        DeclarationSpec declarationSpec = initPattern();

        if (constraintDescrs.isEmpty() && !(pattern.getSource() instanceof AccumulateDescr)) {
            context.addExpression(input(declarationSpec));
        } else {
            final List<PatternConstraintParseResult> patternConstraintParseResults = findAllConstraint(pattern, constraintDescrs, patternType);
            final List<String> allBindings = patternConstraintParseResults
                    .stream()
                    .map(p -> p.getDrlxParseResult().acceptWithReturnValue( DrlxParseSuccess::getExprBinding ))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            validateDuplicateBindings(context.getRuleName(), allBindings).ifPresent(context::addCompilationError);

            if (!context.hasErrors()) {
                buildPattern(declarationSpec, patternConstraintParseResults);
            }
        }
    }

    Set<String> getSettableWatchedProps() {
        Set<String> watchedProps = new HashSet<>();
        watchedProps.addAll(context.getRuleDescr().lookAheadFieldsOfIdentifier(pattern));
        watchedProps.addAll(getPatternListenedProperties(pattern));

        if (watchedProps.isEmpty()) {
            return watchedProps;
        }
        Collection<String> settableProps = ClassUtils.getAccessibleProperties( patternType );
        Set<String> settableWatchedProps = new HashSet<>();
        for (String watchedProp : watchedProps) {
            String actualProp = watchedProp.startsWith( "!" ) ? watchedProp.substring( 1 ) : watchedProp;
            if (actualProp.equals( "*" ) || settableProps.contains( actualProp )) {
                settableWatchedProps.add( watchedProp );
            }
        }
        return settableWatchedProps;
    }

    protected abstract void buildPattern(DeclarationSpec declarationSpec, List<PatternConstraintParseResult> patternConstraintParseResults);

    protected abstract MethodCallExpr input(DeclarationSpec declarationSpec);

    protected abstract DSLNode createSimpleConstraint( DrlxParseSuccess drlxParseResult, PatternDescr pattern );
}
