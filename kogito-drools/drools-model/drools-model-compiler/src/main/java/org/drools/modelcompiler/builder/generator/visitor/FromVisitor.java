package org.drools.modelcompiler.builder.generator.visitor;

import java.util.Optional;

import org.drools.compiler.lang.descr.FromDescr;
import org.drools.compiler.lang.descr.PatternSourceDescr;
import org.drools.core.ruleunit.RuleUnitDescr;
import org.drools.javaparser.ast.expr.ClassExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.DrlxParseResult;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.ModelGenerator;
import org.drools.modelcompiler.builder.generator.RuleContext;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.classToReferenceType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.ModelGenerator.drlxParse;

public class FromVisitor {

    final RuleContext ruleContext;
    final PackageModel packageModel;

    private final String FROM_CALL = "from";
    private final String UNIT_DATA_CALL = "unitData";

    public FromVisitor(RuleContext context, PackageModel packageModel) {
        this.ruleContext = context;
        this.packageModel = packageModel;
    }

    public Optional<Expression> visit(PatternSourceDescr sourceDescr) {
        if (sourceDescr instanceof FromDescr) {
            final String expression = ((FromDescr) sourceDescr).getDataSource().toString();
            Optional<String> optContainsBinding = DrlxParseUtil.findBindingIdFromDotExpression(expression);
            final String bindingId = optContainsBinding.orElse(expression);

            MethodCallExpr fromCall = ruleContext.hasDeclaration( bindingId ) || packageModel.hasDeclaration( bindingId ) ?
                    createFromCall( expression, optContainsBinding, bindingId ) :
                    createUnitDataCall( expression, optContainsBinding, bindingId );

            return Optional.of(fromCall);
        } else {
            return Optional.empty();
        }
    }

    private MethodCallExpr createFromCall( String expression, Optional<String> optContainsBinding, String bindingId ) {
        MethodCallExpr fromCall = new MethodCallExpr(null, FROM_CALL);
        fromCall.addArgument(new NameExpr(toVar(bindingId)));

        if (optContainsBinding.isPresent()) {
            DeclarationSpec declarationSpec = ruleContext.getDeclarationById(bindingId).orElseThrow(RuntimeException::new);
            Class<?> clazz = declarationSpec.getDeclarationClass();

            DrlxParseResult drlxParseResult = drlxParse(ruleContext, packageModel, clazz, bindingId, expression);

            Expression parsedExpression = drlxParseResult.getExpr();
            Expression exprArg = generateLambdaWithoutParameters(drlxParseResult.getUsedDeclarations(), parsedExpression);

            fromCall.addArgument(exprArg);
        }
        return fromCall;
    }

    private MethodCallExpr createUnitDataCall( String expression, Optional<String> optContainsBinding, String bindingId ) {
        RuleUnitDescr ruleUnitDescr = ruleContext.getRuleUnitDescr();
        if ( ruleUnitDescr == null ) {
            throw new IllegalArgumentException("Unknown binding name: " + bindingId);
        }

        Optional<Class<?>> varType = ruleUnitDescr.getVarType( bindingId );
        if ( !varType.isPresent() ) {
            throw new IllegalArgumentException("Unknown binding name: " + bindingId);
        }

        MethodCallExpr unitDataCall = new MethodCallExpr(null, UNIT_DATA_CALL);

        MethodCallExpr typeCall = new MethodCallExpr(null, ModelGenerator.TYPE_CALL);
        typeCall.addArgument( new ClassExpr( classToReferenceType(varType.get()) ));
        unitDataCall.addArgument(typeCall);

        unitDataCall.addArgument(new StringLiteralExpr(bindingId));

        if (optContainsBinding.isPresent()) {
            throw new UnsupportedOperationException();
        }

        return unitDataCall;
    }
}
