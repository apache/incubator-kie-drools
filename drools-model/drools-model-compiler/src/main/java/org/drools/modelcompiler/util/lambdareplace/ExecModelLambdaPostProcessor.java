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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
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
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.model.BitMask;
import org.drools.model.functions.PredicateInformation;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.ModelGenerator;
import org.drools.modelcompiler.util.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.modelcompiler.builder.generator.DslMethodNames.ALPHA_INDEXED_BY_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BETA_INDEXED_BY_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BIND_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.EVAL_EXPR_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.EXECUTE_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.EXPR_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.FROM_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.REACTIVE_FROM_CALL;
import static org.drools.modelcompiler.util.StreamUtils.optionalToStream;

public class ExecModelLambdaPostProcessor {

    Logger logger = LoggerFactory.getLogger(ExecModelLambdaPostProcessor.class.getCanonicalName());

    private final String packageName;
    private final String ruleClassName;
    private final Collection<String> imports;
    private final Collection<String> staticImports;
    private final Map<LambdaExpr, java.lang.reflect.Type> lambdaReturnTypes;
    private final Map<String, PredicateInformation> debugPredicateInformation;
    private final CompilationUnit cu;
    private final boolean isParallel;

    private static final PrettyPrinterConfiguration configuration = new PrettyPrinterConfiguration();

    static {
        configuration.setEndOfLineCharacter("\n"); // hashes will be stable also while testing on windows
    }

    public static final PrettyPrinter MATERIALIZED_LAMBDA_PRETTY_PRINTER = new PrettyPrinter(configuration);

    public ExecModelLambdaPostProcessor(PackageModel pkgModel,
                                        CompilationUnit cu) {
        this.packageName = pkgModel.getName();
        this.ruleClassName = pkgModel.getRulesFileNameWithPackage();
        this.imports = pkgModel.getImports();
        this.staticImports = pkgModel.getStaticImports();
        this.lambdaReturnTypes = pkgModel.getLambdaReturnTypes();
        this.debugPredicateInformation = pkgModel.getAllConstraintsMap();
        this.cu = cu;
        this.isParallel = pkgModel.getConfiguration().isParallelLambdaExternalization();
    }

    public ExecModelLambdaPostProcessor(String packageName,
                                        String ruleClassName,
                                        Collection<String> imports,
                                        Collection<String> staticImports,
                                        Map<LambdaExpr, java.lang.reflect.Type> lambdaReturnTypes,
                                        Map<String, PredicateInformation> debugPredicateInformation,
                                        CompilationUnit cu,
                                        boolean isParallel) {
        this.packageName = packageName;
        this.ruleClassName = ruleClassName;
        this.imports = imports;
        this.staticImports = staticImports;
        this.lambdaReturnTypes = lambdaReturnTypes;
        this.debugPredicateInformation = debugPredicateInformation;
        this.cu = cu;
        this.isParallel = isParallel;
    }

    public List<ReplacedLambdaResult> convertLambdas() {
        if (isParallel) {
            return convertLambdasWithForkJoinPool();
        } else {
            return convertLambdasWithStream();
        }
    }

    private List<ReplacedLambdaResult> convertLambdasWithForkJoinPool() {
        return KnowledgeBuilderImpl.ForkJoinPoolHolder.COMPILER_POOL.submit(this::convertLambdasWithStream).join();
    }

    private List<ReplacedLambdaResult> convertLambdasWithStream() {
        List<MethodCallExpr> exprMethods = new ArrayList<>();
        List<MethodCallExpr> alphaIndexMethods = new ArrayList<>();
        List<MethodCallExpr> betaIndexMethods = new ArrayList<>();
        List<MethodCallExpr> bindMethods = new ArrayList<>();
        List<MethodCallExpr> fromMethods = new ArrayList<>();
        List<MethodCallExpr> executeMethods = new ArrayList<>();

        cu.walk(MethodCallExpr.class, mc -> {
            String methodName = mc.getNameAsString();
            if ( EXPR_CALL.equals(methodName) || EVAL_EXPR_CALL.equals(methodName) ){
                exprMethods.add(mc);
            } else if ( ALPHA_INDEXED_BY_CALL.contains(methodName) ){
                alphaIndexMethods.add(mc);
            } else if ( BETA_INDEXED_BY_CALL.contains(methodName) ){
                betaIndexMethods.add(mc);
            } else if ( BIND_CALL.equals(methodName) ){
                bindMethods.add(mc);
            } else if ( FROM_CALL.equals(methodName) || REACTIVE_FROM_CALL.equals(methodName) ){
                fromMethods.add(mc);
            } else if ( isExecuteNonNestedCall(mc) ){
                executeMethods.add(mc);
            }
        });

        Stream<ReplacedLambdaResult> resultsFromExpr = createStream(exprMethods)
                .flatMap( methodCallExpr1 ->
                        containsTemporalPredicate(methodCallExpr1) ?
                                convertTemporalExpr(methodCallExpr1) :
                                extractLambdaFromMethodCall(methodCallExpr1, (exprId) -> new MaterializedLambdaPredicate(packageName, ruleClassName, getPredicateInformation(exprId))) );

        Stream<ReplacedLambdaResult> resultsFromAlphaIndexedBy = createStream(alphaIndexMethods).flatMap(this::convertIndexedByCall);

        Stream<ReplacedLambdaResult> resultsFromBetaIndexedBy = createStream(betaIndexMethods).flatMap(this::convertIndexedByCall);

        Stream<ReplacedLambdaResult> resultsFromBind = createStream(bindMethods).flatMap(this::convertBindCall);

        Stream<ReplacedLambdaResult> resultsFromFrom = createStream(fromMethods).flatMap(this::convertFromCall);

        Stream<ReplacedLambdaResult> resultsFromExecuteCall = createStream(executeMethods)
                .flatMap(methodCallExpr -> {
                 List<MaterializedLambda.BitMaskVariable> bitMaskVariables = findBitMaskFields(methodCallExpr);
                 return extractLambdaFromMethodCall(methodCallExpr, (a) -> new MaterializedLambdaConsequence(packageName, ruleClassName, bitMaskVariables));
             });

        return Stream.of(resultsFromAlphaIndexedBy,
                         resultsFromBetaIndexedBy,
                         resultsFromBind,
                         resultsFromExecuteCall,
                         resultsFromExpr,
                         resultsFromFrom)
                .reduce(Stream::concat)
                .orElseGet(Stream::empty)
                .collect(Collectors.toList());
    }

    private Stream<MethodCallExpr> createStream(List<MethodCallExpr> expressionLists) {
        return isParallel ? expressionLists.parallelStream() : expressionLists.stream();
    }

    private PredicateInformation getPredicateInformation(Optional<String> exprId) {
        return exprId.flatMap(e -> Optional.ofNullable(debugPredicateInformation.get(e)))
                .orElse(PredicateInformation.EMPTY_PREDICATE_INFORMATION);
    }

    private Stream<ReplacedLambdaResult> convertTemporalExpr(MethodCallExpr methodCallExpr) {
        // TemporalExpr methodCallExpr may have 2 lambdas
        return methodCallExpr.getArguments().stream().flatMap(a -> {
            if (a.isLambdaExpr()) {
                LambdaExpr lambdaExpr = a.asLambdaExpr();
                Optional<MaterializedLambdaExtractor> extractorOpt = createMaterializedLambdaExtractor(lambdaExpr);
                if (!extractorOpt.isPresent()) {
                    logger.debug("Unable to create MaterializedLambdaExtractor for {}", lambdaExpr);
                    return empty();
                } else {
                    MaterializedLambdaExtractor extractor = extractorOpt.get();
                    return replaceLambda(lambdaExpr, i -> extractor, Optional.empty());
                }
            }
            return empty();
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

    private Stream<ReplacedLambdaResult> convertIndexedByCall(MethodCallExpr methodCallExpr) {
        Expression argument = methodCallExpr.getArgument(0);

        if (!argument.isClassExpr()) {
            logger.warn("argument is not ClassExpr. argument : {}, methodCallExpr : {}", argument, methodCallExpr);
            return empty();
        }

        String returnType = getType(argument).asString();
        Optional<String> exprId = methodCallExpr.findFirst(StringLiteralExpr.class).map(LiteralStringValueExpr::getValue);

        List<LambdaExpr> allLambdaArguments = methodCallExpr.getArguments().stream().filter(Expression::isLambdaExpr)
                .map(Expression::asLambdaExpr)
                .collect(Collectors.toList());

        Stream<ReplacedLambdaResult> firstArgumentResult =
                optionalToStream(allLambdaArguments.stream().findFirst())
                .flatMap(a -> replaceLambda(a, i -> new MaterializedLambdaExtractor(packageName, ruleClassName, returnType), exprId));

        Deque<LambdaExpr> allButFirsts = new ArrayDeque<>(allLambdaArguments);
        if (!allButFirsts.isEmpty()) {
            allButFirsts.removeFirst();
        }

        Stream<ReplacedLambdaResult> otherArguments =
                allButFirsts.stream()
                        .flatMap(expr -> replaceLambda(expr.asLambdaExpr(),
                                                       i -> new MaterializedLambdaExtractor(packageName, ruleClassName, "java.lang.Object"), exprId)
                        );

        return Stream.concat(firstArgumentResult, otherArguments);
    }

    private Stream<ReplacedLambdaResult> empty() {
        return Stream.empty();
    }

    private Stream<ReplacedLambdaResult>  convertBindCall(MethodCallExpr methodCallExpr) {
        Expression argument = methodCallExpr.getArgument(0);

        if (!argument.isNameExpr()) {
            logger.warn("argument is not NameExpr. argument : {}, methodCallExpr : {}", argument, methodCallExpr);
            return empty();
        }

        Optional<Type> optType = findVariableType((NameExpr) argument);
        if (!optType.isPresent()) {
            logger.warn("VariableDeclarator type was not found for {}, methodCallExpr : {}", argument, methodCallExpr);
            return empty();
        }
        String returnType = optType.get().asString();

        return extractLambdaFromMethodCall(methodCallExpr, (i) -> new MaterializedLambdaExtractor(packageName, ruleClassName, returnType));
    }

    private Stream<ReplacedLambdaResult>  convertFromCall(MethodCallExpr methodCallExpr) {
        Optional<LambdaExpr> lambdaOpt = methodCallExpr.getArguments().stream().filter(Expression::isLambdaExpr).map(Expression::asLambdaExpr).findFirst();
        if (!lambdaOpt.isPresent()) {
            return empty(); // Don't need to handle. e.g. D.from(var_$children)
        }
        LambdaExpr lambdaExpr = lambdaOpt.get();

        Optional<MaterializedLambdaExtractor> extractorOpt = createMaterializedLambdaExtractor(lambdaExpr);
        if (!extractorOpt.isPresent()) {
            logger.debug("Unable to create MaterializedLambdaExtractor for {}", lambdaExpr);
            return empty();
        }

        MaterializedLambdaExtractor extractor = extractorOpt.get();
        return extractLambdaFromMethodCall(methodCallExpr, (i) -> extractor);
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
            String domainClassMetadata = arguments.get(0).toString();
            List<String> fields = arguments.subList(1, arguments.size()).stream().map(Expression::toString).collect(Collectors.toList());
            return new MaterializedLambda.BitMaskVariableWithFields(domainClassMetadata, fields, maskName);
        }
    }

    private Stream<? extends AssignExpr> findAssignExpr(VariableDeclarator vd) {
        return optionalToStream(vd.findAncestor(AssignExpr.class));
    }

    private Stream<ReplacedLambdaResult> extractLambdaFromMethodCall(MethodCallExpr methodCallExpr, Function<Optional<String>, MaterializedLambda> lambdaExtractor) {
        // Assume first argument is the exprId
        Optional<String> exprId = methodCallExpr.findFirst(StringLiteralExpr.class).map(LiteralStringValueExpr::getValue);

        return methodCallExpr.getArguments().stream().flatMap(a -> {
            if (a.isLambdaExpr()) {
                return replaceLambda(a.asLambdaExpr(), lambdaExtractor, exprId);
            }

            return empty();
        });
    }

    private Stream<ReplacedLambdaResult> replaceLambda(LambdaExpr lambdaExpr, Function<Optional<String>, MaterializedLambda> lambdaExtractor, Optional<String> exprId) {
        try {
            CreatedClass externalisedLambda = lambdaExtractor.apply(exprId).create(lambdaExpr.toString(), imports, staticImports);
            ClassOrInterfaceType type = StaticJavaParser.parseClassOrInterfaceType(externalisedLambda.getClassNameWithPackage());
            return Stream.of(new ReplacedLambdaResult(lambdaExpr, lambdaInstance(type), externalisedLambda));
        } catch (DoNotConvertLambdaException e) {
            logger.debug("Cannot externalize lambdas {}", e.getMessage());
        }
        return Stream.empty();
    }

    public static class ReplacedLambdaResult {

        private final LambdaExpr lambdaToBeReplaced;
        private final FieldAccessExpr externalisedLambdaCall;
        private final CreatedClass externalisedLambda;

        public ReplacedLambdaResult(LambdaExpr lambdaToBeReplaced, FieldAccessExpr externalisedLambdaCall, CreatedClass externalisedLambda) {
            this.lambdaToBeReplaced = lambdaToBeReplaced;
            this.externalisedLambdaCall = externalisedLambdaCall;
            this.externalisedLambda = externalisedLambda;
        }

        public void replaceLambda() {
            lambdaToBeReplaced.replace(externalisedLambdaCall);
        }

        public CreatedClass getExternalisedLambda() {
            return externalisedLambda;
        }
    }
}
