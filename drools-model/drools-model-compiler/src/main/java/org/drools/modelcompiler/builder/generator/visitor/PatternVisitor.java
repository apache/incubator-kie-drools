package org.drools.modelcompiler.builder.generator.visitor;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    public void visit(PatternDescr pattern ) {
        String className = pattern.getObjectType();
        List<? extends BaseDescr> constraintDescrs = pattern.getConstraint().getDescrs();

        // Expression is a query, get bindings from query parameter type
        if ( QueryGenerator.bindQuery(context, packageModel, pattern, constraintDescrs ) ) {
            return;
        }

        if ( QueryGenerator.createQueryCall(packageModel, context, pattern) ) {
            return;
        }

        Class<?> patternType = getClassFromContext(context.getPkg().getTypeResolver(), className);

        if (pattern.getIdentifier() == null) {
            pattern.setIdentifier( generateName("pattern_" + patternType.getSimpleName()) );
        }

        Optional<PatternSourceDescr> source = Optional.ofNullable(pattern.getSource());
        Optional<Expression> declarationSourceFrom = source.flatMap(new FromVisitor(context, packageModel)::visit);
        Optional<Expression> declarationSourceWindow = source.flatMap(new WindowReferenceGenerator(packageModel, context.getPkg())::visit);
        Optional<Expression> declarationSource = declarationSourceFrom.isPresent() ? declarationSourceFrom : declarationSourceWindow;
        context.addDeclaration(new DeclarationSpec(pattern.getIdentifier(), patternType, Optional.of(pattern), declarationSource));

        if (constraintDescrs.isEmpty() && pattern.getSource() == null) {
            MethodCallExpr dslExpr = new MethodCallExpr(null, INPUT_CALL);
            dslExpr.addArgument(new NameExpr(toVar(pattern.getIdentifier())));
            context.addExpression( dslExpr );
        } else {

            final boolean allConstraintsArePositional = !constraintDescrs.isEmpty() && constraintDescrs.stream()
                    .allMatch(c -> c instanceof ExprConstraintDescr
                            && ((ExprConstraintDescr) c).getType().equals(ExprConstraintDescr.Type.POSITIONAL));
            if(allConstraintsArePositional) {
                final MethodCallExpr andDSL = new MethodCallExpr(null, "and");
                context.addExpression(andDSL);
                context.pushExprPointer(andDSL::addArgument);
            }

            for (BaseDescr constraint : constraintDescrs) {
                String expression = getConstraintExpression(patternType, constraint);
                String patternIdentifier = pattern.getIdentifier();
                if(expression.contains(":=")) {
                    expression = expression.replace(":=", "==");
                }

                DrlxParseResult drlxParseResult = ModelGenerator.drlxParse(context, packageModel, patternType, patternIdentifier, expression);
                if (drlxParseResult == null) {
                    return;
                }

                if(drlxParseResult.getExpr() instanceof OOPathExpr) {

                    // If the  outer pattern does not have a binding we generate it
                    if(patternIdentifier == null) {
                        patternIdentifier = context.getExprId(patternType, expression);
                        context.addDeclaration(new DeclarationSpec(patternIdentifier, patternType, Optional.of(pattern), Optional.empty()));
                    }

                    new OOPathExprGenerator(context, packageModel).visit(patternType, patternIdentifier, (OOPathExpr)drlxParseResult.getExpr());
                } else {
                    // need to augment the reactOn inside drlxParseResult with the look-ahead properties.
                    Collection<String> lookAheadFieldsOfIdentifier = context.getRuleDescr().lookAheadFieldsOfIdentifier(pattern);
                    drlxParseResult.getReactOnProperties().addAll(lookAheadFieldsOfIdentifier);
                    drlxParseResult.setWatchedProperties(getPatternListenedProperties(pattern));

                    if (pattern.isUnification()) {
                        drlxParseResult.setPatternBindingUnification(true);
                    }

                    ModelGenerator.processExpression( context, drlxParseResult );
                }
            }
            if(allConstraintsArePositional) {
                context.popExprPointer();
            }
        }
    }

    private static String getConstraintExpression(Class<?> patternType, BaseDescr constraint) {
        if (constraint instanceof ExprConstraintDescr && (( ExprConstraintDescr ) constraint).getType() == ExprConstraintDescr.Type.POSITIONAL) {
            int position = (( ExprConstraintDescr ) constraint).getPosition();
            return getFieldAtPosition(patternType, position) + " == " + constraint.toString();
        }
        return constraint.toString();
    }


    private static String getFieldAtPosition(Class<?> patternType, int position) {
        for (Field field : patternType.getDeclaredFields()) {
            Position p = field.getAnnotation(Position.class );
            if (p != null && p.value() == position) {
                return field.getName();
            }
        }
        throw new RuntimeException( "Cannot find field in position " + position + " for " + patternType );
    }

    private static String[] getPatternListenedProperties(PatternDescr pattern) {
        AnnotationDescr watchAnn = pattern != null ? pattern.getAnnotation("watch" ) : null;
        return watchAnn == null ? new String[0] : watchAnn.getValue().toString().split(",");
    }
}
