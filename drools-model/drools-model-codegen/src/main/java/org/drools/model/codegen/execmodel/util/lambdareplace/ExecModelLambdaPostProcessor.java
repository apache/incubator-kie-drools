/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.util.lambdareplace;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.github.javaparser.printer.configuration.DefaultConfigurationOption;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.model.BitMask;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.generator.DrlxParseUtil;
import org.drools.model.codegen.execmodel.generator.ModelGenerator;
import org.drools.model.functions.PredicateInformation;
import org.drools.mvel.parser.printer.PrintUtil;
import org.kie.internal.builder.conf.ParallelLambdaExternalizationOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.ALPHA_INDEXED_BY_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.BETA_INDEXED_BY_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.BIND_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.DSL_NAMESPACE;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.EVAL_EXPR_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.EXECUTE_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.EXPR_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.FROM_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.NOT_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.REACTIVE_FROM_CALL;
import static org.drools.util.MethodUtils.boxTypePrimitive;
import static org.drools.util.StreamUtils.optionalToStream;

public class ExecModelLambdaPostProcessor {

    Logger logger = LoggerFactory.getLogger(ExecModelLambdaPostProcessor.class.getCanonicalName());

    private final Map<String, CreatedClass> lambdaClasses;
    private final String packageName;
    private final String ruleClassName;
    private final Collection<String> imports;
    private final Collection<String> staticImports;
    private final Map<LambdaExpr, java.lang.reflect.Type> lambdaReturnTypes;
    private final Map<String, PredicateInformation> debugPredicateInformation;
    private final CompilationUnit cu;
    private final boolean isParallel;

    private final List<Runnable> toBeReplacedLambdas = Collections.synchronizedList(new ArrayList<>());

    private static final DefaultPrinterConfiguration configuration = new DefaultPrinterConfiguration();

    static {
        configuration.addOption(new DefaultConfigurationOption(DefaultPrinterConfiguration.ConfigOption.END_OF_LINE_CHARACTER, "\n"));
    }

    public static final DefaultPrettyPrinter MATERIALIZED_LAMBDA_PRETTY_PRINTER = new DefaultPrettyPrinter(configuration);

    public ExecModelLambdaPostProcessor(PackageModel pkgModel, CompilationUnit cu) {
        this.lambdaClasses = pkgModel.getLambdaClasses();
        this.packageName = pkgModel.getName();
        this.ruleClassName = pkgModel.getRulesFileNameWithPackage();
        this.imports = pkgModel.getImports();
        this.staticImports = pkgModel.getStaticImports();
        this.lambdaReturnTypes = pkgModel.getLambdaReturnTypes();
        this.debugPredicateInformation = pkgModel.getAllConstraintsMap();
        this.cu = cu;
        this.isParallel = pkgModel.getConfiguration().getOption(ParallelLambdaExternalizationOption.KEY).isLambdaExternalizationParallel();
    }

    public ExecModelLambdaPostProcessor(String packageName,
                                        String ruleClassName,
                                        Collection<String> imports,
                                        Collection<String> staticImports,
                                        Map<LambdaExpr, java.lang.reflect.Type> lambdaReturnTypes,
                                        Map<String, PredicateInformation> debugPredicateInformation,
                                        CompilationUnit cu,
                                        boolean isParallel) {
        this.lambdaClasses = new ConcurrentHashMap<>();
        this.packageName = packageName;
        this.ruleClassName = ruleClassName;
        this.imports = imports;
        this.staticImports = staticImports;
        this.lambdaReturnTypes = lambdaReturnTypes;
        this.debugPredicateInformation = debugPredicateInformation;
        this.cu = cu;
        this.isParallel = isParallel;
    }

    public void convertLambdas() {
        if (isParallel) {
            convertLambdasWithForkJoinPool();
        } else {
            convertLambdasWithStream();
        }
    }

    private void convertLambdasWithForkJoinPool() {
        KnowledgeBuilderImpl.ForkJoinPoolHolder.COMPILER_POOL.submit(this::convertLambdasWithStream).join();
    }

    private void convertLambdasWithStream() {
        List<MethodCallExpr> exprMethods = new ArrayList<>();
        List<MethodCallExpr> indexMethods = new ArrayList<>();
        List<MethodCallExpr> bindMethods = new ArrayList<>();
        List<MethodCallExpr> fromMethods = new ArrayList<>();
        List<MethodCallExpr> executeMethods = new ArrayList<>();

        cu.walk(MethodCallExpr.class, mc -> {
            String methodName = mc.getNameAsString();
            if ( EXPR_CALL.equals(methodName) || EVAL_EXPR_CALL.equals(methodName) ) {
                exprMethods.add(mc);
            } else if ( ALPHA_INDEXED_BY_CALL.contains(methodName) || BETA_INDEXED_BY_CALL.contains(methodName) ) {
                indexMethods.add(mc);
            } else if ( BIND_CALL.equals(methodName) ) {
                bindMethods.add(mc);
            } else if ( FROM_CALL.equals(methodName) || REACTIVE_FROM_CALL.equals(methodName) ) {
                fromMethods.add(mc);
            } else if ( isExecuteNonNestedCall(mc) ) {
                executeMethods.add(mc);
            }
        });

        createStream(exprMethods)
                .forEach(methodCallExpr1 -> {
                    if (containsTemporalPredicate(methodCallExpr1)) {
                        this.convertTemporalExpr(methodCallExpr1);
                    } else {
                        extractLambdaFromMethodCall(methodCallExpr1,
                                (exprId) -> new MaterializedLambdaPredicate(packageName, ruleClassName, getPredicateInformation(exprId)));
                    }
                });

        createStream(indexMethods).forEach(this::convertIndexedByCall);

        createStream(bindMethods).forEach(this::convertBindCall);

        createStream(fromMethods).forEach(this::convertFromCall);

        createStream(executeMethods)
                .forEach(methodCallExpr -> {
                    List<MaterializedLambda.BitMaskVariable> bitMaskVariables = findBitMaskFields(methodCallExpr);
                    extractLambdaFromMethodCall(methodCallExpr, (a) -> new MaterializedLambdaConsequence(packageName, ruleClassName, bitMaskVariables));
                });

        toBeReplacedLambdas.forEach(Runnable::run);
    }

    private Stream<MethodCallExpr> createStream(List<MethodCallExpr> expressionLists) {
        return isParallel ? expressionLists.parallelStream() : expressionLists.stream();
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
                if (extractorOpt.isEmpty()) {
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
                .anyMatch(mce -> {
                              if (mce.getScope().isPresent() && mce.getScope().get().equals(DSL_NAMESPACE)) {
                                  String methodName = mce.getNameAsString();
                                  if (ModelGenerator.temporalOperators.contains(methodName)) {
                                      return true;
                                  } else if (methodName.equals(NOT_CALL)) {
                                      return containsTemporalPredicate(mce);
                                  }
                              }
                              return false;
                          }
                );
    }

    private boolean isExecuteNonNestedCall(MethodCallExpr mc) {
        Optional<MethodCallExpr> ancestor = mc.findAncestor(MethodCallExpr.class)
                .filter(a -> a.getNameAsString().equals(EXECUTE_CALL));

        return ancestor.isEmpty() && EXECUTE_CALL.equals(mc.getNameAsString());
    }

    private void convertIndexedByCall(MethodCallExpr methodCallExpr) {
        NodeList<Expression> arguments = methodCallExpr.getArguments();
        Expression firstArgument = arguments.get(0);

        if (!firstArgument.isClassExpr()) {
            logger.warn("argument is not ClassExpr. argument : {}, methodCallExpr : {}", firstArgument, methodCallExpr);
            return;
        }

        Optional<String> exprId = methodCallExpr.findFirst(StringLiteralExpr.class).map(LiteralStringValueExpr::getValue);

        Expression leftOperandExtractorLambda = arguments.get(3);
        if (leftOperandExtractorLambda.isLambdaExpr()) {
            replaceLambda(leftOperandExtractorLambda.asLambdaExpr(), id -> new MaterializedLambdaExtractor(packageName, ruleClassName, getType(firstArgument).clone()), exprId);
        }

        for (int i = 4; i < arguments.size(); i++) {
            Expression expr = arguments.get(i);
            if (expr.isLambdaExpr()) {
                replaceLambda(expr.asLambdaExpr(), id -> new MaterializedLambdaExtractor(packageName, ruleClassName, toClassOrInterfaceType(Object.class)), exprId);
            }
        }
    }

    private void convertBindCall(MethodCallExpr methodCallExpr) {
        Expression argument = methodCallExpr.getArgument(0);

        if (!argument.isNameExpr()) {
            logger.warn("argument is not NameExpr. argument : {}, methodCallExpr : {}", argument, methodCallExpr);
            return;
        }

        Optional<Type> optReturnType = findVariableType((NameExpr) argument);
        if (optReturnType.isEmpty()) {
            logger.warn("VariableDeclarator type was not found for {}, methodCallExpr : {}", argument, methodCallExpr);
            return;
        }

        extractLambdaFromMethodCall(methodCallExpr, (i) -> new MaterializedLambdaExtractor(packageName, ruleClassName, optReturnType.get().clone()));
    }

    private void convertFromCall(MethodCallExpr methodCallExpr) {
        Optional<LambdaExpr> lambdaOpt = methodCallExpr.getArguments().stream().filter(Expression::isLambdaExpr).map(Expression::asLambdaExpr).findFirst();
        if (lambdaOpt.isEmpty()) {
            return; // Don't need to handle. e.g. D.from(var_$children)
        }
        LambdaExpr lambdaExpr = lambdaOpt.get();

        Optional<MaterializedLambdaExtractor> extractorOpt = createMaterializedLambdaExtractor(lambdaExpr);
        if (extractorOpt.isEmpty()) {
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

        returnType = boxTypePrimitive(returnType);

        Type returnTypeJp;

        if (returnType instanceof Class) {
            returnTypeJp = toClassOrInterfaceType((Class<?>)returnType);
        } else if (returnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            java.lang.reflect.Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments.length != 1) {
                return Optional.empty();
            }
            java.lang.reflect.Type argType = actualTypeArguments[0];
            if (argType instanceof Class) {
                // java.util.List<org.drools.FromTest$MyPerson> has to be resolved to canonical name java.util.List<org.drools.FromTest.MyPerson>
                returnTypeJp = toClassOrInterfaceType(canonicalNameParameterizedType(parameterizedType, (Class<?>)argType));
            } else {
                return Optional.empty(); // e.g. java.util.Collection<V> (V is TypeVariable), nested ParameterizedType, GenericArrayType etc.
            }
        } else {
            return Optional.empty(); // e.g. GenericArrayType etc.
        }

        return Optional.of(new MaterializedLambdaExtractor(packageName, ruleClassName, returnTypeJp));
    }

    private String canonicalNameParameterizedType(ParameterizedType parameterizedType, Class<?> argType) {
        StringBuilder sb = new StringBuilder();
        sb.append(parameterizedType.getRawType().getTypeName());
        sb.append("<" + argType.getCanonicalName() + ">");
        return sb.toString();
    }

    private Optional<Type> findVariableType(NameExpr nameExpr) {
        return nameExpr.findAncestor(MethodDeclaration.class).flatMap(m -> findVariableType(nameExpr, m));
    }

    private Optional<Type> findVariableType(NameExpr nameExpr, MethodDeclaration m) {
        for (Statement s : m.getBody().get().getStatements()) {
            Optional<VariableDeclarator> vDecl = s.findFirst(VariableDeclarator.class);
            if (vDecl.isPresent() && vDecl.get().getName().equals(nameExpr.getName())) {
                return ((ClassOrInterfaceType)vDecl.get().getType()).getTypeArguments().map( args -> args.get(0) );
            }
        }
        return Optional.empty();
    }

    protected Type getType(Expression argument) {
        Type type = argument.asClassExpr().getType();
        return type.isPrimitiveType() ? type.asPrimitiveType().toBoxedType() : type;
    }

    private FieldAccessExpr lambdaInstance(ClassOrInterfaceType type) {
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
            String domainClassMetadata = PrintUtil.printNode(arguments.get(0));
            List<String> fields = arguments.subList(1, arguments.size()).stream().map(PrintUtil::printNode).collect(Collectors.toList());
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
            CreatedClass aClass = lambdaExtractor.apply(exprId).create(lambdaExpr.clone(), imports, staticImports);
            lambdaClasses.put(aClass.getClassNameWithPackage(), aClass);
            ClassOrInterfaceType type = toClassOrInterfaceType(aClass.getClassNameWithPackage());
            FieldAccessExpr lambdaInstance = lambdaInstance(type);
            toBeReplacedLambdas.add( () -> lambdaExpr.replace(lambdaInstance) );
        } catch (DoNotConvertLambdaException e) {
            logger.debug("Cannot externalize lambdas {}", e.getMessage());
        }
    }
}
