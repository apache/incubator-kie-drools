/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator;

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
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.QueryDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.addon.TypeResolver;
import org.drools.core.base.CoreComponentsBuilder;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.AnnotationDefinition;
import org.drools.core.rule.Behavior;
import org.drools.core.time.TimeUtils;
import org.drools.model.Rule;
import org.drools.model.UnitData;
import org.drools.model.Variable;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.ParseExpressionErrorResult;
import org.drools.modelcompiler.builder.errors.UnknownDeclarationError;
import org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyper;
import org.drools.modelcompiler.builder.generator.expressiontyper.ExpressionTyperContext;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.internal.ruleunit.RuleUnitVariable;

import static java.util.stream.Collectors.toList;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static org.drools.core.impl.StatefulKnowledgeSessionImpl.DEFAULT_RULE_UNIT;
import static org.drools.modelcompiler.builder.PackageModel.DATE_TIME_FORMATTER_FIELD;
import static org.drools.modelcompiler.builder.PackageModel.DOMAIN_CLASSESS_METADATA_FILE_NAME;
import static org.drools.modelcompiler.builder.PackageModel.DOMAIN_CLASS_METADATA_INSTANCE;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.classNameToReferenceType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.classToReferenceType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ATTRIBUTE_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BUILD_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.DECLARATION_OF_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ENTRY_POINT_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.METADATA_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.RULE_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.SUPPLY_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.UNIT_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.UNIT_DATA_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.WINDOW_CALL;
import static org.drools.modelcompiler.util.ClassUtil.asJavaSourceName;
import static org.drools.modelcompiler.util.StringUtil.toId;
import static org.kie.internal.ruleunit.RuleUnitUtil.isLegacyRuleUnit;

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

    public static void generateModel(KnowledgeBuilderImpl kbuilder, InternalKnowledgePackage pkg, PackageDescr packageDescr, PackageModel packageModel, boolean isPattern) {
        TypeResolver typeResolver = pkg.getTypeResolver();
        initPackageModel( kbuilder, pkg, typeResolver, packageDescr, packageModel );

        for(RuleDescr descr : packageDescr.getRules()) {
            RuleContext context = new RuleContext(kbuilder, packageModel, typeResolver, isPattern);
            context.setDialectFromAttributes(packageDescr.getAttributes());
            if (descr instanceof QueryDescr) {
                QueryGenerator.processQueryDef(packageModel, (QueryDescr) descr, context);
            }
        }

        HashSet<RuleUnitDescription> ruleUnitDescriptions = new HashSet<>();

        for (RuleDescr descr : packageDescr.getRules()) {
            RuleContext context = new RuleContext(kbuilder, packageModel, typeResolver, isPattern);
            context.setDialectFromAttributes(packageDescr.getAttributes());
            if (descr instanceof QueryDescr) {
                QueryGenerator.processQuery(kbuilder, packageModel, (QueryDescr) descr);
            } else {
                processRule(kbuilder, packageModel, packageDescr, descr, context);
                RuleUnitDescription ruleUnitDescr = context.getRuleUnitDescr();
                if (ruleUnitDescr != null) ruleUnitDescriptions.add(ruleUnitDescr);
            }
        }

        for (RuleUnitDescription rud : ruleUnitDescriptions) {
            packageModel.addRuleUnit(rud);
        }
    }

    public static void initPackageModel( KnowledgeBuilderImpl kbuilder, InternalKnowledgePackage pkg, TypeResolver typeResolver, PackageDescr packageDescr, PackageModel packageModel ) {
        packageModel.addImports( pkg.getImports().keySet());
        packageModel.addStaticImports( pkg.getStaticImports());
        packageModel.addEntryPoints( packageDescr.getEntryPointDeclarations());
        packageModel.addGlobals( pkg );
        packageModel.addAccumulateFunctions( pkg.getAccumulateFunctions());
        packageModel.setInternalKnowledgePackage( pkg );
        new WindowReferenceGenerator( packageModel, typeResolver ).addWindowReferences( kbuilder, packageDescr.getWindowDeclarations());
        packageModel.addAllFunctions( packageDescr.getFunctions().stream().map(FunctionGenerator::toFunction).collect(toList()));
    }

    private static void processRule(KnowledgeBuilderImpl kbuilder, PackageModel packageModel, PackageDescr packageDescr, RuleDescr ruleDescr, RuleContext context) {
        context.setDescr(ruleDescr);
        context.addGlobalDeclarations(packageModel.getGlobals());
        context.setDialectFromAttributes(ruleDescr.getAttributes().values());

        for(Entry<String, Object> kv : ruleDescr.getNamedConsequences().entrySet()) {
            context.addNamedConsequence(kv.getKey(), kv.getValue().toString());
        }

        RuleUnitDescription ruleUnitDescr = context.getRuleUnitDescr();
        BlockStmt ruleVariablesBlock = new BlockStmt();

        if (isLegacyRuleUnit()) {
            createUnitData( context, ruleUnitDescr, ruleVariablesBlock );
        }

        new ModelGeneratorVisitor(context, packageModel).visit(getExtendedLhs(packageDescr, ruleDescr));
        final String ruleMethodName = "rule_" + toId(ruleDescr.getName());
        MethodDeclaration ruleMethod = new MethodDeclaration(NodeList.nodeList(Modifier.publicModifier(), Modifier.staticModifier()), toClassOrInterfaceType( Rule.class ), ruleMethodName);

        ruleMethod.setJavadocComment(" Rule name: " + ruleDescr.getName() + " ");

        VariableDeclarationExpr ruleVar = new VariableDeclarationExpr(toClassOrInterfaceType( Rule.class ), "rule");

        MethodCallExpr ruleCall = new MethodCallExpr(null, RULE_CALL);
        if (!ruleDescr.getNamespace().isEmpty()) {
            ruleCall.addArgument( new StringLiteralExpr( ruleDescr.getNamespace() ) );
        }
        ruleCall.addArgument( new StringLiteralExpr( ruleDescr.getName() ) );

        MethodCallExpr buildCallScope = ruleUnitDescr != null ?
                new MethodCallExpr(ruleCall, UNIT_CALL).addArgument( new ClassExpr( classNameToReferenceType(ruleUnitDescr.getCanonicalName()) ) ) :
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

        createVariables(kbuilder, ruleVariablesBlock, packageModel, context);
        ruleMethod.setBody(ruleVariablesBlock);

        MethodCallExpr executeCall = new Consequence(context).createCall(ruleDescr, ruleDescr.getConsequence().toString(), ruleVariablesBlock, false );
        buildCall.addArgument( executeCall );

        ruleVariablesBlock.addStatement(new AssignExpr(ruleVar, buildCall, AssignExpr.Operator.ASSIGN));

        ruleVariablesBlock.addStatement( new ReturnStmt("rule") );
        packageModel.putRuleMethod(ruleUnitDescr != null ? ruleUnitDescr.getSimpleName() : DEFAULT_RULE_UNIT, ruleMethod);
    }

    private static AndDescr getExtendedLhs(PackageDescr packageDescr, RuleDescr ruleDescr) {
        if (ruleDescr.getParentName() == null) {
            return ruleDescr.getLhs();
        }
        RuleDescr parent = packageDescr.getRules().stream()
                .filter( r -> r.getName().equals( ruleDescr.getParentName() ) ).findFirst()
                .orElseThrow( () -> new RuntimeException( "Rule " + ruleDescr.getName() + " extends an unknown rule " + ruleDescr.getParentName() ) );
        AndDescr extendedLhs = new AndDescr();
        getExtendedLhs(packageDescr, parent).getDescrs().forEach( extendedLhs::addDescr );
        ruleDescr.getLhs().getDescrs().forEach( extendedLhs::addDescr );
        return extendedLhs;
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
                        addDynamicAttributeArgument( context, attributeCall, value );
                    }
                    break;
                case "enabled":
                    if (value.equalsIgnoreCase( "true" ) || value.equalsIgnoreCase( "false" )) {
                        attributeCall.addArgument( value.toLowerCase() );
                    } else {
                        addDynamicAttributeArgument( context, attributeCall, value );
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
                case "timer":
                    attributeCall.addArgument( new StringLiteralExpr( value ) );
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

    private static void addDynamicAttributeArgument( RuleContext context, MethodCallExpr attributeCall, String value ) {
        ExpressionTyperContext expressionTyperContext = new ExpressionTyperContext();
        ExpressionTyper expressionTyper = new ExpressionTyper(context, Integer.class, null, false, expressionTyperContext);
        Expression salienceExpr = parseExpression( value );
        Optional<TypedExpression> typedExpression = expressionTyper.toTypedExpression(salienceExpr).getTypedExpression();
        if (typedExpression.isPresent()) {
            Expression lambda = generateLambdaWithoutParameters(expressionTyperContext.getUsedDeclarations(), typedExpression.get().getExpression(), true, Optional.empty());
            MethodCallExpr supplyCall = new MethodCallExpr(null, SUPPLY_CALL);
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
            metaAttributeCall.addArgument(new StringLiteralExpr(metaAttr));
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
                    metaAttributeCall.addArgument(new StringLiteralExpr(annValue.toString()));
                } else {
                    Map<String, Object> map = new HashMap<>( annotationDefinition.getValues().size() );
                    for ( String key : annotationDefinition.getValues().keySet() ) {
                        map.put( key, annotationDefinition.getPropertyValue( key ) );
                    }
                    metaAttributeCall.addArgument(objectAsJPExpression(map));
                }
            } else {
                if ( ad.hasValue() ) {
                    if ( ad.getValues().size() == 1 ) {
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

    private static void createUnitData(RuleContext context, RuleUnitDescription ruleUnitDescr, BlockStmt ruleVariablesBlock ) {
        if (ruleUnitDescr != null) {
            for (RuleUnitVariable unitVar : ruleUnitDescr.getUnitVarDeclarations()) {
                addUnitData(context, unitVar, ruleVariablesBlock);
            }
        }
    }

  private static void addUnitData(RuleContext context, RuleUnitVariable unitVar, BlockStmt ruleBlock) {
        Type declType = classToReferenceType(unitVar.getBoxedVarType());
        context.addRuleUnitVar( unitVar.getName(), unitVar.getDataSourceParameterType() );
        context.addRuleUnitVarOriginalType( unitVar.getName(), unitVar.getType() );

        ClassOrInterfaceType varType = toClassOrInterfaceType(UnitData.class);
        varType.setTypeArguments(declType);
        VariableDeclarationExpr var_ = new VariableDeclarationExpr(varType, context.getVar(unitVar.getName()), Modifier.finalModifier());

        MethodCallExpr unitDataCall = new MethodCallExpr(null, UNIT_DATA_CALL);

        unitDataCall.addArgument(new ClassExpr( declType ));
        unitDataCall.addArgument(new StringLiteralExpr(unitVar.getName()));

        AssignExpr var_assign = new AssignExpr(var_, unitDataCall, AssignExpr.Operator.ASSIGN);
        ruleBlock.addStatement(var_assign);
    }

    public static void createVariables(KnowledgeBuilderImpl kbuilder, BlockStmt block, PackageModel packageModel, RuleContext context) {
        for (DeclarationSpec decl : context.getAllDeclarations()) {
            boolean domainClass = packageModel.registerDomainClass( decl.getDeclarationClass() );
            if (!packageModel.getGlobals().containsKey(decl.getBindingId()) && !context.queryParameterWithName(p -> p.name.equals(decl.getBindingId())).isPresent()) {
                addVariable(kbuilder, block, decl, context, domainClass);
            }
        }
    }

    private static void addVariable(KnowledgeBuilderImpl kbuilder, BlockStmt ruleBlock, DeclarationSpec decl, RuleContext context, boolean domainClass) {
        if (decl.getDeclarationClass() == null) {
            kbuilder.addBuilderResult( new UnknownDeclarationError( decl.getBindingId() ) );
            return;
        }
        Type declType = classToReferenceType( decl.getDeclarationClass() );

        ClassOrInterfaceType varType = toClassOrInterfaceType(Variable.class);
        varType.setTypeArguments(declType);
        VariableDeclarationExpr var_ = new VariableDeclarationExpr(varType, context.getVar(decl.getBindingId()), Modifier.finalModifier());

        MethodCallExpr declarationOfCall = new MethodCallExpr(null, DECLARATION_OF_CALL);

        declarationOfCall.addArgument(new ClassExpr( decl.getBoxedType() ));

        if (domainClass) {
            String domainClassSourceName = asJavaSourceName( decl.getDeclarationClass() );
            declarationOfCall.addArgument( DOMAIN_CLASSESS_METADATA_FILE_NAME + context.getPackageModel().getPackageUUID() + "." + domainClassSourceName + DOMAIN_CLASS_METADATA_INSTANCE );
        }

        declarationOfCall.addArgument(new StringLiteralExpr(decl.getVariableName().orElse(decl.getBindingId())));

        decl.getDeclarationSource().ifPresent(declarationOfCall::addArgument);

        decl.getEntryPoint().ifPresent( ep -> {
            MethodCallExpr entryPointCall = new MethodCallExpr(null, ENTRY_POINT_CALL);
            entryPointCall.addArgument( new StringLiteralExpr(ep ) );
            declarationOfCall.addArgument( entryPointCall );
        } );
        for ( BehaviorDescr behaviorDescr : decl.getBehaviors() ) {
            MethodCallExpr windowCall = new MethodCallExpr(null, WINDOW_CALL);
            if ( Behavior.BehaviorType.TIME_WINDOW.matches(behaviorDescr.getSubType() ) ) {
                windowCall.addArgument( "org.drools.model.Window.Type.TIME" );
                windowCall.addArgument( "" + TimeUtils.parseTimeString(behaviorDescr.getParameters().get(0 ) ) );
            }
            if ( Behavior.BehaviorType.LENGTH_WINDOW.matches( behaviorDescr.getSubType() ) ) {
                windowCall.addArgument( "org.drools.model.Window.Type.LENGTH" );
                windowCall.addArgument( "" + Integer.valueOf( behaviorDescr.getParameters().get( 0 ) ) );
            }
            declarationOfCall.addArgument( windowCall );
        }

        AssignExpr var_assign = new AssignExpr(var_, declarationOfCall, AssignExpr.Operator.ASSIGN);
        ruleBlock.addStatement(var_assign);
    }
}
