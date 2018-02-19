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
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

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
import org.drools.core.util.ClassUtils;
import org.drools.core.util.MVELSafeHelper;
import org.drools.core.util.StringUtils;
import org.drools.core.util.index.IndexUtil.ConstraintType;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.body.MethodDeclaration;
import org.drools.javaparser.ast.body.Parameter;
import org.drools.javaparser.ast.expr.AssignExpr;
import org.drools.javaparser.ast.expr.ClassExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.FieldAccessExpr;
import org.drools.javaparser.ast.expr.LambdaExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.NullLiteralExpr;
import org.drools.javaparser.ast.expr.ObjectCreationExpr;
import org.drools.javaparser.ast.expr.SimpleName;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.javaparser.ast.expr.VariableDeclarationExpr;
import org.drools.javaparser.ast.stmt.BlockStmt;
import org.drools.javaparser.ast.stmt.EmptyStmt;
import org.drools.javaparser.ast.stmt.ExpressionStmt;
import org.drools.javaparser.ast.stmt.ReturnStmt;
import org.drools.javaparser.ast.stmt.Statement;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.javaparser.ast.type.Type;
import org.drools.javaparser.ast.type.UnknownType;
import org.drools.model.BitMask;
import org.drools.model.Rule;
import org.drools.model.UnitData;
import org.drools.model.Variable;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.UnknownDeclarationError;
import org.drools.modelcompiler.builder.generator.RuleContext.RuleDialect;
import org.drools.modelcompiler.builder.generator.drlxparse.DrlxParseSuccess;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;
import org.drools.modelcompiler.consequence.DroolsImpl;
import org.kie.soup.project.datamodel.commons.types.TypeResolver;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import static org.drools.javaparser.JavaParser.parseExpression;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.classToReferenceType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.parseBlock;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.visitor.NamedConsequenceVisitor.BREAKING_CALL;
import static org.drools.modelcompiler.util.StringUtil.toId;

public class ModelGenerator {

    private static final ClassOrInterfaceType RULE_TYPE = JavaParser.parseClassOrInterfaceType( Rule.class.getCanonicalName() );
    private static final ClassOrInterfaceType BITMASK_TYPE = JavaParser.parseClassOrInterfaceType( BitMask.class.getCanonicalName() );

    private static final Map<String, Expression> attributesMap = new HashMap<>();

    public static final Set<String> implicitDroolsMethods = new HashSet<>();
    public static final Set<String> knowledgeHelperMethods = new HashSet<>();

    public static final Set<String> temporalOperators = new HashSet<>();

    private static final IndexIdGenerator indexIdGenerator = new IndexIdGenerator();

    private static final Expression asKnoledgeHelperExpression = parseExpression("((" + DroolsImpl.class.getCanonicalName() + ") drools).asKnowledgeHelper()");

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

        implicitDroolsMethods.add("insert");
        implicitDroolsMethods.add("insertLogical");
        implicitDroolsMethods.add("delete");
        implicitDroolsMethods.add("retract");
        implicitDroolsMethods.add("update");

        knowledgeHelperMethods.add("getWorkingMemory");
        knowledgeHelperMethods.add("getRule");
        knowledgeHelperMethods.add("getTuple");
        knowledgeHelperMethods.add("getKnowledgeRuntime");
        knowledgeHelperMethods.add("getKieRuntime");
        knowledgeHelperMethods.add("insertLogical");
    }

    public static final boolean GENERATE_EXPR_ID = true;

    public static final String BUILD_CALL = "build";
    public static final String RULE_CALL = "rule";
    public static final String UNIT_CALL = "unit";
    public static final String EXECUTE_CALL = "execute";
    public static final String EXECUTESCRIPT_CALL = "executeScript";
    public static final String ON_CALL = "on";
    public static final String EXPR_CALL = "expr";
    public static final String INDEXED_BY_CALL = "indexedBy";
    public static final String BIND_CALL = "bind";
    public static final String BIND_AS_CALL = "as";
    public static final String ATTRIBUTE_CALL = "attribute";
    public static final String METADATA_CALL = "metadata";
    public static final String DECLARATION_OF_CALL = "declarationOf";
    public static final String QUERY_INVOCATION_CALL = "call";
    public static final String VALUE_OF_CALL = "valueOf";
    public static final String UNIT_DATA_CALL = "unitData";

    public static void generateModel( KnowledgeBuilderImpl kbuilder, InternalKnowledgePackage pkg, PackageDescr packageDescr, PackageModel packageModel ) {
        TypeResolver typeResolver = pkg.getTypeResolver();
        packageModel.addImports(pkg.getTypeResolver().getImports());
        packageModel.addGlobals(pkg.getGlobals());
        packageModel.addAccumulateFunctions(pkg.getAccumulateFunctions());
        new WindowReferenceGenerator(packageModel, typeResolver).addWindowReferences(kbuilder, packageDescr.getWindowDeclarations());
        packageModel.addAllFunctions(packageDescr.getFunctions().stream().map(FunctionGenerator::toFunction).collect(toList()));

        for(RuleDescr descr : packageDescr.getRules()) {
            if (descr instanceof QueryDescr) {
                QueryGenerator.processQueryDef( kbuilder, typeResolver, packageModel, (QueryDescr) descr);
            }
        }

        for (RuleDescr descr : packageDescr.getRules()) {
            if (descr instanceof QueryDescr) {
                QueryGenerator.processQuery(kbuilder, packageModel, (QueryDescr) descr);
            } else {
                processRule(kbuilder, typeResolver, packageModel, packageDescr, descr);
            }
        }
    }


    private static void processRule( KnowledgeBuilderImpl kbuilder, TypeResolver typeResolver, PackageModel packageModel, PackageDescr packageDescr, RuleDescr ruleDescr) {
        RuleContext context = new RuleContext(kbuilder,packageModel, ruleDescr,  typeResolver);

        for(Entry<String, Object> kv : ruleDescr.getNamedConsequences().entrySet()) {
            context.addNamedConsequence(kv.getKey(), kv.getValue().toString());
        }

        new ModelGeneratorVisitor(context, packageModel).visit(getExtendedLhs(packageDescr, ruleDescr));
        final String ruleMethodName = "rule_" + toId(ruleDescr.getName());
        MethodDeclaration ruleMethod = new MethodDeclaration(EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), RULE_TYPE, ruleMethodName);

        ruleMethod.setJavadocComment(" Rule name: " + ruleDescr.getName() + " ");

        VariableDeclarationExpr ruleVar = new VariableDeclarationExpr(RULE_TYPE, RULE_CALL);

        MethodCallExpr ruleCall = new MethodCallExpr(null, RULE_CALL);
        if (!ruleDescr.getNamespace().isEmpty()) {
            ruleCall.addArgument( new StringLiteralExpr( ruleDescr.getNamespace() ) );
        }
        ruleCall.addArgument( new StringLiteralExpr( ruleDescr.getName() ) );

        RuleUnitDescr ruleUnitDescr = context.getRuleUnitDescr();

        MethodCallExpr buildCallScope = ruleUnitDescr != null ?
                new MethodCallExpr(ruleCall, UNIT_CALL).addArgument( new ClassExpr( classToReferenceType(ruleUnitDescr.getRuleUnitClass()) ) ) :
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

        BlockStmt ruleVariablesBlock = new BlockStmt();
        createUnitData( ruleUnitDescr, ruleVariablesBlock );
        createVariables(kbuilder, ruleVariablesBlock, packageModel, context);
        ruleMethod.setBody(ruleVariablesBlock);

        MethodCallExpr executeCall = createConsequenceCall( packageModel, ruleDescr, context, ruleDescr.getConsequence().toString(), ruleVariablesBlock, false );
        buildCall.addArgument( executeCall );

        ruleVariablesBlock.addStatement(new AssignExpr(ruleVar, buildCall, AssignExpr.Operator.ASSIGN));

        ruleVariablesBlock.addStatement( new ReturnStmt(RULE_CALL) );
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
    private static List<MethodCallExpr> ruleAttributes(RuleContext context, RuleDescr ruleDescr) {
        List<MethodCallExpr> ruleAttributes = new ArrayList<>();
        for (Entry<String, AttributeDescr> as : ruleDescr.getAttributes().entrySet()) {
            // dialect=mvel is not an attribute of DSL expr(), so we check it before.
            if (as.getKey().equals( "dialect" )) {
                if (as.getValue().getValue().equals("mvel")) {
                    context.setRuleDialect(RuleDialect.MVEL);
                }
                continue;
            }
            MethodCallExpr attributeCall = new MethodCallExpr(null, ATTRIBUTE_CALL);
            attributeCall.addArgument(attributesMap.get(as.getKey()));
            switch (as.getKey()) {
                case "dialect":
                    throw new RuntimeException("should not have reached this part of the code");
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
                    attributeCall.addArgument( parseExpression(String.format("GregorianCalendar.from(LocalDate.parse(\"%s\", dateTimeFormatter).atStartOfDay(ZoneId.systemDefault()))", as.getValue().getValue())));
                    break;
                default:
                    throw new UnsupportedOperationException("Unhandled case for rule attribute: " + as.getKey());
            }
            ruleAttributes.add(attributeCall);
        }
        return ruleAttributes;
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
                    metaAttributeCall.addArgument(objectAsJPExpression(annValue));
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

    public static MethodCallExpr createConsequenceCall( PackageModel packageModel, RuleDescr ruleDescr, RuleContext context, String consequenceString, BlockStmt ruleVariablesBlock, boolean isBreaking ) {
        consequenceString = consequenceString.replaceAll( "kcontext", "drools" );
        BlockStmt ruleConsequence = rewriteConsequence(context, consequenceString);
        Collection<String> usedDeclarationInRHS = extractUsedDeclarations(packageModel, context, ruleConsequence, consequenceString);
        MethodCallExpr onCall = onCall(usedDeclarationInRHS);
        if (isBreaking) {
            onCall = new MethodCallExpr( onCall, BREAKING_CALL );
        }
        MethodCallExpr executeCall = null;
        if (context.getRuleDialect() == RuleDialect.JAVA) {
            executeCall = executeCall(context, ruleVariablesBlock, ruleConsequence, usedDeclarationInRHS, onCall);
        } else if (context.getRuleDialect() == RuleDialect.MVEL) {
            executeCall = executeScriptCall(packageModel, ruleDescr, onCall);
        }
        return executeCall;
    }

    private static BlockStmt rewriteConsequence(RuleContext context, String consequence ) {
        if (context.getRuleDialect() == RuleDialect.MVEL) {
            // anyhow the consequence will be used as a ScriptBlock.
            return null;
        }
        String ruleConsequenceAsBlock = rewriteConsequenceBlock(context, consequence.trim() );
        return parseBlock(ruleConsequenceAsBlock);
    }

    private static Collection<String> extractUsedDeclarations(PackageModel packageModel, RuleContext context, BlockStmt ruleConsequence, String consequenceString) {
        Set<String> existingDecls = new HashSet<>();
        existingDecls.addAll(context.getDeclarations().stream().map(DeclarationSpec::getBindingId).collect(toList()));
        existingDecls.addAll(packageModel.getGlobals().keySet());
        if (context.getRuleUnitDescr() != null) {
            existingDecls.addAll(context.getRuleUnitDescr().getUnitVars());
        }

        if (context.getRuleDialect() == RuleDialect.MVEL) {
            return existingDecls.stream().filter(consequenceString::contains).collect(toSet());
        }

        Set<String> declUsedInRHS = ruleConsequence.findAll(NameExpr.class).stream().map(NameExpr::getNameAsString).collect(toSet());
        return existingDecls.stream().filter(declUsedInRHS::contains).collect(toSet());
    }

    private static MethodCallExpr executeCall(RuleContext context, BlockStmt ruleVariablesBlock, BlockStmt ruleConsequence, Collection<String> verifiedDeclUsedInRHS, MethodCallExpr onCall) {
        boolean requireDrools = rewriteRHS(context, ruleVariablesBlock, ruleConsequence);
        MethodCallExpr executeCall = new MethodCallExpr(onCall, EXECUTE_CALL);
        LambdaExpr executeLambda = new LambdaExpr();
        executeCall.addArgument(executeLambda);
        executeLambda.setEnclosingParameters(true);
        if (requireDrools) {
            executeLambda.addParameter(new Parameter(new UnknownType(), "drools"));
        }
        verifiedDeclUsedInRHS.stream().map(x -> new Parameter(new UnknownType(), x)).forEach(executeLambda::addParameter);
        executeLambda.setBody( ruleConsequence );
        return executeCall;
    }

    private static MethodCallExpr executeScriptCall(PackageModel packageModel, RuleDescr ruleDescr, MethodCallExpr onCall) {
        MethodCallExpr executeCall = new MethodCallExpr(onCall, EXECUTESCRIPT_CALL);
        executeCall.addArgument(new StringLiteralExpr("mvel"));

        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(StringBuilder.class.getCanonicalName());
        Expression mvelSB = objectCreationExpr;

        for (String i : packageModel.getImports()) {
            if (i.equals(packageModel.getName() + ".*")) {
                continue; // skip same-package star import.
            }
            MethodCallExpr appendCall = new MethodCallExpr(mvelSB, "append");
            StringLiteralExpr importAsStringLiteral = new StringLiteralExpr();
            importAsStringLiteral.setString("import " + i + ";\n"); // use the setter method in order for the string literal be properly escaped.
            appendCall.addArgument(importAsStringLiteral);
            mvelSB = appendCall;
        }

        StringLiteralExpr mvelScriptBodyStringLiteral = new StringLiteralExpr();
        mvelScriptBodyStringLiteral.setString(ruleDescr.getConsequence().toString()); // use the setter method in order for the string literal be properly escaped.

        MethodCallExpr appendCall = new MethodCallExpr(mvelSB, "append");
        appendCall.addArgument(mvelScriptBodyStringLiteral);

        executeCall.addArgument(new MethodCallExpr(appendCall, "toString"));
        return executeCall;
    }

    private static MethodCallExpr onCall(Collection<String> usedArguments) {
        MethodCallExpr onCall = null;

        if (!usedArguments.isEmpty()) {
            onCall = new MethodCallExpr(null, ON_CALL);
            usedArguments.stream().map(DrlxParseUtil::toVar).forEach(onCall::addArgument );
        }
        return onCall;
    }

    private static void createUnitData( RuleUnitDescr ruleUnitDescr, BlockStmt ruleVariablesBlock ) {
        if (ruleUnitDescr != null) {
            for (String unitVar : ruleUnitDescr.getUnitVars()) {
                addUnitData(unitVar, ruleUnitDescr.getVarType( unitVar ).get(), ruleVariablesBlock);
            }
        }
    }

    private static void addUnitData(String unitVar, Class<?> type, BlockStmt ruleBlock) {
        Type declType = classToReferenceType( type );

        ClassOrInterfaceType varType = JavaParser.parseClassOrInterfaceType(UnitData.class.getCanonicalName());
        varType.setTypeArguments(declType);
        VariableDeclarationExpr var_ = new VariableDeclarationExpr(varType, toVar(unitVar), Modifier.FINAL);

        MethodCallExpr unitDataCall = new MethodCallExpr(null, UNIT_DATA_CALL);

        unitDataCall.addArgument(new ClassExpr( declType ));
        unitDataCall.addArgument(new StringLiteralExpr(unitVar));

        AssignExpr var_assign = new AssignExpr(var_, unitDataCall, AssignExpr.Operator.ASSIGN);
        ruleBlock.addStatement(var_assign);
    }

    public static void createVariables(KnowledgeBuilderImpl kbuilder, BlockStmt block, PackageModel packageModel, RuleContext context) {
        for (DeclarationSpec decl : context.getDeclarations()) {
            if (!packageModel.getGlobals().containsKey(decl.getBindingId()) && !context.queryParameterWithName(p -> p.name.equals(decl.getBindingId())).isPresent()) {
                addVariable(kbuilder, block, decl);
            }
        }
    }

    private static void addVariable(KnowledgeBuilderImpl kbuilder, BlockStmt ruleBlock, DeclarationSpec decl) {
        if (decl.getDeclarationClass() == null) {
            kbuilder.addBuilderResult( new UnknownDeclarationError( decl.getBindingId() ) );
            return;
        }
        Type declType = classToReferenceType( decl.getDeclarationClass() );

        ClassOrInterfaceType varType = JavaParser.parseClassOrInterfaceType(Variable.class.getCanonicalName());
        varType.setTypeArguments(declType);
        VariableDeclarationExpr var_ = new VariableDeclarationExpr(varType, toVar(decl.getBindingId()), Modifier.FINAL);

        MethodCallExpr declarationOfCall = new MethodCallExpr(null, DECLARATION_OF_CALL);

        declarationOfCall.addArgument(new ClassExpr(decl.getType() ));
        declarationOfCall.addArgument(new StringLiteralExpr(decl.getVariableName().orElse(decl.getBindingId())));

        decl.getDeclarationSource().ifPresent(declarationOfCall::addArgument);

        decl.getEntryPoint().ifPresent( ep -> {
            MethodCallExpr entryPointCall = new MethodCallExpr(null, "entryPoint");
            entryPointCall.addArgument( new StringLiteralExpr(ep ) );
            declarationOfCall.addArgument( entryPointCall );
        } );
        for ( BehaviorDescr behaviorDescr : decl.getBehaviors() ) {
            MethodCallExpr windowCall = new MethodCallExpr(null, "window");
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

    private static String rewriteConsequenceBlock( RuleContext context, String consequence ) {
        int modifyPos = StringUtils.indexOfOutOfQuotes(consequence, "modify");
        if (modifyPos < 0) {
            return consequence;
        }

        int lastCopiedEnd = 0;
        StringBuilder sb = new StringBuilder();
        sb.append( consequence.substring( lastCopiedEnd, modifyPos ) );
        lastCopiedEnd = modifyPos + 1;

        for (; modifyPos >= 0; modifyPos = StringUtils.indexOfOutOfQuotes(consequence, "modify", modifyPos + 6)) {
            int declStart = consequence.indexOf( '(', modifyPos+6 );
            int declEnd = consequence.indexOf( ')', declStart+1 );
            if (declEnd < 0) {
                continue;
            }
            String decl = consequence.substring( declStart+1, declEnd ).trim();
            if ( !context.getDeclarationById( decl ).isPresent()) {
                continue;
            }
            int blockStart = consequence.indexOf( '{', declEnd+1 );
            int blockEnd = consequence.indexOf( '}', blockStart+1 );
            if (blockEnd < 0) {
                continue;
            }

            if (lastCopiedEnd < modifyPos) {
                sb.append( consequence.substring( lastCopiedEnd, modifyPos ) );
            }

            NameExpr declAsNameExpr = new NameExpr(decl);
            String originalBlock = consequence.substring(blockStart + 1, blockEnd).trim();
            BlockStmt modifyBlock = JavaParser.parseBlock("{" + originalBlock + ";}");
            List<MethodCallExpr> originalMethodCalls = modifyBlock.findAll(MethodCallExpr.class);
            for (MethodCallExpr mc : originalMethodCalls) {
                Expression mcWithScope = org.drools.modelcompiler.builder.generator.DrlxParseUtil.prepend(declAsNameExpr, mc);
                modifyBlock.replace(mc, mcWithScope);
            }
            for (Statement n : modifyBlock.getStatements()) {
                if (!(n instanceof EmptyStmt)) {
                    sb.append(n);
                }
            }

            sb.append( "update(" ).append( decl ).append( ");\n" );
            lastCopiedEnd = blockEnd+1;
        }

        if (lastCopiedEnd < consequence.length()) {
            sb.append( consequence.substring( lastCopiedEnd ) );
        }

        return sb.toString();
    }

    private static boolean rewriteRHS(RuleContext context, BlockStmt ruleBlock, BlockStmt rhs) {
        AtomicBoolean requireDrools = new AtomicBoolean( false );
        List<MethodCallExpr> methodCallExprs = rhs.findAll(MethodCallExpr.class);
        List<MethodCallExpr> updateExprs = new ArrayList<>();

        for ( MethodCallExpr methodCallExpr : methodCallExprs ) {
            if ( isDroolsMethod( methodCallExpr ) ) {
                if ( !methodCallExpr.getScope().isPresent() ) {
                    methodCallExpr.setScope( new NameExpr( "drools" ) );
                }
                if ( knowledgeHelperMethods.contains( methodCallExpr.getNameAsString() ) ) {
                    methodCallExpr.setScope( asKnoledgeHelperExpression );
                } else if ( methodCallExpr.getNameAsString().equals( "update" ) ) {
                    updateExprs.add( methodCallExpr );
                } else if ( methodCallExpr.getNameAsString().equals( "retract" ) ) {
                    methodCallExpr.setName( new SimpleName( "delete" ) );
                }
                requireDrools.set( true );
            }
        }

        for (MethodCallExpr updateExpr : updateExprs) {
            Expression argExpr = updateExpr.getArgument( 0 );
            if (argExpr instanceof NameExpr) {
                String updatedVar = ( (NameExpr) argExpr ).getNameAsString();
                Class<?> updatedClass = context.getDeclarationById( updatedVar ).map(DeclarationSpec::getDeclarationClass).orElseThrow(RuntimeException::new);

                MethodCallExpr bitMaskCreation = new MethodCallExpr( new NameExpr( BitMask.class.getCanonicalName() ), "getPatternMask" );
                bitMaskCreation.addArgument( new ClassExpr( JavaParser.parseClassOrInterfaceType( updatedClass.getCanonicalName() ) ) );

                methodCallExprs.subList( 0, methodCallExprs.indexOf( updateExpr ) ).stream()
                               .filter( mce -> mce.getScope().isPresent() && hasScope( mce, updatedVar ) )
                               .map( mce -> ClassUtils.setter2property( mce.getNameAsString() ) )
                               .filter( Objects::nonNull )
                               .distinct()
                               .forEach( s -> bitMaskCreation.addArgument( new StringLiteralExpr( s ) ) );

                VariableDeclarationExpr bitMaskVar = new VariableDeclarationExpr(BITMASK_TYPE, "mask_" + updatedVar, Modifier.FINAL);
                AssignExpr bitMaskAssign = new AssignExpr(bitMaskVar, bitMaskCreation, AssignExpr.Operator.ASSIGN);
                ruleBlock.addStatement(bitMaskAssign);

                updateExpr.addArgument( "mask_" + updatedVar );
            }
        }

        return requireDrools.get();
    }

    private static boolean isDroolsMethod( MethodCallExpr mce ) {
        return hasScope( mce, "drools" ) ||
               ( !mce.getScope().isPresent() && implicitDroolsMethods.contains( mce.getNameAsString() ) );
    }

    private static boolean hasScope( MethodCallExpr mce, String scope ) {
        return mce.getScope().map( s -> s instanceof NameExpr && (( NameExpr ) s).getNameAsString().equals( scope ) ).orElse( false );
    }


    public static void processExpression(RuleContext context, DrlxParseSuccess drlxParseResult) {
        if (drlxParseResult.hasUnificationVariable()) {
            Expression dslExpr = buildUnificationExpression(context, drlxParseResult);
            context.addExpression(dslExpr);
        } else if ( drlxParseResult.isValidExpression() ) {
            Expression dslExpr = buildExpressionWithIndexing(context, drlxParseResult);
            context.addExpression(dslExpr);
        }
        if (drlxParseResult.getExprBinding() != null) {
            Expression dslExpr = buildBinding(drlxParseResult);
            context.addExpression(dslExpr);
        }
    }

    public static Expression buildExpressionWithIndexing(RuleContext context, DrlxParseSuccess drlxParseResult) {
        String exprId = drlxParseResult.getExprId();
        MethodCallExpr exprDSL = new MethodCallExpr(null, EXPR_CALL);
        if (exprId != null && !"".equals(exprId)) {
            exprDSL.addArgument( new StringLiteralExpr(exprId) );
        }

        exprDSL = buildExpression(context, drlxParseResult, exprDSL );
        exprDSL = buildIndexedBy(context, drlxParseResult, exprDSL);
        exprDSL = buildReactOn( drlxParseResult, exprDSL );
        return exprDSL;
    }

    private static Expression buildUnificationExpression(RuleContext context, DrlxParseSuccess drlxParseResult) {
        MethodCallExpr exprDSL = buildBinding(drlxParseResult);
        context.addDeclaration(new DeclarationSpec(drlxParseResult.getUnificationVariable(),
                                                   drlxParseResult.getUnificationVariableType(),
                                                    drlxParseResult.getUnificationName()
                                                    ));
        return exprDSL;
    }

    private static MethodCallExpr buildExpression(RuleContext context, DrlxParseSuccess drlxParseResult, MethodCallExpr exprDSL ) {
        final List<String> usedDeclarationsWithUnification = new ArrayList<>();
        if(!drlxParseResult.isPatternBindingUnification()) {
            if (drlxParseResult.getPatternBinding() != null && !drlxParseResult.getUsedDeclarations().contains( drlxParseResult.getPatternBinding() )) {
                exprDSL.addArgument(new NameExpr(toVar(drlxParseResult.getPatternBinding())));
            }
        } else {
            usedDeclarationsWithUnification.add(drlxParseResult.getPatternBinding());
        }
        usedDeclarationsWithUnification.addAll(drlxParseResult.getUsedDeclarations());
        usedDeclarationsWithUnification.stream()
                .map(x -> QueryGenerator.substituteBindingWithQueryParameter(context, x))
                .forEach(exprDSL::addArgument);
        exprDSL.addArgument(buildConstraintExpression( drlxParseResult, drlxParseResult.getExpr() ));
        return exprDSL;
    }

    private static MethodCallExpr buildIndexedBy(RuleContext context, DrlxParseSuccess drlxParseResult, MethodCallExpr exprDSL) {
        ConstraintType decodeConstraintType = drlxParseResult.getDecodeConstraintType();
        TypedExpression left = drlxParseResult.getLeft();
        TypedExpression right = drlxParseResult.getRight();

        // .indexBy(..) is only added if left is not an identity expression:
        if ( decodeConstraintType != null &&
             !(left.getExpression() instanceof NameExpr &&
             ((NameExpr)left.getExpression()).getName().getIdentifier().equals("_this")) &&
             left.getFieldName() != null ) {
            Class<?> indexType = Stream.of( left, right ).map( TypedExpression::getType )
                                       .filter( Objects::nonNull )
                                       .findFirst().get();

            ClassExpr indexedBy_indexedClass = new ClassExpr( JavaParser.parseType( indexType.getCanonicalName() ) );
            FieldAccessExpr indexedBy_constraintType = new FieldAccessExpr( new NameExpr( "org.drools.model.Index.ConstraintType" ), decodeConstraintType.toString()); // not 100% accurate as the type in "nameExpr" is actually parsed if it was JavaParsers as a big chain of FieldAccessExpr
            LambdaExpr indexedBy_leftOperandExtractor = new LambdaExpr();
            indexedBy_leftOperandExtractor.addParameter(new Parameter(new UnknownType(), "_this"));
            boolean leftContainsThis = left.getExpression().toString().contains("_this");
            indexedBy_leftOperandExtractor.setBody(new ExpressionStmt(leftContainsThis ? left.getExpression() : right.getExpression()) );

            MethodCallExpr indexedByDSL = new MethodCallExpr(exprDSL, INDEXED_BY_CALL);
            indexedByDSL.addArgument( indexedBy_indexedClass );
            indexedByDSL.addArgument( indexedBy_constraintType );
            indexedByDSL.addArgument( "" + indexIdGenerator.getFieldId(drlxParseResult.getPatternType(), left.getFieldName() ) );
            indexedByDSL.addArgument( indexedBy_leftOperandExtractor );

            Collection<String> usedDeclarations = drlxParseResult.getUsedDeclarations();
            if ( usedDeclarations.isEmpty() ) {
                indexedByDSL.addArgument( right.getExpression() );
            } else if (usedDeclarations.size() == 1) {
                // we ask if "right" expression is simply a symbol, hence just purely a declaration referenced by name
                if (context.getDeclarationById(right.getExpressionAsString()).isPresent()) {
                    LambdaExpr indexedBy_rightOperandExtractor = new LambdaExpr();
                    indexedBy_rightOperandExtractor.addParameter(new Parameter(new UnknownType(), usedDeclarations.iterator().next()));
                    indexedBy_rightOperandExtractor.setBody(new ExpressionStmt(!leftContainsThis ? left.getExpression() : right.getExpression()));
                    indexedByDSL.addArgument(indexedBy_rightOperandExtractor);
                } else {
                    // this is a case where a Beta node should NOT create the index because the "right" is not just-a-symbol, the "right" is not a declaration referenced by name
                    return exprDSL;
                }
            } else {
                // this is a case where a Beta node should NOT create the index because the "right" is not just-a-symbol, the "right" is not a declaration referenced by name
                return exprDSL;
            }
            return indexedByDSL;
        }
        return exprDSL;
    }

    private static MethodCallExpr buildReactOn(DrlxParseSuccess drlxParseResult, MethodCallExpr exprDSL ) {
        if ( !drlxParseResult.getReactOnProperties().isEmpty() ) {
            exprDSL = new MethodCallExpr(exprDSL, "reactOn");
            drlxParseResult.getReactOnProperties().stream()
                             .map( StringLiteralExpr::new )
                             .forEach( exprDSL::addArgument );

        }

        if ( !drlxParseResult.getWatchedProperties().isEmpty() ) {
            exprDSL = new MethodCallExpr(exprDSL, "watch");
            drlxParseResult.getWatchedProperties().stream()
                    .map( StringLiteralExpr::new )
                    .forEach( exprDSL::addArgument );
        }

        return exprDSL;
    }

    private static Expression buildConstraintExpression(DrlxParseSuccess drlxParseResult, Expression expr ) {
        return drlxParseResult.isStatic() ? expr : generateLambdaWithoutParameters(drlxParseResult.getUsedDeclarations(), expr, drlxParseResult.isSkipThisAsParam());
    }

    public static MethodCallExpr buildBinding(DrlxParseSuccess drlxParseResult ) {
        MethodCallExpr bindDSL = new MethodCallExpr(null, BIND_CALL);
        if(drlxParseResult.hasUnificationVariable()) {
            bindDSL.addArgument(new NameExpr(toVar(drlxParseResult.getUnificationVariable())));
        } else {
            bindDSL.addArgument( new NameExpr(toVar(drlxParseResult.getExprBinding())) );
        }
        MethodCallExpr bindAsDSL = new MethodCallExpr(bindDSL, BIND_AS_CALL);
        bindAsDSL.addArgument( new NameExpr(toVar(drlxParseResult.getPatternBinding())) );
        final Expression constraintExpression = buildConstraintExpression(drlxParseResult, org.drools.modelcompiler.builder.generator.DrlxParseUtil.findLeftLeafOfMethodCall(drlxParseResult.getLeft().getExpression())  );
        bindAsDSL.addArgument(constraintExpression);
        return buildReactOn( drlxParseResult, bindAsDSL );
    }
}