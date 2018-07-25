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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.QueryDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.factmodel.AnnotationDefinition;
import org.drools.core.rule.Behavior;
import org.drools.core.ruleunit.RuleUnitDescr;
import org.drools.core.time.TimeUtils;
import org.drools.core.util.MVELSafeHelper;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.body.MethodDeclaration;
import org.drools.javaparser.ast.expr.AssignExpr;
import org.drools.javaparser.ast.expr.ClassExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NullLiteralExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.javaparser.ast.expr.VariableDeclarationExpr;
import org.drools.javaparser.ast.stmt.BlockStmt;
import org.drools.javaparser.ast.stmt.ReturnStmt;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.javaparser.ast.type.Type;
import org.drools.model.Rule;
import org.drools.model.UnitData;
import org.drools.model.Variable;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.UnknownDeclarationError;
import org.drools.modelcompiler.builder.generator.RuleContext.RuleDialect;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;

import static java.util.stream.Collectors.toList;

import static org.drools.javaparser.JavaParser.parseExpression;
import static org.drools.modelcompiler.builder.PackageModel.DATE_TIME_FORMATTER_FIELD;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.classToReferenceType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ATTRIBUTE_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BUILD_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.DECLARATION_OF_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ENTRY_POINT_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.METADATA_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.RULE_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.UNIT_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.UNIT_DATA_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.WINDOW_CALL;
import static org.drools.modelcompiler.util.ClassUtil.toRawClass;
import static org.drools.modelcompiler.util.StringUtil.toId;

public class ModelGenerator {

    private static final Map<String, Expression> attributesMap = new HashMap<>();

    public static final Set<String> temporalOperators = new HashSet<>();

    static {
        attributesMap.put("no-loop", parseExpression("Rule.Attribute.NO_LOOP"));
        attributesMap.put("salience", parseExpression("Rule.Attribute.SALIENCE"));
        attributesMap.put("enabled", parseExpression("Rule.Attribute.ENABLED"));
        attributesMap.put("auto-focus", parseExpression("Rule.Attribute.AUTO_FOCUS"));
        attributesMap.put("lock-on-active", parseExpression("Rule.Attribute.LOCK_ON_ACTIVE"));
        attributesMap.put("agenda-group", parseExpression("Rule.Attribute.AGENDA_GROUP"));
        attributesMap.put("activation-group", parseExpression("Rule.Attribute.ACTIVATION_GROUP"));
        attributesMap.put("ruleflow-group", parseExpression("Rule.Attribute.RULEFLOW_GROUP"));
        attributesMap.put("duration", parseExpression("Rule.Attribute.DURATION"));
        attributesMap.put("timer", parseExpression("Rule.Attribute.TIMER"));
        attributesMap.put("calendars", parseExpression("Rule.Attribute.CALENDARS"));
        attributesMap.put("date-effective", parseExpression("Rule.Attribute.DATE_EFFECTIVE"));
        attributesMap.put("date-expires", parseExpression("Rule.Attribute.DATE_EXPIRES"));

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
        packageModel.addImports(pkg.getImports().keySet());
        packageModel.addStaticImports(pkg.getStaticImports());
        packageModel.addEntryPoints(packageDescr.getEntryPointDeclarations());
        packageModel.addGlobals(pkg.getGlobals());
        packageModel.addAccumulateFunctions(pkg.getAccumulateFunctions());
        packageModel.setInternalKnowledgePackage(pkg);
        new WindowReferenceGenerator(packageModel, typeResolver).addWindowReferences(kbuilder, packageDescr.getWindowDeclarations());
        packageModel.addAllFunctions(packageDescr.getFunctions().stream().map(FunctionGenerator::toFunction).collect(toList()));

        for(RuleDescr descr : packageDescr.getRules()) {
            if (descr instanceof QueryDescr) {
                QueryGenerator.processQueryDef( kbuilder, typeResolver, packageModel, (QueryDescr) descr, isPattern);
            }
        }

        for (RuleDescr descr : packageDescr.getRules()) {
            if (descr instanceof QueryDescr) {
                QueryGenerator.processQuery(kbuilder, packageModel, (QueryDescr) descr);
            } else {
                processRule(kbuilder, typeResolver, packageModel, packageDescr, descr, isPattern);
            }
        }
    }


    private static void processRule(KnowledgeBuilderImpl kbuilder, TypeResolver typeResolver, PackageModel packageModel, PackageDescr packageDescr, RuleDescr ruleDescr, boolean isPattern) {
        RuleContext context = new RuleContext(kbuilder, packageModel, ruleDescr,  typeResolver, isPattern);
        context.addGlobalDeclarations(packageModel.getGlobals());

        for(Entry<String, Object> kv : ruleDescr.getNamedConsequences().entrySet()) {
            context.addNamedConsequence(kv.getKey(), kv.getValue().toString());
        }

        setDialectFromRuleDescr(context, ruleDescr);

        RuleUnitDescr ruleUnitDescr = context.getRuleUnitDescr();
        BlockStmt ruleVariablesBlock = new BlockStmt();
        createUnitData( context, ruleUnitDescr, ruleVariablesBlock );

        new ModelGeneratorVisitor(context, packageModel).visit(getExtendedLhs(packageDescr, ruleDescr));
        final String ruleMethodName = "rule_" + toId(ruleDescr.getName());
        MethodDeclaration ruleMethod = new MethodDeclaration(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), toClassOrInterfaceType( Rule.class ), ruleMethodName);

        ruleMethod.setJavadocComment(" Rule name: " + ruleDescr.getName() + " ");

        VariableDeclarationExpr ruleVar = new VariableDeclarationExpr(toClassOrInterfaceType( Rule.class ), "rule");

        MethodCallExpr ruleCall = new MethodCallExpr(null, RULE_CALL);
        if (!ruleDescr.getNamespace().isEmpty()) {
            ruleCall.addArgument( new StringLiteralExpr( ruleDescr.getNamespace() ) );
        }
        ruleCall.addArgument( new StringLiteralExpr( ruleDescr.getName() ) );

        MethodCallExpr buildCallScope = ruleUnitDescr != null ?
                new MethodCallExpr(ruleCall, UNIT_CALL).addArgument( new ClassExpr( classToReferenceType(ruleUnitDescr.getRuleUnitClass()) ) ) :
                ruleCall;

        for (MethodCallExpr attributeExpr : ruleAttributes(ruleDescr)) {
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
        packageModel.putRuleMethod(ruleMethodName, ruleMethod);
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
    private static List<MethodCallExpr> ruleAttributes(RuleDescr ruleDescr) {
        final List<MethodCallExpr> ruleAttributes = new ArrayList<>();
        final Set<Entry<String, AttributeDescr>> excludingDialect =
                ruleDescr.getAttributes().entrySet()
                        .stream().filter(r -> !r.getKey().equals("dialect"))
                        .collect(Collectors.toSet());
        for (Entry<String, AttributeDescr> as : excludingDialect) {
            MethodCallExpr attributeCall = new MethodCallExpr(null, ATTRIBUTE_CALL);
            attributeCall.addArgument(attributesMap.get(as.getKey()));
            switch (as.getKey()) {
                case "no-loop":
                case "salience":
                case "enabled":
                case "auto-focus":
                case "lock-on-active":
                    attributeCall.addArgument( parseExpression(as.getValue().getValue()));
                    break;
                case "agenda-group":
                case "activation-group":
                case "ruleflow-group":
                case "duration":
                case "timer":
                    attributeCall.addArgument(new StringLiteralExpr( as.getValue().getValue()) );
                    break;
                case "calendars":
                    String value = as.getValue().getValue().trim();
                    if (value.startsWith( "[" )) {
                        value = value.substring( 1, value.length()-1 ).trim();
                    }
                    Expression arrayExpr = parseExpression("new String[] { " + value + " }");
                    attributeCall.addArgument( arrayExpr );
                    break;
                case "date-effective":
                case "date-expires":
                    attributeCall.addArgument( parseExpression(String.format("GregorianCalendar.from(LocalDate.parse(\"%s\", " + DATE_TIME_FORMATTER_FIELD + ").atStartOfDay(ZoneId.systemDefault()))", as.getValue().getValue())));
                    break;
                default:
                    throw new UnsupportedOperationException("Unhandled case for rule attribute: " + as.getKey());
            }
            ruleAttributes.add(attributeCall);
        }
        return ruleAttributes;
    }

    public static void setDialectFromRuleDescr(RuleContext context, RuleDescr ruleDescr) {
        for (Entry<String, AttributeDescr> as : ruleDescr.getAttributes().entrySet()) {
            if (as.getKey().equals("dialect")) {
                if (as.getValue().getValue().equals("mvel")) {
                    context.setRuleDialect(RuleDialect.MVEL);
                }
                return;
            }
        }
    }

    /**
     * Build a list of method calls, representing each needed {@link org.drools.model.impl.RuleBuilder#metadata(String, Object)}
     * starting from a drools-compiler {@link RuleDescr}.
     */
    private static List<MethodCallExpr> ruleMetaAttributes(RuleContext context, RuleDescr ruleDescr) {
        List<MethodCallExpr> ruleMetaAttributes = new ArrayList<>();
        for (String metaAttr : ruleDescr.getAnnotationNames()) {
            MethodCallExpr metaAttributeCall = new MethodCallExpr(METADATA_CALL);
            metaAttributeCall.addArgument(new StringLiteralExpr(metaAttr));
            AnnotationDescr ad = ruleDescr.getAnnotation( metaAttr );
            String adFqn = ad.getFullyQualifiedName();
            if (adFqn != null) {
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
                        metaAttributeCall.addArgument(objectAsJPExpression(resolveValue(ad.getSingleValueAsString())));
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

    private static Object resolveValue(String value) {
        // for backward compatibility, if something is not an expression, we return an string as is
        Object result = value;
        // try to resolve as an expression:
        try {
            result = MVELSafeHelper.getEvaluator().eval(value);
        } catch (Exception e) {
            // do nothing
        }
        return result;
    }

    private static void createUnitData( RuleContext context, RuleUnitDescr ruleUnitDescr, BlockStmt ruleVariablesBlock ) {
        if (ruleUnitDescr != null) {
            for (Map.Entry<String, Method> unitVar : ruleUnitDescr.getUnitVarAccessors().entrySet()) {
                addUnitData(context, unitVar.getKey(), unitVar.getValue().getGenericReturnType(), ruleVariablesBlock);
            }
        }
    }

    private static void addUnitData(RuleContext context, String unitVar, java.lang.reflect.Type type, BlockStmt ruleBlock) {
        Class<?> rawClass = toRawClass(type);
        Type declType = classToReferenceType( toRawClass(type) );

        context.addRuleUnitVar( unitVar, getClassForUnitData( type, rawClass ) );

        ClassOrInterfaceType varType = toClassOrInterfaceType(UnitData.class);
        varType.setTypeArguments(declType);
        VariableDeclarationExpr var_ = new VariableDeclarationExpr(varType, context.getVar(unitVar), Modifier.FINAL);

        MethodCallExpr unitDataCall = new MethodCallExpr(null, UNIT_DATA_CALL);

        unitDataCall.addArgument(new ClassExpr( declType ));
        unitDataCall.addArgument(new StringLiteralExpr(unitVar));

        AssignExpr var_assign = new AssignExpr(var_, unitDataCall, AssignExpr.Operator.ASSIGN);
        ruleBlock.addStatement(var_assign);
    }

    private static Class<?> getClassForUnitData( java.lang.reflect.Type type, Class<?> rawClass ) {
        if (Iterable.class.isAssignableFrom( rawClass ) && type instanceof ParameterizedType) {
            return toRawClass( (( ParameterizedType ) type).getActualTypeArguments()[0] );
        }
        if (rawClass.isArray()) {
            return rawClass.getComponentType();
        }
        return rawClass;
    }

    public static void createVariables(KnowledgeBuilderImpl kbuilder, BlockStmt block, PackageModel packageModel, RuleContext context) {
        for (DeclarationSpec decl : context.getAllDeclarations()) {
            if (!packageModel.getGlobals().containsKey(decl.getBindingId()) && !context.queryParameterWithName(p -> p.name.equals(decl.getBindingId())).isPresent()) {
                addVariable(kbuilder, block, decl, context);
            }
        }
    }

    private static void addVariable(KnowledgeBuilderImpl kbuilder, BlockStmt ruleBlock, DeclarationSpec decl, RuleContext context) {
        if (decl.getDeclarationClass() == null) {
            kbuilder.addBuilderResult( new UnknownDeclarationError( decl.getBindingId() ) );
            return;
        }
        Type declType = classToReferenceType( decl.getDeclarationClass() );

        ClassOrInterfaceType varType = toClassOrInterfaceType(Variable.class);
        varType.setTypeArguments(declType);
        VariableDeclarationExpr var_ = new VariableDeclarationExpr(varType, context.getVar(decl.getBindingId()), Modifier.FINAL);

        MethodCallExpr declarationOfCall = new MethodCallExpr(null, DECLARATION_OF_CALL);

        declarationOfCall.addArgument(new ClassExpr(decl.getType() ));
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
                windowCall.addArgument( "Window.Type.TIME" );
                windowCall.addArgument( "" + TimeUtils.parseTimeString(behaviorDescr.getParameters().get(0 ) ) );
            }
            if ( Behavior.BehaviorType.LENGTH_WINDOW.matches( behaviorDescr.getSubType() ) ) {
                windowCall.addArgument( "Window.Type.LENGTH" );
                windowCall.addArgument( "" + Integer.valueOf( behaviorDescr.getParameters().get( 0 ) ) );
            }
            declarationOfCall.addArgument( windowCall );
        }

        AssignExpr var_assign = new AssignExpr(var_, declarationOfCall, AssignExpr.Operator.ASSIGN);
        ruleBlock.addStatement(var_assign);
    }
}