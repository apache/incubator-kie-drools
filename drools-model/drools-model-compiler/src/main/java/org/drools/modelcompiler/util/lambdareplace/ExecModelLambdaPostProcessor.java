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

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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
import com.github.javaparser.ast.expr.LiteralStringValueExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.printer.PrettyPrinter;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import org.drools.model.BitMask;
import org.drools.model.functions.PredicateInformation;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.ModelGenerator;
import org.drools.modelcompiler.builder.generator.expression.FlowExpressionBuilder;
import org.drools.modelcompiler.builder.generator.expression.PatternExpressionBuilder;
import org.drools.modelcompiler.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.modelcompiler.builder.generator.DslMethodNames.ALPHA_INDEXED_BY_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BETA_INDEXED_BY_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BIND_AS_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.EXECUTE_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.FROM_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.INDEXED_BY_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.REACTIVE_FROM_CALL;
import static org.drools.modelcompiler.util.StreamUtils.optionalToStream;

public class ExecModelLambdaPostProcessor {

    Logger logger = LoggerFactory.getLogger(ExecModelLambdaPostProcessor.class.getCanonicalName());

    private final Map<String, CreatedClass> lambdaClasses;
    private final String packageName;
    private final String ruleClassName;
    private final Collection<String> imports;
    private final Collection<String> staticImports;
    private final Map<LambdaExpr, java.lang.reflect.Type> lambdaReturnTypes;
    private final Map<String, PredicateInformation> debugPredicateInformation;
    private final CompilationUnit clone;

    private static final PrettyPrinterConfiguration configuration = new PrettyPrinterConfiguration();

    static {
        configuration.setEndOfLineCharacter("\n"); // hashes will be stable also while testing on windows
    }

    public static final PrettyPrinter MATERIALIZED_LAMBDA_PRETTY_PRINTER = new PrettyPrinter(configuration);

    public ExecModelLambdaPostProcessor(PackageModel pkgModel,
                                        CompilationUnit clone) {
        this.lambdaClasses = pkgModel.getLambdaClasses();
        this.packageName = pkgModel.getName();
        this.ruleClassName = pkgModel.getRulesFileNameWithPackage();
        this.imports = pkgModel.getImports();
        this.staticImports = pkgModel.getStaticImports();
        this.lambdaReturnTypes = pkgModel.getLambdaReturnTypes();
        this.debugPredicateInformation = pkgModel.getAllConstraintsMap();
        this.clone = clone;
    }

    public ExecModelLambdaPostProcessor(Map<String, CreatedClass> lambdaClasses,
                                        String packageName,
                                        String ruleClassName,
                                        Collection<String> imports,
                                        Collection<String> staticImports,
                                        Map<LambdaExpr, java.lang.reflect.Type> lambdaReturnTypes,
                                        Map<String, PredicateInformation> debugPredicateInformation,
                                        CompilationUnit clone) {
        this.lambdaClasses = lambdaClasses;
        this.packageName = packageName;
        this.ruleClassName = ruleClassName;
        this.imports = imports;
        this.staticImports = staticImports;
        this.lambdaReturnTypes = lambdaReturnTypes;
        this.debugPredicateInformation = debugPredicateInformation;
        this.clone = clone;
    }

    public void convertLambdas() {

        clone.findAll(MethodCallExpr.class, mc -> PatternExpressionBuilder.EXPR_CALL.equals(mc.getNameAsString()) ||
                                                  FlowExpressionBuilder.EXPR_CALL.equals(mc.getNameAsString()))
             .forEach(methodCallExpr1 -> {
                 if (containsTemporalPredicate(methodCallExpr1)) {
                     this.convertTemporalExpr(methodCallExpr1);
                 } else {
                     extractLambdaFromMethodCall(methodCallExpr1,
                                                 (exprId) -> new MaterializedLambdaPredicate(packageName, ruleClassName, getPredicateInformation(exprId)));
                 }
             });

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

        clone.findAll(MethodCallExpr.class, mc -> FROM_CALL.equals(mc.getNameAsString()) ||
                                                  REACTIVE_FROM_CALL.equals(mc.getNameAsString()))
             .forEach(this::convertFromCall);

        clone.findAll(MethodCallExpr.class, this::isExecuteNonNestedCall)
             .forEach(methodCallExpr -> {
                 List<MaterializedLambda.BitMaskVariable> bitMaskVariables = findBitMaskFields(methodCallExpr);
                 extractLambdaFromMethodCall(methodCallExpr, (a) -> new MaterializedLambdaConsequence(packageName, ruleClassName, bitMaskVariables));
             });
    }

    private PredicateInformation getPredicateInformation(Optional<String> exprId) {
        return exprId.flatMap(e -> Optional.ofNullable(debugPredicateInformation.get(e)))
                .orElse(PredicateInformation.EMPTY_PREDICATE_INFORMATION);
    }

    private void convertTemporalExpr(MethodCallExpr methodCallExpr) {
        // TemporalExpr methodCallExpr may have 2 lambdas
        methodCallExpr.getArguments().forEach(a -> {
            if (a.isLambdaExpr()) {
                LambdaExpr lambdaExpr = a.asLambdaExpr();
                Optional<MaterializedLambdaExtractor> extractorOpt = createMaterializedLambdaExtractor(lambdaExpr);
                if (!extractorOpt.isPresent()) {
                    logger.debug("Unable to create MaterializedLambdaExtractor for {}", lambdaExpr);
                } else {
                    MaterializedLambdaExtractor extractor = extractorOpt.get();
                    replaceLambda(lambdaExpr, (i) -> extractor, Optional.empty());
                }
            }
        });
    }

    private boolean containsTemporalPredicate(MethodCallExpr mc) {
        return mc.getArguments()
                 .stream()
                 .filter(MethodCallExpr.class::isInstance)
                 .map(MethodCallExpr.class::cast)
                 .map(DrlxParseUtil::findLastMethodInChain)
                 .map(MethodCallExpr::getNameAsString)
                 .anyMatch(name -> name.startsWith("D.") && ModelGenerator.temporalOperators.contains(name.substring(2)));
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
        Optional<String> exprId = methodCallExpr.findFirst(StringLiteralExpr.class).map(LiteralStringValueExpr::getValue);

        boolean first = true;
        for (Expression expr : methodCallExpr.getArguments()) {
            if (expr.isLambdaExpr()) {
                if (first) {
                    replaceLambda( expr.asLambdaExpr(), ( i ) -> new MaterializedLambdaExtractor( packageName, ruleClassName, returnType ), exprId );
                    first = false;
                } else {
                    replaceLambda( expr.asLambdaExpr(), ( i ) -> new MaterializedLambdaExtractor( packageName, ruleClassName, "java.lang.Object" ), exprId );
                }
            }
        }
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

        extractLambdaFromMethodCall(methodCallExpr, (i) -> new MaterializedLambdaExtractor(packageName, ruleClassName, returnType));
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
            .filter(MethodCallExpr.class::isInstance)
            .map(MethodCallExpr.class::cast)
            .filter(parentMethod -> parentMethod.getNameAsString().equals(BIND_AS_CALL))
            .findFirst();

        if (!bindAsMethodOpt.isPresent()) {
            logger.warn("Method 'as' is not found for {}", methodCallExpr);
            return; // not externalize
        }

        extractLambdaFromMethodCall(bindAsMethodOpt.get(), (i) -> new MaterializedLambdaExtractor(packageName, ruleClassName, returnType));
    }

    private void convertFromCall(MethodCallExpr methodCallExpr) {
        Optional<LambdaExpr> lambdaOpt = methodCallExpr.getArguments().stream().filter(Expression::isLambdaExpr).map(Expression::asLambdaExpr).findFirst();
        if (!lambdaOpt.isPresent()) {
            return; // Don't need to handle. e.g. D.from(var_$children)
        }
        LambdaExpr lambdaExpr = lambdaOpt.get();

        Optional<MaterializedLambdaExtractor> extractorOpt = createMaterializedLambdaExtractor(lambdaExpr);
        if (!extractorOpt.isPresent()) {
            logger.debug("Unable to create MaterializedLambdaExtractor for {}", lambdaExpr);
            return;
        }

        MaterializedLambdaExtractor extractor = extractorOpt.get();
        extractLambdaFromMethodCall(methodCallExpr, (i) -> extractor);
    }

    private Optional<MaterializedLambdaExtractor> createMaterializedLambdaExtractor(LambdaExpr lambdaExpr) {
        java.lang.reflect.Type returnType = lambdaReturnTypes.get(lambdaExpr);
        if (returnType == null) {
            return Optional.empty();
        }

        returnType = ClassUtil.boxTypePrimitive(returnType);

        String returnTypeStr;

        if (returnType instanceof Class) {
            returnTypeStr = ((Class<?>)returnType).getCanonicalName();
        } else if (returnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            java.lang.reflect.Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments.length != 1) {
                return Optional.empty();
            }
            java.lang.reflect.Type argType = actualTypeArguments[0];
            if (argType instanceof Class) {
                // java.util.List<org.drools.FromTest$MyPerson> has to be resolved to canonical name java.util.List<org.drools.FromTest.MyPerson>
                returnTypeStr = canonicalNameParameterizedType(parameterizedType, (Class<?>)argType);
            } else {
                return Optional.empty(); // e.g. java.util.Collection<V> (V is TypeVariable), nested ParameterizedType, GenericArrayType etc.
            }
        } else {
            return Optional.empty(); // e.g. GenericArrayType etc.
        }

        return Optional.of(new MaterializedLambdaExtractor(packageName, ruleClassName, returnTypeStr));
    }

    private String canonicalNameParameterizedType(ParameterizedType parameterizedType, Class<?> argType) {
        StringBuilder sb = new StringBuilder();
        sb.append(parameterizedType.getRawType().getTypeName());
        sb.append("<" + argType.getCanonicalName() + ">");
        return sb.toString();
    }

    private Optional<Type> findVariableType(NameExpr nameExpr) {
        return optionalToStream(nameExpr.findAncestor(MethodDeclaration.class))
            .flatMap(node -> node.findAll(VariableDeclarator.class).stream())
            .filter(node -> node.getName().equals(nameExpr.getName()))
            .map(VariableDeclarator::getType)
            .filter(ClassOrInterfaceType.class::isInstance)
            .map(ClassOrInterfaceType.class::cast)
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

    private void extractLambdaFromMethodCall(MethodCallExpr methodCallExpr, Function<Optional<String>, MaterializedLambda> lambdaExtractor) {
        // Assume first argument is the exprId
        Optional<String> exprId = methodCallExpr.findFirst(StringLiteralExpr.class).map(LiteralStringValueExpr::getValue);

        methodCallExpr.getArguments().forEach(a -> {
            if (a.isLambdaExpr()) {
                replaceLambda(a.asLambdaExpr(), lambdaExtractor, exprId);
            }
        });
    }

    private void replaceLambda(LambdaExpr lambdaExpr, Function<Optional<String>, MaterializedLambda> lambdaExtractor, Optional<String> exprId) {
        try {
            CreatedClass aClass = lambdaExtractor.apply(exprId).create(lambdaExpr.toString(), imports, staticImports);
            lambdaClasses.put(aClass.getClassNameWithPackage(), aClass);

            ClassOrInterfaceType type = StaticJavaParser.parseClassOrInterfaceType(aClass.getClassNameWithPackage());
            lambdaExpr.replace(lambdaInstance(type));
        } catch (DoNotConvertLambdaException e) {
            logger.debug("Cannot externalize lambdas {}", e.getMessage());
        }
    }
}
