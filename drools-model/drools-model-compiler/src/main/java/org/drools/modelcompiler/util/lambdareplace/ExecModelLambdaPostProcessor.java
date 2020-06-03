/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.util.lambdareplace;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.printer.PrettyPrinter;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import org.drools.model.BitMask;
import org.drools.modelcompiler.builder.generator.expression.FlowExpressionBuilder;
import org.drools.modelcompiler.builder.generator.expression.PatternExpressionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.modelcompiler.builder.generator.DslMethodNames.ALPHA_INDEXED_BY_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BETA_INDEXED_BY_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BIND_AS_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.EXECUTE_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.INDEXED_BY_CALL;
import static org.drools.modelcompiler.util.StreamUtils.optionalToStream;

public class ExecModelLambdaPostProcessor {

    Logger logger = LoggerFactory.getLogger(ExecModelLambdaPostProcessor.class.getCanonicalName());

    private final Map<String, CreatedClass> lambdaClasses;
    private final String packageName;
    private final String ruleClassName;
    private final Collection<String> imports;
    private final Collection<String> staticImports;
    private final CompilationUnit clone;

    private static final PrettyPrinterConfiguration configuration = new PrettyPrinterConfiguration();

    static {
        configuration.setEndOfLineCharacter("\n"); // hashes will be stable also while testing on windows
    }

    public static final PrettyPrinter MATERIALIZED_LAMBDA_PRETTY_PRINTER = new PrettyPrinter(configuration);

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
            clone.findAll(MethodCallExpr.class, mc -> PatternExpressionBuilder.EXPR_CALL.equals(mc.getNameAsString()) ||
                                                      FlowExpressionBuilder.EXPR_CALL.equals(mc.getNameAsString()))
                    .forEach(methodCallExpr1 -> extractLambdaFromMethodCall(methodCallExpr1, () -> new MaterializedLambdaPredicate(packageName, ruleClassName)));

            clone.findAll(MethodCallExpr.class, mc -> INDEXED_BY_CALL.contains(mc.getName().asString()))
                    .forEach(this::convertIndexedByCall);

            clone.findAll(MethodCallExpr.class, mc -> ALPHA_INDEXED_BY_CALL.contains(mc.getName().asString()))
                    .forEach(this::convertIndexedByCall);

            clone.findAll(MethodCallExpr.class, mc -> BETA_INDEXED_BY_CALL.contains(mc.getName().asString()))
                    .forEach(this::convertIndexedByCall);

            clone.findAll(MethodCallExpr.class, mc -> PatternExpressionBuilder.BIND_CALL.equals(mc.getNameAsString()))
                    .forEach(this::convertBindCall);

            clone.findAll(MethodCallExpr.class, mc -> FlowExpressionBuilder.BIND_CALL.equals(mc.getNameAsString()))
                    .forEach(this::convertBindCallForFlowDSL);

            clone.findAll(MethodCallExpr.class, this::isExecuteNonNestedCall)
                    .forEach(methodCallExpr -> {
                        List<MaterializedLambda.BitMaskVariable> bitMaskVariables = findBitMaskFields(methodCallExpr);
                        extractLambdaFromMethodCall(methodCallExpr, () -> new MaterializedLambdaConsequence(packageName, ruleClassName, bitMaskVariables));
                    });
    }

    private boolean isExecuteNonNestedCall(MethodCallExpr mc) {
        Optional<MethodCallExpr> ancestor = mc.findAncestor(MethodCallExpr.class)
                .filter(a -> a.getNameAsString().equals(EXECUTE_CALL));

        return !ancestor.isPresent() && EXECUTE_CALL.equals(mc.getNameAsString());
    }

    private void convertIndexedByCall(MethodCallExpr methodCallExpr) {
        Expression argument = methodCallExpr.getArgument(0);

        if (!argument.isClassExpr()) {
            logger.warn("argument is not ClassExpr. argument : {}, methodCallExpr : {}", argument, methodCallExpr);
            return;
        }

        String returnType = getType(argument).asString();
        extractLambdaFromMethodCall(methodCallExpr, () -> new MaterializedLambdaExtractor(packageName, ruleClassName, returnType));
    }

    private void convertBindCall(MethodCallExpr methodCallExpr) {
        Expression argument = methodCallExpr.getArgument(0);

        if (!argument.isNameExpr()) {
            logger.warn("argument is not NameExpr. argument : {}, methodCallExpr : {}", argument, methodCallExpr);
            return;
        }

        Optional<Type> optType = findVariableType((NameExpr) argument);
        if (!optType.isPresent()) {
            logger.warn("VariableDeclarator type was not found for {}, methodCallExpr : {}", argument, methodCallExpr);
            return;
        }
        String returnType = optType.get().asString();

        extractLambdaFromMethodCall(methodCallExpr, () -> new MaterializedLambdaExtractor(packageName, ruleClassName, returnType));
    }

    private void convertBindCallForFlowDSL(MethodCallExpr methodCallExpr) {
        Expression argument = methodCallExpr.getArgument(0);

        if (!argument.isNameExpr()) {
            logger.warn("argument is not NameExpr. argument : {}, methodCallExpr : {}", argument, methodCallExpr);
            return;
        }

        Optional<Type> optType = findVariableType((NameExpr) argument);
        if (!optType.isPresent()) {
            logger.warn("VariableDeclarator type was not found for {}, methodCallExpr : {}", argument, methodCallExpr);
            return;
        }
        String returnType = optType.get().asString();

        Optional<MethodCallExpr> bindAsMethodOpt = optionalToStream(methodCallExpr.getParentNode())
            .map(node -> (MethodCallExpr) node)
            .filter(parentMethod -> parentMethod.getNameAsString().equals(BIND_AS_CALL))
            .findFirst();

        if (!bindAsMethodOpt.isPresent()) {
            logger.warn("Method 'as' is not found for {}", methodCallExpr);
            return; // not externalize
        }

        extractLambdaFromMethodCall(bindAsMethodOpt.get(), () -> new MaterializedLambdaExtractor(packageName, ruleClassName, returnType));
    }

    private Optional<Type> findVariableType(NameExpr nameExpr) {
        return optionalToStream(nameExpr.findAncestor(MethodDeclaration.class))
            .flatMap(node -> node.findAll(VariableDeclarator.class).stream())
            .filter(node -> node.getName().equals(nameExpr.getName()))
            .map(VariableDeclarator::getType)
            .map(type -> (ClassOrInterfaceType)type)
            .flatMap(classOrInterfaceType -> optionalToStream(classOrInterfaceType.getTypeArguments()))
            .filter(typeArgList -> typeArgList.size() == 1)
            .map(typeArgList -> typeArgList.get(0))
            .findFirst();
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

    private List<MaterializedLambda.BitMaskVariable> findBitMaskFields(MethodCallExpr methodCallExpr) {
        return optionalToStream(methodCallExpr.findAncestor(MethodDeclaration.class))
                .flatMap(node -> node.findAll(VariableDeclarator.class).stream())
                .filter(this::isBitMaskType)
                .flatMap(this::findAssignExpr)
                .map(this::toMaterializedLambdaFactory)
                .collect(Collectors.toList());
    }

    private boolean isBitMaskType(VariableDeclarator vd) {
        return vd.getType().asString().equals(BitMask.class.getCanonicalName());
    }

    private MaterializedLambda.BitMaskVariable toMaterializedLambdaFactory(AssignExpr ae) {
        String maskName = ae.getTarget().asVariableDeclarationExpr().getVariables().iterator().next().getNameAsString();
        MethodCallExpr maskInit = ae.getValue().asMethodCallExpr();
        if (maskInit.getArguments().isEmpty()) {
            return new MaterializedLambda.AllSetButLastBitMask(maskName);
        } else {
            NodeList<Expression> arguments = maskInit.getArguments();
            String domainClassMetadata = arguments.get(0).toString();
            List<String> fields = arguments.subList(1, arguments.size()).stream().map(Expression::toString).collect(Collectors.toList());
            return new MaterializedLambda.BitMaskVariableWithFields(domainClassMetadata, fields, maskName);
        }
    }

    private Stream<? extends AssignExpr> findAssignExpr(VariableDeclarator vd) {
        return optionalToStream(vd.findAncestor(AssignExpr.class));
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
