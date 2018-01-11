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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.QueryDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.Behavior;
import org.drools.core.ruleunit.RuleUnitDescr;
import org.drools.core.time.TimeUtils;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.StringUtils;
import org.drools.core.util.index.IndexUtil.ConstraintType;
import org.drools.drlx.DrlxParser;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.body.MethodDeclaration;
import org.drools.javaparser.ast.body.Parameter;
import org.drools.javaparser.ast.drlx.OOPathExpr;
import org.drools.javaparser.ast.drlx.expr.DrlxExpression;
import org.drools.javaparser.ast.drlx.expr.PointFreeExpr;
import org.drools.javaparser.ast.expr.AssignExpr;
import org.drools.javaparser.ast.expr.BinaryExpr;
import org.drools.javaparser.ast.expr.BinaryExpr.Operator;
import org.drools.javaparser.ast.expr.ClassExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.FieldAccessExpr;
import org.drools.javaparser.ast.expr.LambdaExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.ObjectCreationExpr;
import org.drools.javaparser.ast.expr.SimpleName;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.javaparser.ast.expr.ThisExpr;
import org.drools.javaparser.ast.expr.UnaryExpr;
import org.drools.javaparser.ast.expr.VariableDeclarationExpr;
import org.drools.javaparser.ast.nodeTypes.NodeWithArguments;
import org.drools.javaparser.ast.nodeTypes.NodeWithOptionalScope;
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
import org.drools.modelcompiler.builder.errors.ParseExpressionErrorResult;
import org.drools.modelcompiler.builder.errors.UnknownDeclarationError;
import org.drools.modelcompiler.builder.generator.RuleContext.RuleDialect;
import org.drools.modelcompiler.builder.generator.operatorspec.CustomOperatorSpec;
import org.drools.modelcompiler.builder.generator.operatorspec.InOperatorSpec;
import org.drools.modelcompiler.builder.generator.operatorspec.TemporalOperatorSpec;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.drools.javaparser.printer.PrintUtil.toDrlx;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.classToReferenceType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.isPrimitiveExpression;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.parseBlock;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.visitor.NamedConsequenceVisitor.BREAKING_CALL;
import static org.drools.modelcompiler.util.StringUtil.toId;

public class ModelGenerator {

    private static final ClassOrInterfaceType RULE_TYPE = JavaParser.parseClassOrInterfaceType( Rule.class.getCanonicalName() );
    private static final ClassOrInterfaceType BITMASK_TYPE = JavaParser.parseClassOrInterfaceType( BitMask.class.getCanonicalName() );

    private static final Map<String, Expression> attributesMap = new HashMap<>();
    private static final Map<String, Expression> consequenceMethods = new HashMap<>();
    private static final Map<String, CustomOperatorSpec> customOperators = new HashMap<>();

    private static final IndexIdGenerator indexIdGenerator = new IndexIdGenerator();

    static {
        attributesMap.put("no-loop", JavaParser.parseExpression("Rule.Attribute.NO_LOOP"));
        attributesMap.put("salience", JavaParser.parseExpression("Rule.Attribute.SALIENCE"));
        attributesMap.put("enabled", JavaParser.parseExpression("Rule.Attribute.ENABLED"));
        attributesMap.put("auto-focus", JavaParser.parseExpression("Rule.Attribute.AUTO_FOCUS"));
        attributesMap.put("lock-on-active", JavaParser.parseExpression("Rule.Attribute.LOCK_ON_ACTIVE"));
        attributesMap.put("agenda-group", JavaParser.parseExpression("Rule.Attribute.AGENDA_GROUP"));
        attributesMap.put("activation-group", JavaParser.parseExpression("Rule.Attribute.ACTIVATION_GROUP"));
        attributesMap.put("ruleflow-group", JavaParser.parseExpression("Rule.Attribute.RULEFLOW_GROUP"));
        attributesMap.put("duration", JavaParser.parseExpression("Rule.Attribute.DURATION"));
        attributesMap.put("timer", JavaParser.parseExpression("Rule.Attribute.TIMER"));
        attributesMap.put("calendars", JavaParser.parseExpression("Rule.Attribute.CALENDARS"));
        attributesMap.put("date-effective", JavaParser.parseExpression("Rule.Attribute.DATE_EFFECTIVE"));
        attributesMap.put("date-expires", JavaParser.parseExpression("Rule.Attribute.DATE_EXPIRES"));

        consequenceMethods.put("getKnowledgeRuntime", JavaParser.parseExpression("drools.getRuntime(org.kie.api.runtime.KieRuntime.class)"));
        consequenceMethods.put("getKieRuntime", JavaParser.parseExpression("drools.getRuntime(org.kie.api.runtime.KieRuntime.class)"));

        customOperators.put("in", InOperatorSpec.INSTANCE);

        customOperators.put("before", TemporalOperatorSpec.INSTANCE);
        customOperators.put("after", TemporalOperatorSpec.INSTANCE);
        customOperators.put("coincides", TemporalOperatorSpec.INSTANCE);
        customOperators.put("metby", TemporalOperatorSpec.INSTANCE);
        customOperators.put("finishedby", TemporalOperatorSpec.INSTANCE);
        customOperators.put("overlaps", TemporalOperatorSpec.INSTANCE);
        customOperators.put("meets", TemporalOperatorSpec.INSTANCE);
        customOperators.put("during", TemporalOperatorSpec.INSTANCE);
        customOperators.put("finishes", TemporalOperatorSpec.INSTANCE);
        customOperators.put("startedby", TemporalOperatorSpec.INSTANCE);
        customOperators.put("overlappedby", TemporalOperatorSpec.INSTANCE);
        customOperators.put("includes", TemporalOperatorSpec.INSTANCE);
        customOperators.put("starts", TemporalOperatorSpec.INSTANCE);
    }

    public static final boolean GENERATE_EXPR_ID = true;

    public static final String BUILD_CALL = "build";
    public static final String RULE_CALL = "rule";
    public static final String UNIT_CALL = "unit";
    public static final String EXECUTE_CALL = "execute";
    public static final String EXECUTESCRIPT_CALL = "executeScript";
    public static final String ON_CALL = "on";
    public static final String EXPR_CALL = "expr";
    public static final String BIND_CALL = "bind";
    public static final String BIND_AS_CALL = "as";
    public static final String ATTRIBUTE_CALL = "attribute";
    public static final String DECLARATION_OF_CALL = "declarationOf";
    public static final String TYPE_CALL = "type";
    public static final String QUERY_INVOCATION_CALL = "call";
    public static final String VALUE_OF_CALL = "valueOf";
    public static final String UNIT_DATA_CALL = "unitData";

    public static void generateModel( KnowledgeBuilderImpl kbuilder, InternalKnowledgePackage pkg, PackageDescr packageDescr, PackageModel packageModel ) {
        packageModel.addImports(pkg.getTypeResolver().getImports());
        packageModel.addGlobals(pkg.getGlobals());
        packageModel.addAccumulateFunctions(pkg.getAccumulateFunctions());
        new WindowReferenceGenerator(packageModel, pkg).addWindowReferences(kbuilder, packageDescr.getWindowDeclarations());
        packageModel.addAllFunctions(packageDescr.getFunctions().stream().map(FunctionGenerator::toFunction).collect(toList()));

        for(RuleDescr descr : packageDescr.getRules()) {
            if (descr instanceof QueryDescr) {
                QueryGenerator.processQueryDef( kbuilder, pkg, packageModel, (QueryDescr) descr);
            }
        }

        for (RuleDescr descr : packageDescr.getRules()) {
            if (descr instanceof QueryDescr) {
                QueryGenerator.processQuery(kbuilder, packageModel, (QueryDescr) descr);
            } else {
                processRule(kbuilder, pkg, packageModel, descr);
            }
        }
    }


    private static void processRule(KnowledgeBuilderImpl kbuilder, InternalKnowledgePackage pkg, PackageModel packageModel, RuleDescr ruleDescr) {
        RuleContext context = new RuleContext(kbuilder, pkg, packageModel.getExprIdGenerator(), ruleDescr);

        for(Entry<String, Object> kv : ruleDescr.getNamedConsequences().entrySet()) {
            context.addNamedConsequence(kv.getKey(), kv.getValue().toString());
        }

        new ModelGeneratorVisitor(context, packageModel).visit(ruleDescr.getLhs());
        MethodDeclaration ruleMethod = new MethodDeclaration(EnumSet.of(Modifier.PRIVATE), RULE_TYPE, "rule_" + toId( ruleDescr.getName() ) );

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

        MethodCallExpr buildCall = new MethodCallExpr(buildCallScope, BUILD_CALL, NodeList.nodeList(context.getExpressions()));

        BlockStmt ruleVariablesBlock = new BlockStmt();
        createUnitData( ruleUnitDescr, ruleVariablesBlock );
        createVariables(kbuilder, ruleVariablesBlock, packageModel, context);
        ruleMethod.setBody(ruleVariablesBlock);

        MethodCallExpr executeCall = createConsequenceCall( packageModel, ruleDescr, context, ruleDescr.getConsequence().toString(), ruleVariablesBlock, false );
        buildCall.addArgument( executeCall );

        ruleVariablesBlock.addStatement(new AssignExpr(ruleVar, buildCall, AssignExpr.Operator.ASSIGN));

        ruleVariablesBlock.addStatement( new ReturnStmt(RULE_CALL) );
        packageModel.putRuleMethod("rule_" + toId( ruleDescr.getName() ), ruleMethod);
    }

    /**
     * Build a list of method calls, representing each needed {@link org.drools.model.impl.RuleBuilder#attribute(org.drools.model.Rule.Attribute, Object)}
     * starting from a drools-compiler {@link RuleDescr}.
     * The tuple represent the Rule Attribute expressed in JavParser form, and the attribute value expressed in JavaParser form.
     * @param context 
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
                    attributeCall.addArgument(JavaParser.parseExpression(as.getValue().getValue()));
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
                    Expression arrayExpr = JavaParser.parseExpression("new String[] { " + value + " }");
                    attributeCall.addArgument( arrayExpr );
                    break;
                case "date-effective":
                case "date-expires":
                    attributeCall.addArgument(JavaParser.parseExpression(String.format("GregorianCalendar.from(LocalDate.parse(\"%s\", dateTimeFormatter).atStartOfDay(ZoneId.systemDefault()))", as.getValue().getValue())));
                    break;
                default:
                    throw new UnsupportedOperationException("Unhandled case for rule attribute: " + as.getKey());
            }
            ruleAttributes.add(attributeCall);
        }
        return ruleAttributes;
    }

    public static MethodCallExpr createConsequenceCall( PackageModel packageModel, RuleDescr ruleDescr, RuleContext context, String consequenceString, BlockStmt ruleVariablesBlock, boolean isBreaking ) {
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

        Set<String> declUsedInRHS = ruleConsequence.getChildNodesByType(NameExpr.class).stream().map(NameExpr::getNameAsString).collect(toSet());
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
        mvelSB = appendCall;

        MethodCallExpr toStringCall = new MethodCallExpr(mvelSB, "toString");
        mvelSB = toStringCall;

        executeCall.addArgument(mvelSB);
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

        MethodCallExpr typeCall = new MethodCallExpr(null, ModelGenerator.TYPE_CALL);
        typeCall.addArgument( new ClassExpr( declType ));
        unitDataCall.addArgument(typeCall);

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
        MethodCallExpr typeCall = new MethodCallExpr(null, TYPE_CALL);
        typeCall.addArgument( new ClassExpr(decl.getType() ));

        declarationOfCall.addArgument(typeCall);
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
            List<MethodCallExpr> originalMethodCalls = modifyBlock.getChildNodesByType(MethodCallExpr.class);
            for (MethodCallExpr mc : originalMethodCalls) {
                Expression mcWithScope = DrlxParseUtil.prepend(declAsNameExpr, mc);
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
        List<MethodCallExpr> methodCallExprs = rhs.getChildNodesByType(MethodCallExpr.class);
        List<MethodCallExpr> updateExprs = new ArrayList<>();

        for (MethodCallExpr methodCallExpr : methodCallExprs) {
            if ( isDroolsMethod( methodCallExpr ) ) {
                if (!methodCallExpr.getScope().isPresent()) {
                    methodCallExpr.setScope(new NameExpr("drools"));
                }
                if (methodCallExpr.getNameAsString().equals("update")) {
                    updateExprs.add(methodCallExpr);
                } else if (methodCallExpr.getNameAsString().equals("retract")) {
                    methodCallExpr.setName( new SimpleName( "delete" ) );
                }
                requireDrools.set( true );
            } else {
                methodCallExpr.getScope().ifPresent( scope -> {
                    if (scope instanceof MethodCallExpr && hasScope( (( MethodCallExpr ) scope), "drools" )) {
                        Expression newScope = consequenceMethods.get( (( MethodCallExpr ) scope).getNameAsString() );
                        if (newScope != null) {
                            methodCallExpr.setScope( newScope );
                        }
                        requireDrools.set( true );
                    }
                } );
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
        return hasScope( mce, "drools" ) || (
                !mce.getScope().isPresent() && (
                mce.getNameAsString().equals("insert") ||
                mce.getNameAsString().equals("insertLogical") ||
                mce.getNameAsString().equals("delete") ||
                mce.getNameAsString().equals("retract") ||
                mce.getNameAsString().equals("update") ) );
    }

    private static boolean hasScope( MethodCallExpr mce, String scope ) {
        return mce.getScope().map( s -> s instanceof NameExpr && (( NameExpr ) s).getNameAsString().equals( scope ) ).orElse( false );
    }


    public static void processExpression(RuleContext context, DrlxParseResult drlxParseResult) {
        if (drlxParseResult.hasUnificationVariable()) {
            Expression dslExpr = buildUnificationExpression(context, drlxParseResult);
            context.addExpression(dslExpr);
        } else if ( drlxParseResult.getExpr() != null && (
                drlxParseResult.getRight() != null || drlxParseResult.getExprType() == Boolean.class || drlxParseResult.getExprType() == boolean.class ) ) {
            Expression dslExpr = buildExpressionWithIndexing(context, drlxParseResult);
            context.addExpression(dslExpr);
        }
        if (drlxParseResult.getExprBinding() != null) {
            Expression dslExpr = buildBinding(drlxParseResult);
            context.addExpression(dslExpr);
        }
    }

    public static DrlxParseResult drlxParse(RuleContext context, PackageModel packageModel, Class<?> patternType, String bindingId, String expression) {
        if ( expression.startsWith( bindingId + "." ) ) {
            expression = expression.substring( bindingId.length()+1 );
        }

        DrlxExpression drlx = DrlxParser.parseExpression( expression );
        DrlxParseResult result = getDrlxParseResult( context, packageModel, patternType, bindingId, expression, drlx );
        if (drlx.getBind() != null) {
            String bindId = drlx.getBind().asString();
            context.addDeclaration( new DeclarationSpec( bindId, result.getExprType() ) );
            result.setExprBinding( bindId );
        }

        return result;
    }

    private static DrlxParseResult getDrlxParseResult( RuleContext context, PackageModel packageModel, Class<?> patternType, String bindingId, String expression, DrlxExpression drlx ) {
        Expression drlxExpr = drlx.getExpr();

        String exprId;
        if ( GENERATE_EXPR_ID ) {
            exprId = context.getExprId( patternType, expression );
        }

        if ( drlxExpr instanceof BinaryExpr ) {
            BinaryExpr binaryExpr = (BinaryExpr) drlxExpr;
            Operator operator = binaryExpr.getOperator();

            ConstraintType decodeConstraintType = DrlxParseUtil.toConstraintType( operator );
            List<String> usedDeclarations = new ArrayList<>();
            Set<String> reactOnProperties = new HashSet<>();
            TypedExpression left = DrlxParseUtil.toTypedExpression( context, packageModel, patternType, binaryExpr.getLeft(), usedDeclarations, reactOnProperties, binaryExpr);
            TypedExpression right = DrlxParseUtil.toTypedExpression( context, packageModel, patternType, binaryExpr.getRight(), usedDeclarations, reactOnProperties, binaryExpr);

            Expression combo;
            if ( left.isPrimitive() ) {
                combo = new BinaryExpr( left.getExpression(), right.getExpression(), operator );
            } else {
                switch ( operator ) {
                    case EQUALS:
                    case NOT_EQUALS:
                        combo = getEqualityExpression( left, right, operator );
                        break;
                    default:
                        if ( left.getExpression() == null || right.getExpression() == null ) {
                            context.addCompilationError( new ParseExpressionErrorResult(drlxExpr) );
                            return null;
                        }
                        combo = new BinaryExpr( left.getExpression(), right.getExpression(), operator );
                }
            }

            if ( left.getPrefixExpression() != null ) {
                combo = new BinaryExpr( left.getPrefixExpression(), combo, Operator.AND );
            }

            return new DrlxParseResult(patternType, exprId, bindingId, combo, left.getType())
                    .setDecodeConstraintType( decodeConstraintType ).setUsedDeclarations( usedDeclarations )
                    .setReactOnProperties( reactOnProperties ).setLeft( left ).setRight( right );
        }

        if ( drlxExpr instanceof UnaryExpr ) {
            UnaryExpr unaryExpr = (UnaryExpr) drlxExpr;

            List<String> usedDeclarations = new ArrayList<>();
            Set<String> reactOnProperties = new HashSet<>();
            TypedExpression left = DrlxParseUtil.toTypedExpression( context, packageModel, patternType, unaryExpr, usedDeclarations, reactOnProperties, unaryExpr);

            return new DrlxParseResult(patternType, exprId, bindingId, left.getExpression(), left.getType())
                    .setUsedDeclarations( usedDeclarations ).setReactOnProperties( reactOnProperties ).setLeft( left );
        }

        if ( drlxExpr instanceof PointFreeExpr ) {
            PointFreeExpr pointFreeExpr = (PointFreeExpr) drlxExpr;

            List<String> usedDeclarations = new ArrayList<>();
            Set<String> reactOnProperties = new HashSet<>();
            TypedExpression left = DrlxParseUtil.toTypedExpression( context, packageModel, patternType, pointFreeExpr.getLeft(), usedDeclarations, reactOnProperties, pointFreeExpr);
            for (Expression rightExpr : pointFreeExpr.getRight()) {
                DrlxParseUtil.toTypedExpression( context, packageModel, patternType, rightExpr, usedDeclarations, reactOnProperties, pointFreeExpr);
            }

            String operator = pointFreeExpr.getOperator().asString();
            CustomOperatorSpec opSpec = customOperators.get( operator );
            if (opSpec == null) {
                throw new UnsupportedOperationException("Unknown operator '" + operator + "' in expression: " + toDrlx(drlxExpr));
            }
            MethodCallExpr methodCallExpr = opSpec.getMethodCallExpr( pointFreeExpr, left );

            return new DrlxParseResult(patternType, exprId, bindingId, methodCallExpr, boolean.class )
                    .setUsedDeclarations( usedDeclarations ).setReactOnProperties( reactOnProperties ).setLeft( left ).setStatic( opSpec.isStatic() );
        }

        if (drlxExpr instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = (MethodCallExpr) drlxExpr;
            
            // when the methodCallExpr will be placed in the model/DSL, any parameter being a "this" need to be implemented as _this by convention.
            List<ThisExpr> rewriteThisExprs = recurseCollectArguments(methodCallExpr).stream()
                                                                                     .filter(ThisExpr.class::isInstance)
                                                                                     .map(ThisExpr.class::cast)
                                                                                     .collect(Collectors.toList());
            for (ThisExpr t : rewriteThisExprs) {
                methodCallExpr.replace(t, new NameExpr("_this"));
            }
            
            Optional<MethodDeclaration> functionCall = packageModel.getFunctions().stream().filter(m -> m.getName().equals(methodCallExpr.getName())).findFirst();
            if(functionCall.isPresent()) {
                Class<?> returnType = DrlxParseUtil.getClassFromContext(context.getPkg().getTypeResolver(), functionCall.get().getType().asString());
                NodeList<Expression> arguments = methodCallExpr.getArguments();
                List<String> usedDeclarations = new ArrayList<>();
                for(Expression arg : arguments) {
                    if (arg instanceof NameExpr && !arg.toString().equals("_this")) {
                        usedDeclarations.add(arg.toString());
                    }
                }
                return new DrlxParseResult(patternType, exprId, bindingId, methodCallExpr, returnType).setUsedDeclarations(usedDeclarations);
            } else {
                NameExpr _this = new NameExpr("_this");
                TypedExpression converted = DrlxParseUtil.toMethodCallWithClassCheck(context, methodCallExpr, patternType, context.getPkg().getTypeResolver());
                Expression withThis = DrlxParseUtil.prepend(_this, converted.getExpression());
                return new DrlxParseResult(patternType, exprId, bindingId, withThis, converted.getType()).setLeft( converted );
            }
        }

        if (drlxExpr instanceof FieldAccessExpr) {
            FieldAccessExpr fieldCallExpr = (FieldAccessExpr) drlxExpr;

            NameExpr _this = new NameExpr("_this");
            TypedExpression converted = DrlxParseUtil.toMethodCallWithClassCheck(context, fieldCallExpr, patternType, context.getPkg().getTypeResolver());
            Expression withThis = DrlxParseUtil.prepend(_this, converted.getExpression());
            return new DrlxParseResult(patternType, exprId, bindingId, withThis, converted.getType()).setLeft( converted );
        }

        if (drlxExpr instanceof NameExpr) {
            NameExpr methodCallExpr = (NameExpr) drlxExpr;

            NameExpr _this = new NameExpr("_this");
            TypedExpression converted = DrlxParseUtil.toMethodCallWithClassCheck(context, methodCallExpr, patternType, context.getPkg().getTypeResolver());
            Expression withThis = DrlxParseUtil.prepend(_this, converted.getExpression());

            if (drlx.getBind() != null) {
                return new DrlxParseResult( patternType, exprId, bindingId, null, converted.getType() )
                        .setLeft( new TypedExpression( withThis, converted.getType() ) )
                        .addReactOnProperty( methodCallExpr.getNameAsString() );
            } else {
                return new DrlxParseResult( patternType, exprId, bindingId, withThis, converted.getType() )
                        .addReactOnProperty( methodCallExpr.getNameAsString() );
            }
        }

        if (drlxExpr instanceof OOPathExpr ) {
            return new DrlxParseResult(patternType, exprId, bindingId, drlxExpr, null);
        }

        throw new UnsupportedOperationException("Unknown expression: " + toDrlx(drlxExpr)); // TODO
    }

    private static Expression getEqualityExpression( TypedExpression left, TypedExpression right, Operator operator ) {
        if (isPrimitiveExpression(right.getExpression())) {
            return new BinaryExpr( left.getExpression(), right.getExpression(), operator == Operator.EQUALS ? BinaryExpr.Operator.EQUALS : Operator.NOT_EQUALS );
        }
        MethodCallExpr methodCallExpr = new MethodCallExpr( left.getExpression(), "equals" );
        methodCallExpr.addArgument( right.getExpression() ); // don't create NodeList with static method because missing "parent for child" would null and NPE
        return operator == Operator.EQUALS ? methodCallExpr : new UnaryExpr( methodCallExpr, UnaryExpr.Operator.LOGICAL_COMPLEMENT );
    }

    private static List<Expression> recurseCollectArguments(NodeWithArguments<?> methodCallExpr) {
        List<Expression> res = new ArrayList<>();
        res.addAll(methodCallExpr.getArguments());
        if ( methodCallExpr instanceof NodeWithOptionalScope ) {
            NodeWithOptionalScope<?> nodeWithOptionalScope = (NodeWithOptionalScope) methodCallExpr;
            if ( nodeWithOptionalScope.getScope().isPresent() ) {
                Object scope = nodeWithOptionalScope.getScope().get();
                if (scope instanceof NodeWithArguments) {
                    res.addAll(recurseCollectArguments((NodeWithArguments<?>) scope));
                }
            }
        }
        return res;
    }

    public static Expression buildExpressionWithIndexing(RuleContext context, DrlxParseResult drlxParseResult) {
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

    public static Expression buildUnificationExpression(RuleContext context, DrlxParseResult drlxParseResult) {
        MethodCallExpr exprDSL = buildBinding(drlxParseResult);
        context.addDeclaration(new DeclarationSpec(drlxParseResult.getUnificationVariable(),
                                                   drlxParseResult.getUnificationVariableType(),
                                                    drlxParseResult.getUnificationName()
                                                    ));
        return exprDSL;
    }

    private static MethodCallExpr buildExpression(RuleContext context, DrlxParseResult drlxParseResult, MethodCallExpr exprDSL ) {
        final List<String> usedDeclarationsWithUnification = new ArrayList<>();
        if(!drlxParseResult.isPatternBindingUnification()) {
            if (drlxParseResult.getPatternBinding() != null) {
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

    private static MethodCallExpr buildIndexedBy(RuleContext context, DrlxParseResult drlxParseResult, MethodCallExpr exprDSL) {
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

            MethodCallExpr indexedByDSL = new MethodCallExpr(exprDSL, "indexedBy");
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

    private static MethodCallExpr buildReactOn( DrlxParseResult drlxParseResult, MethodCallExpr exprDSL ) {
        if ( !drlxParseResult.getReactOnProperties().isEmpty() ) {
            exprDSL = new MethodCallExpr(exprDSL, "reactOn");
            drlxParseResult.getReactOnProperties().stream()
                             .map( StringLiteralExpr::new )
                             .forEach( exprDSL::addArgument );

        }

        if ( drlxParseResult.getWatchedProperties() != null && drlxParseResult.getWatchedProperties().length > 0 ) {
            exprDSL = new MethodCallExpr(exprDSL, "watch");
            Stream.of(drlxParseResult.getWatchedProperties())
                    .map( StringLiteralExpr::new )
                    .forEach( exprDSL::addArgument );
        }

        return exprDSL;
    }

    private static Expression buildConstraintExpression( DrlxParseResult drlxParseResult, Expression expr ) {
        return drlxParseResult.isStatic() ? expr : generateLambdaWithoutParameters(drlxParseResult.getUsedDeclarations(), expr);
    }

    public static MethodCallExpr buildBinding(DrlxParseResult drlxParseResult ) {
        MethodCallExpr bindDSL = new MethodCallExpr(null, BIND_CALL);
        if(drlxParseResult.hasUnificationVariable()) {
            bindDSL.addArgument(new NameExpr(toVar(drlxParseResult.getUnificationVariable())));
        } else {
            bindDSL.addArgument( new NameExpr(toVar(drlxParseResult.getExprBinding())) );
        }
        MethodCallExpr bindAsDSL = new MethodCallExpr(bindDSL, BIND_AS_CALL);
        bindAsDSL.addArgument( new NameExpr(toVar(drlxParseResult.getPatternBinding())) );
        final Expression constraintExpression = buildConstraintExpression(drlxParseResult, DrlxParseUtil.findLeftLeafOfMethodCall(drlxParseResult.getLeft().getExpression())  );
        bindAsDSL.addArgument(constraintExpression);
        return buildReactOn( drlxParseResult, bindAsDSL );
    }
}