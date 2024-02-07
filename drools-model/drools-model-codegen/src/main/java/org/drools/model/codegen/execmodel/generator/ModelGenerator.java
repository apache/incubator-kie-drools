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
package org.drools.model.codegen.execmodel.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.drools.base.base.CoreComponentsBuilder;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.factmodel.AnnotationDefinition;
import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.builder.impl.TypeDeclarationContext;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.QueryDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.model.Rule;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.errors.InvalidExpressionErrorResult;
import org.drools.model.codegen.execmodel.errors.ParseExpressionErrorResult;
import org.drools.model.codegen.execmodel.generator.expressiontyper.ExpressionTyper;
import org.drools.model.codegen.execmodel.generator.expressiontyper.ExpressionTyperContext;
import org.drools.model.codegen.execmodel.generator.visitor.ModelGeneratorVisitor;
import org.drools.util.TypeResolver;
import org.kie.internal.builder.conf.ParallelRulesBuildThresholdOption;
import org.kie.internal.ruleunit.RuleUnitDescription;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static org.drools.kiesession.session.StatefulKnowledgeSessionImpl.DEFAULT_RULE_UNIT;
import static org.drools.model.codegen.execmodel.PackageModel.DATE_TIME_FORMATTER_FIELD;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toStringLiteral;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.ATTRIBUTE_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.BUILD_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.METADATA_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.RULE_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.SUPPLY_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.UNIT_CALL;
import static org.drools.model.codegen.execmodel.generator.DslMethodNames.createDslTopLevelMethod;
import static org.drools.model.codegen.execmodel.generator.RuleContext.DIALECT_ATTRIBUTE;
import static org.drools.modelcompiler.util.StringUtil.toId;
import static org.drools.modelcompiler.util.TimerUtil.validateTimer;

public class ModelGenerator {

    private static final Map<String, String> attributesMap = new HashMap<>();

    public static final Set<String> temporalOperators = new HashSet<>();

    static {
        attributesMap.put("no-loop",  "org.drools.model.Rule.Attribute.NO_LOOP");
        attributesMap.put("salience", "org.drools.model.Rule.Attribute.SALIENCE");
        attributesMap.put("enabled", "org.drools.model.Rule.Attribute.ENABLED");
        attributesMap.put("auto-focus", "org.drools.model.Rule.Attribute.AUTO_FOCUS");
        attributesMap.put("lock-on-active", "org.drools.model.Rule.Attribute.LOCK_ON_ACTIVE");
        attributesMap.put("agenda-group", "org.drools.model.Rule.Attribute.AGENDA_GROUP");
        attributesMap.put("activation-group", "org.drools.model.Rule.Attribute.ACTIVATION_GROUP");
        attributesMap.put("ruleflow-group", "org.drools.model.Rule.Attribute.RULEFLOW_GROUP");
        attributesMap.put("duration", "org.drools.model.Rule.Attribute.DURATION");
        attributesMap.put("timer", "org.drools.model.Rule.Attribute.TIMER");
        attributesMap.put("calendars", "org.drools.model.Rule.Attribute.CALENDARS");
        attributesMap.put("date-effective", "org.drools.model.Rule.Attribute.DATE_EFFECTIVE");
        attributesMap.put("date-expires", "org.drools.model.Rule.Attribute.DATE_EXPIRES");

        temporalOperators.add("before");
        temporalOperators.add("after");
        temporalOperators.add("coincides");
        temporalOperators.add("metby");
        temporalOperators.add("finishedby");
        temporalOperators.add("overlaps");
        temporalOperators.add("meets");
        temporalOperators.add("during");
        temporalOperators.add("finishes");
        temporalOperators.add("startedby");
        temporalOperators.add("overlappedby");
        temporalOperators.add("includes");
        temporalOperators.add("starts");
    }

    public static final boolean GENERATE_EXPR_ID = true;

    public static void generateModel( KnowledgeBuilderImpl knowledgeBuilder, InternalKnowledgePackage pkg, PackageDescr packageDescr, PackageModel packageModel) {
        generateModel(knowledgeBuilder, knowledgeBuilder, pkg, packageDescr, packageModel);
    }

    public static void generateModel( TypeDeclarationContext typeDeclarationContext, BuildResultCollector resultCollector,
                                      InternalKnowledgePackage pkg, PackageDescr packageDescr, PackageModel packageModel) {
        if (!packageDescr.getRules().isEmpty()) {
            packageModel.addRuleUnits(processRules( typeDeclarationContext, resultCollector, packageDescr, packageModel, pkg.getTypeResolver()));
        }
    }

    private static Set<RuleUnitDescription> processRules( TypeDeclarationContext typeDeclarationContext, BuildResultCollector resultCollector,
                                                          PackageDescr packageDescr, PackageModel packageModel, TypeResolver typeResolver) {
        Set<RuleUnitDescription> ruleUnitDescrs = new HashSet<>();

        for (RuleDescr descr : packageDescr.getRules()) {
            RuleContext context = new RuleContext(
                    typeDeclarationContext, resultCollector, packageModel, typeResolver, descr);
            if (context.getRuleUnitDescr() != null) {
                ruleUnitDescrs.add(context.getRuleUnitDescr());
            }
            context.setDialectFromAttribute(packageDescr.getAttribute( DIALECT_ATTRIBUTE ));
            if (descr instanceof QueryDescr) {
                QueryGenerator.processQueryDef(packageModel, context);
            }
        }

        if ( isParallelRulesBuild(typeDeclarationContext, packageDescr, packageModel) ) {
            List<RuleContext> ruleContexts = new ArrayList<>();
            int i = 0;
            for (RuleDescr ruleDescr : packageDescr.getRules()) {
                RuleContext context = new RuleContext(typeDeclarationContext, resultCollector, packageModel, typeResolver, ruleDescr, i++ );
                ruleContexts.add(context);
            }
            KnowledgeBuilderImpl.ForkJoinPoolHolder.COMPILER_POOL.submit(() ->
                    ruleContexts.parallelStream().forEach(context -> processRuleDescr(context, packageDescr))
            ).join();
        } else {
            int i = 0;
            for (RuleDescr ruleDescr : packageDescr.getRules()) {
                RuleContext context = new RuleContext(typeDeclarationContext, resultCollector, packageModel, typeResolver, ruleDescr, i++ );
                processRuleDescr(context, packageDescr);
            }
        }

        return ruleUnitDescrs;
    }

    private static boolean isParallelRulesBuild(TypeDeclarationContext typeDeclarationContext, PackageDescr packageDescr, PackageModel packageModel) {
        if ( packageModel.isReproducibleExecutableModelGeneration() ) {
            return false;
        }
        int parallelRulesBuildThreshold = typeDeclarationContext.getBuilderConfiguration().getOption(ParallelRulesBuildThresholdOption.KEY).getParallelRulesBuildThreshold();
        return parallelRulesBuildThreshold != -1 && packageDescr.getRules().size() > parallelRulesBuildThreshold;
    }

    private static void processRuleDescr(RuleContext context, PackageDescr packageDescr) {
        if (context.getRuleDescr() instanceof QueryDescr) {
            QueryGenerator.processQuery(context.getPackageModel(), (QueryDescr) context.getRuleDescr());
            return;
        }
        context.setDialectFromAttribute(packageDescr.getAttribute( DIALECT_ATTRIBUTE ));
        processRule(packageDescr, context);
    }

    private static void processRule(PackageDescr packageDescr, RuleContext context) {
        PackageModel packageModel = context.getPackageModel();
        RuleDescr ruleDescr = context.getRuleDescr();
        context.addGlobalDeclarations();
        context.setDialectFromAttribute(ruleDescr.getAttributes().get( DIALECT_ATTRIBUTE ));

        for(Entry<String, Object> kv : ruleDescr.getNamedConsequences().entrySet()) {
            context.addNamedConsequence(kv.getKey(), kv.getValue().toString());
        }

        BlockStmt ruleVariablesBlock = context.getRuleVariablesBlock();

        Optional<AndDescr> optExtendedLhs = getExtendedLhs(context, packageDescr, ruleDescr, new HashSet<>());
        if (optExtendedLhs.isEmpty()) {
            return;
        }
        new ModelGeneratorVisitor(context, packageModel).visit(optExtendedLhs.get());
        if (context.hasCompilationError()) {
            return;
        }

        final String ruleMethodName = "rule_" + toId(ruleDescr.getName());
        MethodDeclaration ruleMethod = new MethodDeclaration(NodeList.nodeList(Modifier.publicModifier(), Modifier.staticModifier()), toClassOrInterfaceType( Rule.class ), ruleMethodName);

        ruleMethod.setJavadocComment(" Rule name: " + ruleDescr.getName() + " ");

        VariableDeclarationExpr ruleVar = new VariableDeclarationExpr(toClassOrInterfaceType( Rule.class ), "rule");

        MethodCallExpr ruleCall = createDslTopLevelMethod(RULE_CALL);
        if (!ruleDescr.getNamespace().isEmpty()) {
            ruleCall.addArgument( toStringLiteral( ruleDescr.getNamespace() ) );
        }
        ruleCall.addArgument( toStringLiteral( ruleDescr.getName() ) );

        RuleUnitDescription ruleUnitDescr = context.getRuleUnitDescr();
        MethodCallExpr buildCallScope = ruleUnitDescr != null ?
                new MethodCallExpr(ruleCall, UNIT_CALL).addArgument( new ClassExpr( toClassOrInterfaceType(ruleUnitDescr.getCanonicalName()) ) ) :
                ruleCall;

        for (MethodCallExpr attributeExpr : ruleAttributes(context, ruleDescr)) {
            attributeExpr.setScope( buildCallScope );
            buildCallScope = attributeExpr;
        }

        for (MethodCallExpr metaAttributeExpr : ruleMetaAttributes(context, ruleDescr)) {
            metaAttributeExpr.setScope(buildCallScope);
            buildCallScope = metaAttributeExpr;
        }

        MethodCallExpr buildCall = new MethodCallExpr(buildCallScope, BUILD_CALL, NodeList.nodeList(context.getExpressions()));

        createVariables(ruleVariablesBlock, packageModel, context);
        ruleMethod.setBody(ruleVariablesBlock);

        MethodCallExpr executeCall = new Consequence(context).createCall(ruleDescr.getConsequence().toString(), ruleVariablesBlock, false );
        buildCall.addArgument( executeCall );

        ruleVariablesBlock.addStatement(new AssignExpr(ruleVar, buildCall, AssignExpr.Operator.ASSIGN));

        ruleVariablesBlock.addStatement( new ReturnStmt("rule") );
        packageModel.putRuleMethod(ruleUnitDescr != null ? ruleUnitDescr.getSimpleName() : DEFAULT_RULE_UNIT, ruleMethod, context.getRuleIndex());
    }

    private static Optional<AndDescr> getExtendedLhs(RuleContext context, PackageDescr packageDescr, RuleDescr ruleDescr, Set<RuleDescr> ruleDescrSet) {
        if (ruleDescrSet.contains(ruleDescr)) {
            context.addCompilationError(new DescrBuildError(packageDescr, ruleDescr, null, "Circular dependency in rules hierarchy " + ruleDescr.getName()));
            return Optional.empty();
        }
        ruleDescrSet.add(ruleDescr);

        if (ruleDescr.getParentName() == null) {
            return Optional.of(ruleDescr.getLhs());
        }
        Optional<RuleDescr> optParent = packageDescr.getRules().stream()
                .filter( r -> r.getName().equals( ruleDescr.getParentName() ) ).findFirst();

        if (optParent.isEmpty()) {
            context.addCompilationError(new DescrBuildError(packageDescr, ruleDescr, null, "Rule " + ruleDescr.getName() + " extends an unknown rule " + ruleDescr.getParentName()));
            return Optional.empty();
        }

        RuleDescr parentRuleDescr = optParent.get();
        AndDescr extendedLhs = new AndDescr();
        Optional<AndDescr> optParentExtendedLhs = getExtendedLhs(context, packageDescr, parentRuleDescr, ruleDescrSet);
        if (optParentExtendedLhs.isEmpty()) {
            // No need to add CompilationError because it should have been already added.
            return Optional.empty();
        }

        optParentExtendedLhs.get().getDescrs().forEach(extendedLhs::addDescr);
        ruleDescr.getLhs().getDescrs().forEach( extendedLhs::addDescr );
        return Optional.of(extendedLhs);
    }

    /**
     * Build a list of method calls, representing each needed {@link org.drools.model.impl.RuleBuilder#attribute(org.drools.model.Rule.Attribute, Object)}
     * starting from a drools-compiler {@link RuleDescr}.
     * The tuple represent the Rule Attribute expressed in JavParser form, and the attribute value expressed in JavaParser form.
     */
    private static List<MethodCallExpr> ruleAttributes(RuleContext context, RuleDescr ruleDescr) {
        final List<MethodCallExpr> ruleAttributes = new ArrayList<>();
        final Set<Entry<String, AttributeDescr>> excludingDialect =
                ruleDescr.getAttributes().entrySet()
                        .stream().filter(r -> !r.getKey().equals("dialect"))
                        .collect(Collectors.toSet());
        for (Entry<String, AttributeDescr> as : excludingDialect) {
            MethodCallExpr attributeCall = new MethodCallExpr(null, ATTRIBUTE_CALL);
            attributeCall.addArgument(attributesMap.get(as.getKey()));
            String value = as.getValue().getValue().trim();
            switch (as.getKey()) {
                case "salience":
                    try {
                        Integer.parseInt( value );
                        attributeCall.addArgument( value );
                    } catch (NumberFormatException nfe) {
                        addDynamicAttributeArgument( context, attributeCall, value, int.class );
                    }
                    break;
                case "enabled":
                    if (value.equalsIgnoreCase( "true" ) || value.equalsIgnoreCase( "false" )) {
                        attributeCall.addArgument( value.toLowerCase() );
                    } else {
                        addDynamicAttributeArgument( context, attributeCall, value, boolean.class );
                    }
                    break;
                case "no-loop":
                case "auto-focus":
                case "lock-on-active":
                    attributeCall.addArgument( value );
                    break;
                case "agenda-group":
                case "activation-group":
                case "ruleflow-group":
                case "duration":
                    attributeCall.addArgument( toStringLiteral( value ) );
                    break;
                case "timer":
                    if (validateTimer(value)) {
                        attributeCall.addArgument( toStringLiteral( value ) );
                    } else {
                        context.addCompilationError( new InvalidExpressionErrorResult(value) );
                    }
                    break;
                case "calendars":
                    if (value.startsWith( "[" )) {
                        value = value.substring( 1, value.length()-1 ).trim();
                    }
                    Expression arrayExpr = parseExpression("new String[] { " + value + " }");
                    attributeCall.addArgument( arrayExpr );
                    break;
                case "date-effective":
                case "date-expires":
                    attributeCall.addArgument( parseExpression(String.format("java.util.GregorianCalendar.from(java.time.LocalDate.parse(\"%s\", " + DATE_TIME_FORMATTER_FIELD + ").atStartOfDay(java.time.ZoneId.systemDefault()))", as.getValue().getValue())));
                    break;
                default:
                    throw new UnsupportedOperationException("Unhandled case for rule attribute: " + as.getKey());
            }
            ruleAttributes.add(attributeCall);
        }
        return ruleAttributes;
    }

    private static void addDynamicAttributeArgument( RuleContext context, MethodCallExpr attributeCall, String value, Class<?> requiredAttributeType ) {
        ExpressionTyperContext expressionTyperContext = new ExpressionTyperContext();
        ExpressionTyper expressionTyper = new ExpressionTyper(context, Integer.class, null, false, expressionTyperContext);
        Expression salienceExpr = parseExpression( value );
        Optional<TypedExpression> typedExpression = expressionTyper.toTypedExpression(salienceExpr).getTypedExpression();
        if (typedExpression.isPresent()) {
            Expression expr = typedExpression.get().getExpression();
            java.lang.reflect.Type exprType = typedExpression.get().getType();
            if (requiredAttributeType == int.class) {
                if (exprType == String.class) {
                    expr = new MethodCallExpr("org.drools.modelcompiler.util.EvaluationUtil.string2Int", expr);
                } else if (exprType == long.class || exprType == Long.class) {
                    expr = new MethodCallExpr(expr, "intValue");
                }
            }

            Expression lambda = generateLambdaWithoutParameters(expressionTyperContext.getUsedDeclarations(), expr, true, Optional.empty());
            MethodCallExpr supplyCall = createDslTopLevelMethod(SUPPLY_CALL);
            expressionTyperContext.getUsedDeclarations().stream()
                    .map(context::getVarExpr)
                    .forEach(supplyCall::addArgument);
            supplyCall.addArgument( lambda );
            attributeCall.addArgument( supplyCall );
        } else {
            context.addCompilationError( new ParseExpressionErrorResult(salienceExpr) );
        }
    }

    /**
     * Build a list of method calls, representing each needed {@link org.drools.model.impl.RuleBuilder#metadata(String, Object)}
     * starting from a drools-compiler {@link RuleDescr}.<br/>
     * Based on {@link org.drools.modelcompiler.KiePackagesBuilder#setRuleMetaAttributes(Rule, RuleImpl)} the reserved annotation keywords are:
     * Propagation, All, Direct.
     */
    private static List<MethodCallExpr> ruleMetaAttributes(RuleContext context, RuleDescr ruleDescr) {
        List<MethodCallExpr> ruleMetaAttributes = new ArrayList<>();
        for (String metaAttr : ruleDescr.getAnnotationNames()) {
            MethodCallExpr metaAttributeCall = new MethodCallExpr(METADATA_CALL);
            metaAttributeCall.addArgument(toStringLiteral(metaAttr));
            AnnotationDescr ad = ruleDescr.getAnnotation( metaAttr );
            String adFqn = ad.getFullyQualifiedName();
            if ("Propagation".equals(metaAttr)) { // legacy case, as explained in the javadoc annotation above, ref. DROOLS-5685
                metaAttributeCall.addArgument(parseExpression(org.kie.api.definition.rule.Propagation.Type.class.getCanonicalName() + "." + ad.getSingleValueAsString()));
            } else if (adFqn != null) {
                AnnotationDefinition annotationDefinition;
                try {
                    annotationDefinition = AnnotationDefinition.build(context.getTypeResolver().resolveType(adFqn),
                                                                      ad.getValueMap(),
                                                                      context.getTypeResolver());
                } catch (NoSuchMethodException | ClassNotFoundException e) {
                    throw new RuntimeException( e );
                }
                if ( annotationDefinition.getValues().size() == 1 && annotationDefinition.getValues().containsKey( AnnotationDescr.VALUE ) ) {
                    Object annValue = annotationDefinition.getPropertyValue(AnnotationDescr.VALUE);
                    metaAttributeCall.addArgument(toStringLiteral(annValue.toString()));
                } else {
                    Map<String, Object> map = new HashMap<>( annotationDefinition.getValues().size() );
                    for ( String key : annotationDefinition.getValues().keySet() ) {
                        map.put( key, annotationDefinition.getPropertyValue( key ) );
                    }
                    metaAttributeCall.addArgument(objectAsJPExpression(map));
                }
            } else {
                if ( ad.hasValue() ) {
                    if ( ad.getValueMap().size() == 1 ) {
                        metaAttributeCall.addArgument(annotationSingleValueExpression(ad));
                    } else {
                        metaAttributeCall.addArgument(objectAsJPExpression(ad.getValueMap()));
                    }
                } else {
                    metaAttributeCall.addArgument(new NullLiteralExpr());
                }
            }
            ruleMetaAttributes.add(metaAttributeCall);
        }
        return ruleMetaAttributes;
    }

    private static Expression objectAsJPExpression(Object annValue) {
        if (annValue instanceof String) {
            StringLiteralExpr aStringLiteral = new StringLiteralExpr();
            aStringLiteral.setString(annValue.toString()); // use the setter method in order for the string literal be properly escaped.
            return aStringLiteral;
        } else if (annValue instanceof Number) {
            return parseExpression(annValue.toString());
        } else if (annValue instanceof Map) {
            throw new UnsupportedOperationException("cannot define a canonical representation for a java.util.Map yet.");
        } else {
            throw new UnsupportedOperationException("I was unable to define a canonical String representation to give to JP yet about: " + annValue);
        }
    }

    private static Expression annotationSingleValueExpression(AnnotationDescr ad) {
        // for backward compatibility, if something is not an expression, we return an string as is
        if (resolveValueWithMVEL(ad.getSingleValueAsString()).isPresent()) {
            return parseExpression(ad.getSingleValueAsString()); // then in the produced exec model we leave the original expression statement, not the pre-computed MVEL result.
        } else {
            return objectAsJPExpression(ad.getSingleValueAsString()); // backward compatibility case: @ann(Hello World!) or @ann( john_doe ) would result in a String literal for "Hello World!" or "john_doe"
        }
    }

    private static Optional<Object> resolveValueWithMVEL(String value) {
        // try to resolve as an expression:
        try {
            Object result = CoreComponentsBuilder.get().getMVELExecutor().eval(value);
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static void createVariables(BlockStmt block, PackageModel packageModel, RuleContext context) {
        for (DeclarationSpec decl : context.getAllDeclarations()) {
            decl.registerOnPackage(packageModel, context, block);
        }
    }

}
