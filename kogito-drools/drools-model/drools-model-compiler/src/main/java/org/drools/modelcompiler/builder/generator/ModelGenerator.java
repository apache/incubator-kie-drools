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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.ConditionalBranchDescr;
import org.drools.compiler.lang.descr.ConditionalElementDescr;
import org.drools.compiler.lang.descr.EvalDescr;
import org.drools.compiler.lang.descr.ExistsDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.ForallDescr;
import org.drools.compiler.lang.descr.NamedConsequenceDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.PatternSourceDescr;
import org.drools.compiler.lang.descr.QueryDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.Behavior;
import org.drools.core.time.TimeUtils;
import org.drools.core.util.ClassUtils;
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
import org.drools.javaparser.ast.drlx.expr.TemporalLiteralChunkExpr;
import org.drools.javaparser.ast.drlx.expr.TemporalLiteralExpr;
import org.drools.javaparser.ast.expr.AssignExpr;
import org.drools.javaparser.ast.expr.BinaryExpr;
import org.drools.javaparser.ast.expr.BinaryExpr.Operator;
import org.drools.javaparser.ast.expr.ClassExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.FieldAccessExpr;
import org.drools.javaparser.ast.expr.LambdaExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.SimpleName;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.javaparser.ast.expr.UnaryExpr;
import org.drools.javaparser.ast.expr.VariableDeclarationExpr;
import org.drools.javaparser.ast.stmt.BlockStmt;
import org.drools.javaparser.ast.stmt.ExpressionStmt;
import org.drools.javaparser.ast.stmt.ReturnStmt;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.javaparser.ast.type.Type;
import org.drools.javaparser.ast.type.UnknownType;
import org.drools.model.BitMask;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.RuleContext.RuleDialect;
import org.kie.api.definition.type.Position;

import static java.util.stream.Collectors.toList;

import static org.drools.javaparser.printer.PrintUtil.toDrlx;
import static org.drools.model.impl.NamesGenerator.generateName;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.generateLambdaWithoutParameters;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getClassFromContext;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.parseBlock;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
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

        customOperators.put("before", TemporalOperatorSpec.INSTANCE);
        customOperators.put("after", TemporalOperatorSpec.INSTANCE);
        customOperators.put("in", InOperatorSpec.INSTANCE);
    }

    public static final boolean GENERATE_EXPR_ID = true;

    static final String BUILD_CALL = "build";
    static final String RULE_CALL = "rule";
    static final String EXECUTE_CALL = "execute";
    static final String EXECUTESCRIPT_CALL = "executeScript";
    static final String ON_CALL = "on";
    static final String EXPR_CALL = "expr";
    static final String INPUT_CALL = "input";
    static final String BIND_CALL = "bind";
    static final String BIND_AS_CALL = "as";
    static final String ATTRIBUTE_CALL = "attribute";
    static final String DECLARATION_OF_CALL = "declarationOf";
    static final String TYPE_CALL = "type";
    static final String QUERY_INVOCATION_CALL = "call";
    static final String VALUE_OF_CALL = "valueOf";

    public static void generateModel(InternalKnowledgePackage pkg, PackageDescr packageDescr, PackageModel packageModel) {
        packageModel.addImports(pkg.getTypeResolver().getImports());
        packageModel.addGlobals(pkg.getGlobals());
        new WindowReferenceGenerator(packageModel, pkg).addWindowReferences(packageDescr.getWindowDeclarations());
        packageModel.addAllFunctions(packageDescr.getFunctions().stream().map(FunctionGenerator::toFunction).collect(toList()));


        for(RuleDescr descr : packageDescr.getRules()) {
            if (descr instanceof QueryDescr) {
                QueryGenerator.processQueryDef(pkg, packageModel, (QueryDescr) descr);
            }
        }

        for (RuleDescr descr : packageDescr.getRules()) {
            if (descr instanceof QueryDescr) {
                QueryGenerator.processQuery(packageModel, (QueryDescr) descr);
            } else {
                processRule(pkg, packageModel, descr);
            }
        }
    }


    private static void processRule(InternalKnowledgePackage pkg, PackageModel packageModel, RuleDescr ruleDescr) {
        RuleContext context = new RuleContext(pkg, packageModel.getExprIdGenerator(), Optional.of(ruleDescr));

        for(Entry<String, Object> kv : ruleDescr.getNamedConsequences().entrySet()) {
            context.addNamedConsequence(kv.getKey(), kv.getValue().toString());
        }

        visit(context, packageModel, ruleDescr.getLhs());
        MethodDeclaration ruleMethod = new MethodDeclaration(EnumSet.of(Modifier.PRIVATE), RULE_TYPE, "rule_" + toId( ruleDescr.getName() ) );

        ruleMethod.setJavadocComment(" Rule name: " + ruleDescr.getName() + " ");

        VariableDeclarationExpr ruleVar = new VariableDeclarationExpr(RULE_TYPE, RULE_CALL);

        MethodCallExpr ruleCall = new MethodCallExpr(null, RULE_CALL);
        if (!ruleDescr.getNamespace().isEmpty()) {
            ruleCall.addArgument( new StringLiteralExpr( ruleDescr.getNamespace() ) );
        }
        ruleCall.addArgument( new StringLiteralExpr( ruleDescr.getName() ) );

        MethodCallExpr buildCallScope = ruleCall;
        for (MethodCallExpr attributeExpr : ruleAttributes(context, ruleDescr)) {
            attributeExpr.setScope( buildCallScope );
            buildCallScope = attributeExpr;
        }

        MethodCallExpr buildCall = new MethodCallExpr(buildCallScope, BUILD_CALL, NodeList.nodeList(context.expressions));

        BlockStmt ruleConsequence = rewriteConsequence(context, ruleDescr.getConsequence().toString());

        BlockStmt ruleVariablesBlock = new BlockStmt();
        createVariables(ruleVariablesBlock, packageModel, context);
        ruleMethod.setBody(ruleVariablesBlock);

        List<String> usedDeclarationInRHS = extractUsedDeclarations(packageModel, context, ruleConsequence);
        MethodCallExpr onCall = onCall(usedDeclarationInRHS);
        MethodCallExpr executeCall = null;
        if (context.getRuleDialect() == RuleDialect.JAVA) {
            executeCall = executeCall(context, ruleVariablesBlock, ruleConsequence, usedDeclarationInRHS, onCall);
        } else if (context.getRuleDialect() == RuleDialect.MVEL) {
            executeCall = executeScriptCall(ruleDescr, onCall);
        }

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

    public static BlockStmt rewriteConsequence(RuleContext context, String consequence ) {
        if (context.getRuleDialect() == RuleDialect.MVEL) {
            // anyhow the consequence will be used as a ScriptBlock.
            return null;
        }
        String ruleConsequenceAsBlock = rewriteConsequenceBlock(context, consequence.trim() );
        return parseBlock(ruleConsequenceAsBlock);
    }

    public static List<String> extractUsedDeclarations(PackageModel packageModel, RuleContext context, BlockStmt ruleConsequence) {
        Set<String> existingDecls = new HashSet<>();
        existingDecls.addAll(context.getDeclarations().stream().map(DeclarationSpec::getBindingId).collect(toList()));
        existingDecls.addAll(packageModel.getGlobals().keySet());
        if (context.getRuleDialect() == RuleDialect.JAVA) {
            List<String> declUsedInRHS = ruleConsequence.getChildNodesByType(NameExpr.class).stream().map(NameExpr::getNameAsString).collect(toList());
            return existingDecls.stream().filter(declUsedInRHS::contains).collect(toList());
        } else {
            // if dialect is MVEL then avoid optimization, and just return them all.
            return existingDecls.stream().collect(Collectors.toList());
        }
    }

    public static MethodCallExpr executeCall(RuleContext context, BlockStmt ruleVariablesBlock, BlockStmt ruleConsequence, List<String> verifiedDeclUsedInRHS, MethodCallExpr onCall) {
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

    private static MethodCallExpr executeScriptCall(RuleDescr ruleDescr, MethodCallExpr onCall) {
        MethodCallExpr executeCall = new MethodCallExpr(onCall, EXECUTESCRIPT_CALL);
        executeCall.addArgument(new StringLiteralExpr("mvel"));
        StringLiteralExpr scriptText = new StringLiteralExpr();
        scriptText.setString(ruleDescr.getConsequence().toString()); // use the setter method in order for the string literal be properly escaped.
        executeCall.addArgument(scriptText);
        return executeCall;
    }

    public static MethodCallExpr onCall(List<String> usedArguments) {
        MethodCallExpr onCall = null;

        if (!usedArguments.isEmpty()) {
            onCall = new MethodCallExpr(null, ON_CALL);
            usedArguments.stream().map(DrlxParseUtil::toVar).forEach(onCall::addArgument );
        }
        return onCall;
    }

    public static void createVariables(BlockStmt block, PackageModel packageModel, RuleContext context) {

        for (DeclarationSpec decl : context.getDeclarations()) {
            if (!packageModel.getGlobals().containsKey(decl.getBindingId()) && !context.queryParameterWithName(p -> p.name.equals(decl.getBindingId())).isPresent()) {
                addVariable(block, decl);
            }
        }
    }

    private static void addVariable(BlockStmt ruleBlock, DeclarationSpec decl) {
        ClassOrInterfaceType varType = JavaParser.parseClassOrInterfaceType(Variable.class.getCanonicalName());
        Type declType = DrlxParseUtil.classToReferenceType(decl.declarationClass );

        varType.setTypeArguments(declType);
        VariableDeclarationExpr var_ = new VariableDeclarationExpr(varType, toVar(decl.getBindingId()), Modifier.FINAL);

        MethodCallExpr declarationOfCall = new MethodCallExpr(null, DECLARATION_OF_CALL);
        MethodCallExpr typeCall = new MethodCallExpr(null, TYPE_CALL);
        typeCall.addArgument( new ClassExpr(declType ));

        declarationOfCall.addArgument(typeCall);
        declarationOfCall.addArgument(new StringLiteralExpr(decl.variableName.orElse(decl.getBindingId())));

        decl.declarationSource.ifPresent(declarationOfCall::addArgument);

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
        int modifyPos = consequence.indexOf( "modify" );
        if (modifyPos < 0) {
            return consequence;
        }

        int lastCopiedEnd = 0;
        StringBuilder sb = new StringBuilder();
        sb.append( consequence.substring( lastCopiedEnd, modifyPos ) );

        for (; modifyPos >= 0; modifyPos = consequence.indexOf( "modify", modifyPos+6 )) {
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
            String block = consequence.substring( blockStart+1, blockEnd ).trim();
            for (String blockStatement : block.split( ";" )) {
                sb.append( decl ).append( "." ).append( blockStatement.trim() ).append( ";\n" );
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

    private static void visit(RuleContext context, PackageModel packageModel, BaseDescr descr) {
        if ( descr instanceof AndDescr) {
            visit(context, packageModel, ( (AndDescr) descr ));
        } else if ( descr instanceof OrDescr) {
            visit( context, packageModel, ( (OrDescr) descr ), "or");
        } else if ( descr instanceof PatternDescr && ((PatternDescr)descr).getSource() instanceof AccumulateDescr) {
            new AccumulateVisitor(context, packageModel).visit(( (AccumulateDescr)((PatternDescr) descr).getSource() ));
        } else if ( descr instanceof PatternDescr ) {
            visit( context, packageModel, ( (PatternDescr) descr ));
        } else if ( descr instanceof EvalDescr ) {
            visit( context, packageModel, ( (EvalDescr) descr ));
        } else if ( descr instanceof NotDescr) {
            visit( context, packageModel, ( (NotDescr) descr ), "not");
        } else if ( descr instanceof ExistsDescr) {
            visit( context, packageModel, ( (ExistsDescr) descr ), "exists");
        } else if ( descr instanceof ForallDescr) {
            visit( context, packageModel, ( (ForallDescr) descr ), "forall");
        } else if ( descr instanceof QueryDescr) {
            visit( context, packageModel, ( (QueryDescr) descr ));
        } else if ( descr instanceof NamedConsequenceDescr) {
           new NamedConsequenceVisitor(context, packageModel).visit(((NamedConsequenceDescr) descr ));
        } else if ( descr instanceof ConditionalBranchDescr) {
            new NamedConsequenceVisitor(context, packageModel).visit(((ConditionalBranchDescr) descr ));
        } else {
            throw new UnsupportedOperationException("TODO"); // TODO
        }
    }

    private static void visit( RuleContext context, PackageModel packageModel, ConditionalElementDescr descr, String methodName ) {
        final MethodCallExpr ceDSL = new MethodCallExpr(null, methodName);
        context.addExpression(ceDSL);
        context.pushExprPointer( ceDSL::addArgument );
        for (BaseDescr subDescr : descr.getDescrs()) {
            visit(context, packageModel, subDescr );
        }
        context.popExprPointer();
    }

    private static void visit( RuleContext context, PackageModel packageModel, EvalDescr descr ) {
        final String expression = descr.getContent().toString();

        final Optional<String> bindingIdFromFunctionCall =  DrlxParseUtil.findBindingIdFromFunctionCallExpression(expression);
        final Optional<String> bindingIdFromDot = DrlxParseUtil.findBindingIdFromDotExpression(expression);
        final String bindingId = bindingIdFromFunctionCall.map(Optional::of).orElse(bindingIdFromDot).orElseThrow(() -> new UnsupportedOperationException("unable to parse eval expression: " + expression));

        Class<?> patternType = context.getDeclarationById(bindingId)
                .map(DeclarationSpec::getDeclarationClass)
                .orElseThrow(RuntimeException::new);
        DrlxParseResult drlxParseResult = drlxParse(context, packageModel, patternType, bindingId, expression);
        processExpression(context, drlxParseResult);
    }

    public static void visit(RuleContext context, PackageModel packageModel, AndDescr descr) {
        // if it's the first (implied) `and` wrapping the first level of patterns, skip adding it to the DSL.
        if ( context.getExprPointerLevel() != 1 ) {
            final MethodCallExpr andDSL = new MethodCallExpr(null, "and");
            context.addExpression(andDSL);
            context.pushExprPointer( andDSL::addArgument );
        }
        for (BaseDescr subDescr : descr.getDescrs()) {
            context.parentDesc = descr;
            visit( context, packageModel, subDescr );
        }
        if ( context.getExprPointerLevel() != 1 ) {
            context.popExprPointer();
        }
    }

    public static void visit(RuleContext context, PackageModel packageModel, PatternDescr pattern ) {
        String className = pattern.getObjectType();
        List<? extends BaseDescr> constraintDescrs = pattern.getConstraint().getDescrs();

        // Expression is a query, get bindings from query parameter type
        if ( QueryGenerator.bindQuery( context, packageModel, pattern, constraintDescrs ) ) {
            return;
        }

        if ( QueryGenerator.createQueryCall(packageModel, context, pattern) ) {
            return;
        }

        Class<?> patternType = getClassFromContext(context.getPkg(),className);

        if (pattern.getIdentifier() == null) {
            pattern.setIdentifier( generateName("pattern_" + patternType.getSimpleName()) );
        }

        Optional<PatternSourceDescr> source = Optional.ofNullable(pattern.getSource());
        Optional<Expression> declarationSourceFrom = source.flatMap(new FromVisitor(context, packageModel)::visit);
        Optional<Expression> declarationSourceWindow = source.flatMap(new WindowReferenceGenerator(packageModel, context.getPkg())::visit);
        Optional<Expression> declarationSource = declarationSourceFrom.isPresent() ? declarationSourceFrom : declarationSourceWindow;
        context.addDeclaration(new DeclarationSpec(pattern.getIdentifier(), patternType, Optional.of(pattern), declarationSource));

        if (constraintDescrs.isEmpty() && pattern.getSource() == null) {
            MethodCallExpr dslExpr = new MethodCallExpr(null, INPUT_CALL);
            dslExpr.addArgument(new NameExpr(toVar(pattern.getIdentifier())));
            context.addExpression( dslExpr );
        } else {

            final boolean allConstraintsArePositional = !constraintDescrs.isEmpty() && constraintDescrs.stream()
                    .allMatch(c -> c instanceof ExprConstraintDescr
                            && ((ExprConstraintDescr) c).getType().equals(ExprConstraintDescr.Type.POSITIONAL));
            if(allConstraintsArePositional) {
                final MethodCallExpr andDSL = new MethodCallExpr(null, "and");
                context.addExpression(andDSL);
                context.pushExprPointer(andDSL::addArgument);
            }

            for (BaseDescr constraint : constraintDescrs) {
                String expression = getConstraintExpression(patternType, constraint);
                String patternIdentifier = pattern.getIdentifier();
                if(expression.contains(":=")) {
                    expression = expression.replace(":=", "==");
                }
                DrlxParseResult drlxParseResult = drlxParse(context, packageModel, patternType, patternIdentifier, expression);

                if(drlxParseResult.expr instanceof OOPathExpr) {

                    // If the  outer pattern does not have a binding we generate it
                    if(patternIdentifier == null) {
                        patternIdentifier = context.getExprId(patternType, expression);
                        context.addDeclaration(new DeclarationSpec(patternIdentifier, patternType, Optional.of(pattern), Optional.empty()));
                    }

                    new OOPathExprVisitor(context, packageModel).visit(patternType, patternIdentifier, (OOPathExpr)drlxParseResult.expr);
                } else {
                    // need to augment the reactOn inside drlxParseResult with the look-ahead properties.
                    Collection<String> lookAheadFieldsOfIdentifier = context.getRuleDescr()
                        .map(ruleDescr -> ruleDescr.lookAheadFieldsOfIdentifier(pattern))
                            .orElseGet(Collections::emptyList);
                    drlxParseResult.reactOnProperties.addAll(lookAheadFieldsOfIdentifier);
                    drlxParseResult.watchedProperties = getPatternListenedProperties(pattern);

                    if(pattern.isUnification()) {
                        drlxParseResult.setPatternBindingUnification(true);
                    }

                    processExpression( context, drlxParseResult );
                }
            }
            if(allConstraintsArePositional) {
                context.popExprPointer();
            }
        }
    }

    private static String getConstraintExpression(Class<?> patternType, BaseDescr constraint) {
        if (constraint instanceof ExprConstraintDescr && (( ExprConstraintDescr ) constraint).getType() == ExprConstraintDescr.Type.POSITIONAL) {
            int position = (( ExprConstraintDescr ) constraint).getPosition();
            return getFieldAtPosition(patternType, position) + " == " + constraint.toString();
        }
        return constraint.toString();
    }

    private static String getFieldAtPosition(Class<?> patternType, int position) {
        for (Field field : patternType.getDeclaredFields()) {
            Position p = field.getAnnotation( Position.class );
            if (p != null && p.value() == position) {
                return field.getName();
            }
        }
        throw new RuntimeException( "Cannot find field in position " + position + " for " + patternType );
    }

    private static String[] getPatternListenedProperties(PatternDescr pattern) {
        AnnotationDescr watchAnn = pattern != null ? pattern.getAnnotation( "watch" ) : null;
        return watchAnn == null ? new String[0] : watchAnn.getValue().toString().split(",");
    }


    private static void processExpression(RuleContext context, DrlxParseResult drlxParseResult) {
        if (drlxParseResult.hasUnificationVariable()) {
            Expression dslExpr = buildUnificationExpression(context, drlxParseResult);
            context.addExpression(dslExpr);
        } else if (drlxParseResult.expr != null) {
            Expression dslExpr = buildExpressionWithIndexing(context, drlxParseResult);
            context.addExpression(dslExpr);
        }
        if (drlxParseResult.exprBinding != null) {
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
            context.addDeclaration( new DeclarationSpec( bindId, result.exprType ) );
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
            TypedExpression left = DrlxParseUtil.toTypedExpression( context, packageModel, patternType, binaryExpr.getLeft(), usedDeclarations, reactOnProperties );
            TypedExpression right = DrlxParseUtil.toTypedExpression( context, packageModel, patternType, binaryExpr.getRight(), usedDeclarations, reactOnProperties );

            Expression combo;
            if ( left.isPrimitive() ) {
                combo = new BinaryExpr( left.getExpression(), right.getExpression(), operator );
            } else {
                switch ( operator ) {
                    case EQUALS:
                        MethodCallExpr methodCallExpr = new MethodCallExpr( left.getExpression(), "equals" );
                        methodCallExpr.addArgument( right.getExpression() ); // don't create NodeList with static method because missing "parent for child" would null and NPE
                        combo = methodCallExpr;
                        break;
                    case NOT_EQUALS:
                        MethodCallExpr methodCallExpr2 = new MethodCallExpr( left.getExpression(), "equals" );
                        methodCallExpr2.addArgument( right.getExpression() );
                        combo = methodCallExpr2;
                        combo = new UnaryExpr( combo, UnaryExpr.Operator.LOGICAL_COMPLEMENT );
                        break;
                    default:
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
            TypedExpression left = DrlxParseUtil.toTypedExpression( context, packageModel, patternType, unaryExpr, usedDeclarations, reactOnProperties );

            return new DrlxParseResult(patternType, exprId, bindingId, left.getExpression(), left.getType())
                    .setUsedDeclarations( usedDeclarations ).setReactOnProperties( reactOnProperties ).setLeft( left );
        }

        if ( drlxExpr instanceof PointFreeExpr ) {
            PointFreeExpr pointFreeExpr = (PointFreeExpr) drlxExpr;

            List<String> usedDeclarations = new ArrayList<>();
            Set<String> reactOnProperties = new HashSet<>();
            TypedExpression left = DrlxParseUtil.toTypedExpression( context, packageModel, patternType, pointFreeExpr.getLeft(), usedDeclarations, reactOnProperties );
            for (Expression rightExpr : pointFreeExpr.getRight()) {
                DrlxParseUtil.toTypedExpression( context, packageModel, patternType, rightExpr, usedDeclarations, reactOnProperties );
            }

            String operator = pointFreeExpr.getOperator().asString();
            CustomOperatorSpec opSpec = customOperators.get( operator );
            if (opSpec == null) {
                throw new UnsupportedOperationException("Unknown operator '" + operator + "' in expression: " + toDrlx(drlxExpr));
            }
            MethodCallExpr methodCallExpr = opSpec.getMethodCallExpr( pointFreeExpr, left );

            return new DrlxParseResult(patternType, exprId, bindingId, methodCallExpr, left.getType() )
                    .setUsedDeclarations( usedDeclarations ).setReactOnProperties( reactOnProperties ).setLeft( left ).setStatic( opSpec.isStatic() );
        }

        if (drlxExpr instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = (MethodCallExpr) drlxExpr;

            Optional<MethodDeclaration> functionCall = packageModel.getFunctions().stream().filter(m -> m.getName().equals(methodCallExpr.getName())).findFirst();
            if(functionCall.isPresent()) {
                Class<?> returnType = DrlxParseUtil.getClassFromContext(context.getPkg(), functionCall.get().getType().asString());
                NodeList<Expression> arguments = methodCallExpr.getArguments();
                List<String> usedDeclarations = new ArrayList<>();
                for(Expression arg : arguments) {
                    if(arg instanceof NameExpr) {
                        usedDeclarations.add(arg.toString());
                    }
                }
                return new DrlxParseResult(patternType, exprId, bindingId, methodCallExpr, returnType).setUsedDeclarations(usedDeclarations);
            } else {
                NameExpr _this = new NameExpr("_this");
                TypedExpression converted = DrlxParseUtil.toMethodCallWithClassCheck(methodCallExpr, patternType);
                Expression withThis = DrlxParseUtil.prepend(_this, converted.getExpression());
                return new DrlxParseResult(patternType, exprId, bindingId, withThis, converted.getType());
            }

        }

        if (drlxExpr instanceof NameExpr) {
            NameExpr methodCallExpr = (NameExpr) drlxExpr;

            NameExpr _this = new NameExpr("_this");
            TypedExpression converted = DrlxParseUtil.toMethodCallWithClassCheck(methodCallExpr, patternType);
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

    private interface CustomOperatorSpec {
        MethodCallExpr getMethodCallExpr( PointFreeExpr pointFreeExpr, TypedExpression left );
        boolean isStatic();
    }

    private static class TemporalOperatorSpec implements CustomOperatorSpec {
        static final TemporalOperatorSpec INSTANCE = new TemporalOperatorSpec();

        public MethodCallExpr getMethodCallExpr( PointFreeExpr pointFreeExpr, TypedExpression left ) {
            MethodCallExpr methodCallExpr = new MethodCallExpr( null, pointFreeExpr.getOperator().asString() );
            if (pointFreeExpr.getArg1() != null) {
                addArgumentToMethodCall( pointFreeExpr.getArg1(), methodCallExpr );
                if (pointFreeExpr.getArg2() != null) {
                    addArgumentToMethodCall( pointFreeExpr.getArg2(), methodCallExpr );
                }
            }
            return methodCallExpr;
        }

        @Override
        public boolean isStatic() {
            return true;
        }
    }

    private static class InOperatorSpec implements CustomOperatorSpec {
        static final InOperatorSpec INSTANCE = new InOperatorSpec();

        public MethodCallExpr getMethodCallExpr( PointFreeExpr pointFreeExpr, TypedExpression left ) {
            MethodCallExpr asList = new MethodCallExpr( new NameExpr("java.util.Arrays"), "asList" );
            for (Expression rightExpr : pointFreeExpr.getRight()) {
                asList.addArgument( rightExpr );
            }
            MethodCallExpr methodCallExpr = new MethodCallExpr( asList, "contains" );
            methodCallExpr.addArgument( left.getExpression() );
            return methodCallExpr;
        }

        @Override
        public boolean isStatic() {
            return false;
        }
    }

    static class DrlxParseResult {

        final Class<?> patternType;
        final Expression expr;
        final Class<?> exprType;

        private String exprId;
        private String patternBinding;
        boolean isPatternBindingUnification = false;

        private String exprBinding;

        ConstraintType decodeConstraintType;
        List<String> usedDeclarations = Collections.emptyList();
        Set<String> reactOnProperties = Collections.emptySet();
        String[] watchedProperties;

        TypedExpression left;
        TypedExpression right;
        boolean isStatic;

        public DrlxParseResult( Class<?> patternType, String exprId, String patternBinding, Expression expr, Class<?> exprType) {
            this.patternType = patternType;
            this.exprId = exprId;
            this.patternBinding = patternBinding;
            this.expr = expr;
            this.exprType = exprType;
        }

        public DrlxParseResult setDecodeConstraintType( ConstraintType decodeConstraintType ) {
            this.decodeConstraintType = decodeConstraintType;
            return this;
        }

        public DrlxParseResult setUsedDeclarations( List<String> usedDeclarations ) {
            this.usedDeclarations = usedDeclarations;
            return this;
        }

        public DrlxParseResult setReactOnProperties( Set<String> reactOnProperties ) {
            this.reactOnProperties = reactOnProperties;
            return this;
        }

        public DrlxParseResult setPatternBindingUnification( Boolean unification) {
            this.isPatternBindingUnification = unification;
            return this;
        }

        public DrlxParseResult addReactOnProperty( String reactOnProperty ) {
            if (reactOnProperties.isEmpty()) {
                reactOnProperties = new HashSet<>();
            }
            this.reactOnProperties.add(reactOnProperty);
            return this;
        }

        public DrlxParseResult setLeft( TypedExpression left ) {
            this.left = left;
            return this;
        }

        public DrlxParseResult setRight( TypedExpression right ) {
            this.right = right;
            return this;
        }

        public DrlxParseResult setStatic( boolean aStatic ) {
            isStatic = aStatic;
            return this;
        }

        public String getExprId() {
            return exprId;
        }

        public String getPatternBinding() {
            return patternBinding;
        }

        public void setExprId(String exprId) {
            this.exprId = exprId;
        }

        public void setPatternBinding(String patternBinding) {
            this.patternBinding = patternBinding;
        }

        public void setExprBinding(String exprBinding) {
            this.exprBinding = exprBinding;
        }

        public boolean hasUnificationVariable() {
            return Optional.ofNullable(left).flatMap(TypedExpression::getUnificationVariable).isPresent() ||
                    Optional.ofNullable(right).flatMap(TypedExpression::getUnificationVariable).isPresent();
        }

        public String getUnificationVariable() {
            return left.getUnificationVariable().isPresent() ? left.getUnificationVariable().get() : right.getUnificationVariable().get();
        }

        public String getUnificationName() {
            return left.getUnificationName().isPresent() ? left.getUnificationName().get() : right.getUnificationName().get();
        }

        public Class<?> getUnificationVariableType() {
            return left.getUnificationVariable().isPresent() ? right.getType() : left.getType();
        }
    }



    private static void addArgumentToMethodCall( Expression expr, MethodCallExpr methodCallExpr ) {
        if (expr instanceof TemporalLiteralExpr ) {
            TemporalLiteralExpr tempExpr1 = (TemporalLiteralExpr) expr;
            final TemporalLiteralChunkExpr firstTemporalExpression = tempExpr1.getChunks().iterator().next();
            methodCallExpr.addArgument("" + firstTemporalExpression.getValue() );
            methodCallExpr.addArgument( "java.util.concurrent.TimeUnit." + firstTemporalExpression.getTimeUnit() );
        } else {
            methodCallExpr.addArgument( expr );
        }
    }

    public static Expression buildExpressionWithIndexing(RuleContext context, DrlxParseResult drlxParseResult) {
        String exprId = drlxParseResult.exprId;
        MethodCallExpr exprDSL = new MethodCallExpr(null, EXPR_CALL);
        if (exprId != null && !"".equals(exprId)) {
            exprDSL.addArgument( new StringLiteralExpr(exprId) );
        }

        exprDSL = buildExpression(context, drlxParseResult, exprDSL );
        exprDSL = buildIndexedBy( drlxParseResult, exprDSL );
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
        if(!drlxParseResult.isPatternBindingUnification) {
            exprDSL.addArgument(new NameExpr(toVar(drlxParseResult.patternBinding)));
        } else {
            usedDeclarationsWithUnification.add(drlxParseResult.patternBinding);
        }
        usedDeclarationsWithUnification.addAll(drlxParseResult.usedDeclarations);
        usedDeclarationsWithUnification.stream()
                .map(x -> QueryGenerator.substituteBindingWithQueryParameter(context, x))
                .forEach(exprDSL::addArgument);
        exprDSL.addArgument(buildConstraintExpression( drlxParseResult, drlxParseResult.expr ));
        return exprDSL;
    }

    private static MethodCallExpr buildIndexedBy( DrlxParseResult drlxParseResult, MethodCallExpr exprDSL ) {
        ConstraintType decodeConstraintType = drlxParseResult.decodeConstraintType;
        TypedExpression left = drlxParseResult.left;
        TypedExpression right = drlxParseResult.right;

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
            indexedByDSL.addArgument( "" + indexIdGenerator.getFieldId( drlxParseResult.patternType, left.getFieldName() ) );
            indexedByDSL.addArgument( indexedBy_leftOperandExtractor );

            List<String> usedDeclarations = drlxParseResult.usedDeclarations;
            if ( usedDeclarations.isEmpty() ) {
                indexedByDSL.addArgument( right.getExpression() );
            } else if ( usedDeclarations.size() == 1 ) {
                LambdaExpr indexedBy_rightOperandExtractor = new LambdaExpr();
                indexedBy_rightOperandExtractor.addParameter(new Parameter(new UnknownType(), usedDeclarations.iterator().next()));
                indexedBy_rightOperandExtractor.setBody(new ExpressionStmt(!leftContainsThis ? left.getExpression() : right.getExpression()) );
                indexedByDSL.addArgument( indexedBy_rightOperandExtractor );
            } else {
//                throw new UnsupportedOperationException( "TODO" ); // TODO: possibly not to be indexed
            }
            return indexedByDSL;
        }
        return exprDSL;
    }

    private static MethodCallExpr buildReactOn( DrlxParseResult drlxParseResult, MethodCallExpr exprDSL ) {
        if ( !drlxParseResult.reactOnProperties.isEmpty() ) {
            exprDSL = new MethodCallExpr(exprDSL, "reactOn");
            drlxParseResult.reactOnProperties.stream()
                             .map( StringLiteralExpr::new )
                             .forEach( exprDSL::addArgument );

        }

        if ( drlxParseResult.watchedProperties != null && drlxParseResult.watchedProperties.length > 0 ) {
            exprDSL = new MethodCallExpr(exprDSL, "watch");
            Stream.of( drlxParseResult.watchedProperties )
                    .map( StringLiteralExpr::new )
                    .forEach( exprDSL::addArgument );
        }

        return exprDSL;
    }

    private static Expression buildConstraintExpression( DrlxParseResult drlxParseResult, Expression expr ) {
        return drlxParseResult.isStatic ? expr : generateLambdaWithoutParameters(drlxParseResult.usedDeclarations, expr);
    }

    public static MethodCallExpr buildBinding(DrlxParseResult drlxParseResult ) {
        MethodCallExpr bindDSL = new MethodCallExpr(null, BIND_CALL);
        if(drlxParseResult.hasUnificationVariable()) {
            bindDSL.addArgument(new NameExpr(toVar(drlxParseResult.getUnificationVariable())));
        } else {
            bindDSL.addArgument( new NameExpr(toVar(drlxParseResult.exprBinding)) );
        }
        MethodCallExpr bindAsDSL = new MethodCallExpr(bindDSL, BIND_AS_CALL);
        bindAsDSL.addArgument( new NameExpr(toVar(drlxParseResult.patternBinding)) );
        bindAsDSL.addArgument( buildConstraintExpression( drlxParseResult, drlxParseResult.left.getExpression() ) );
        return buildReactOn( drlxParseResult, bindAsDSL );
    }
}