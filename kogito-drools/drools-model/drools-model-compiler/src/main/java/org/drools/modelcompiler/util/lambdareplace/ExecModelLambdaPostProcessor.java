package org.drools.modelcompiler.util.lambdareplace;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.drools.modelcompiler.builder.RuleWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.modelcompiler.builder.generator.DslMethodNames.ALPHA_INDEXED_BY_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BETA_INDEXED_BY_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.EXECUTE_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.INDEXED_BY_CALL;
import static org.drools.modelcompiler.builder.generator.expression.PatternExpressionBuilder.EXPR_CALL;

public class ExecModelLambdaPostProcessor {

    Logger logger = LoggerFactory.getLogger(RuleWriter.class.getCanonicalName());

    private final Map<String, CreatedClass> lambdaClasses;
    private final String packageName;
    private final String ruleClassName;
    private final Collection<String> imports;
    private final Collection<String> staticImports;
    private final CompilationUnit clone;

    public ExecModelLambdaPostProcessor(Map<String, CreatedClass> lambdaClasses,
                                        String packageName,
                                        String ruleClassName,
                                        Collection<String> imports,
                                        Collection<String> staticImports,
                                        CompilationUnit clone) {
        this.lambdaClasses = lambdaClasses;
        this.packageName = packageName;
        this.ruleClassName = ruleClassName;
        this.imports = imports;
        this.staticImports = staticImports;
        this.clone = clone;
    }

    public void convertLambdas() {
            clone.findAll(MethodCallExpr.class, mc -> EXPR_CALL.equals(mc.getNameAsString()))
                    .forEach(methodCallExpr1 -> extractLambdaFromMethodCall(methodCallExpr1, () -> new MaterializedLambdaPredicate(packageName, ruleClassName)));

            clone.findAll(MethodCallExpr.class, mc -> INDEXED_BY_CALL.contains(mc.getName().asString()))
                    .forEach(this::convertIndexedByCall);

            clone.findAll(MethodCallExpr.class, mc -> ALPHA_INDEXED_BY_CALL.contains(mc.getName().asString()))
                    .forEach(this::convertIndexedByCall);

            clone.findAll(MethodCallExpr.class, mc -> BETA_INDEXED_BY_CALL.contains(mc.getName().asString()))
                    .forEach(this::convertIndexedByCall);

            clone.findAll(MethodCallExpr.class, this::isExecuteNonNestedCall)
                    .forEach(methodCallExpr -> extractLambdaFromMethodCall(methodCallExpr, () -> new MaterializedLambdaConsequence(packageName, ruleClassName)));
    }

    private boolean isExecuteNonNestedCall(MethodCallExpr mc) {
        Optional<MethodCallExpr> ancestor = mc.findAncestor(MethodCallExpr.class)
                .filter(a -> a.getNameAsString().equals(EXECUTE_CALL));

        return !ancestor.isPresent() && EXECUTE_CALL.equals(mc.getNameAsString());
    }

    private void convertIndexedByCall(MethodCallExpr methodCallExpr) {
        Expression argument = methodCallExpr.getArgument(0);

        if (!argument.isClassExpr()) {
            throw new RuntimeException();
        }

        String returnType = getType(argument).asString();
        extractLambdaFromMethodCall(methodCallExpr, () -> new MaterializedLambdaExtractor(packageName, ruleClassName, returnType));
    }

    protected Type getType(Expression argument) {
        Type type = argument.asClassExpr().getType();
        if (type.isPrimitiveType()) {
            return type.asPrimitiveType().toBoxedType();
        }
        return type;
    }

    private Expression lambdaInstance(ClassOrInterfaceType type) {
        return new FieldAccessExpr(new NameExpr(type.asString()), "INSTANCE");
    }

    private void extractLambdaFromMethodCall(MethodCallExpr methodCallExpr, Supplier<MaterializedLambda> lambdaExtractor) {
        methodCallExpr.getArguments().forEach(a -> {
            if (a.isLambdaExpr()) {
                LambdaExpr lambdaExpr = a.asLambdaExpr();

                try {
                    CreatedClass aClass = lambdaExtractor.get().create(lambdaExpr.toString(), imports, staticImports);
                    lambdaClasses.put(aClass.getClassNameWithPackage(), aClass);

                    ClassOrInterfaceType type = StaticJavaParser.parseClassOrInterfaceType(aClass.getClassNameWithPackage());
                    a.replace(lambdaInstance(type));
                } catch(DoNotConvertLambdaException e) {
                    logger.debug("Cannot externalize lambdas {}", e.getMessage());
                }
            }
        });
    }
}
