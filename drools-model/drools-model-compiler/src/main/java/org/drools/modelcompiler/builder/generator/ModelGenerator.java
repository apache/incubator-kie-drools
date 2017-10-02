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

import org.drools.compiler.lang.descr.*;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.Behavior;
import org.drools.core.time.TimeUtils;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.index.IndexUtil;
import org.drools.core.util.index.IndexUtil.ConstraintType;
import org.drools.drlx.DrlxParser;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.body.MethodDeclaration;
import org.drools.javaparser.ast.body.Parameter;
import org.drools.javaparser.ast.drlx.expr.PointFreeExpr;
import org.drools.javaparser.ast.drlx.expr.TemporalLiteralExpr;
import org.drools.javaparser.ast.expr.*;
import org.drools.javaparser.ast.expr.BinaryExpr.Operator;
import org.drools.javaparser.ast.stmt.BlockStmt;
import org.drools.javaparser.ast.stmt.ExpressionStmt;
import org.drools.javaparser.ast.stmt.ReturnStmt;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.javaparser.ast.type.Type;
import org.drools.javaparser.ast.type.UnknownType;
import org.drools.model.BitMask;
import org.drools.model.Query;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.RuleDescrImpl;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.drools.javaparser.printer.PrintUtil.toDrlx;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.*;
import static org.drools.modelcompiler.builder.generator.StringUtil.toId;

public class ModelGenerator {

    private static final ClassOrInterfaceType RULE_TYPE = JavaParser.parseClassOrInterfaceType( Rule.class.getCanonicalName() );
    private static final ClassOrInterfaceType BITMASK_TYPE = JavaParser.parseClassOrInterfaceType( BitMask.class.getCanonicalName() );

    public static final boolean GENERATE_EXPR_ID = true;

    public static final String BUILD_CALL = "build";
    public static final String RULE_CALL = "rule";
    public static final String EXECUTE_CALL = "execute";
    public static final String ON_CALL = "on";
    public static final String QUERY_CALL = "query";
    public static final String EXPR_CALL = "expr";
    public static final String INPUT_CALL = "input";

    public static PackageModel generateModel(InternalKnowledgePackage pkg, List<RuleDescrImpl> rules, List<FunctionDescr> functions) {
        String name = pkg.getName();
        PackageModel packageModel = new PackageModel(name);
        packageModel.addImports(pkg.getTypeResolver().getImports());
        packageModel.addGlobals(pkg.getGlobals());
        packageModel.addAllFunctions(functions.stream().map(FunctionGenerator::toFunction).collect(Collectors.toList()));

        for (RuleDescrImpl descr : rules) {
            final RuleDescr descriptor = descr.getDescr();
            if (descriptor instanceof QueryDescr) {
                processQuery(pkg, packageModel, (QueryDescr) descriptor);
            } else {
                processRule(pkg, packageModel, descriptor);
            }
        }

        packageModel.print();
        return packageModel;
    }

    private static void processRule(InternalKnowledgePackage pkg, PackageModel packageModel, RuleDescr ruleDescr) {
        RuleContext context = new RuleContext(pkg, packageModel.getExprIdGenerator() );

        for(Entry<String, Object> kv : ruleDescr.getNamedConsequences().entrySet()) {
            context.addNamedConsequence(kv.getKey(), kv.getValue().toString());
        }

        visit(context, packageModel, ruleDescr.getLhs());
        MethodDeclaration ruleMethod = new MethodDeclaration(EnumSet.of(Modifier.PRIVATE), RULE_TYPE, "rule_" + toId( ruleDescr.getName() ) );

        VariableDeclarationExpr ruleVar = new VariableDeclarationExpr(RULE_TYPE, RULE_CALL);

        MethodCallExpr ruleCall = new MethodCallExpr(null, RULE_CALL);
        ruleCall.addArgument( new StringLiteralExpr( ruleDescr.getName() ) );

        MethodCallExpr buildCall = new MethodCallExpr(ruleCall, BUILD_CALL, NodeList.nodeList(context.expressions));

        BlockStmt ruleConsequence = rewriteConsequence(context, ruleDescr.getConsequence().toString());

        BlockStmt ruleVariablesBlock = createRuleVariables(packageModel, context);
        ruleMethod.setBody(ruleVariablesBlock);

        List<String> usedDeclarationInRHS = extractUsedDeclarations(packageModel, context, ruleConsequence);
        MethodCallExpr onCall = onCall(usedDeclarationInRHS);
        MethodCallExpr executeCall = executeCall(context, ruleVariablesBlock, ruleConsequence, usedDeclarationInRHS, onCall);

        buildCall.addArgument( executeCall );

        ruleVariablesBlock.addStatement(new AssignExpr(ruleVar, buildCall, AssignExpr.Operator.ASSIGN));

        ruleVariablesBlock.addStatement( new ReturnStmt(RULE_CALL) );
        packageModel.putRuleMethod("rule_" + toId( ruleDescr.getName() ), ruleMethod);
    }

    public static BlockStmt rewriteConsequence(RuleContext context, String consequence ) {
        String ruleConsequenceAsBlock = rewriteConsequenceBlock(context, consequence.trim() );
        return parseBlock(ruleConsequenceAsBlock);
    }

    public static List<String> extractUsedDeclarations(PackageModel packageModel, RuleContext context, BlockStmt ruleConsequence) {
        List<String> declUsedInRHS = ruleConsequence.getChildNodesByType(NameExpr.class).stream().map(NameExpr::getNameAsString).collect(Collectors.toList());
        Set<String> existingDecls = new HashSet<>();
        existingDecls.addAll(context.declarations.keySet());
        existingDecls.addAll(packageModel.getGlobals().keySet());
        return existingDecls.stream().filter(declUsedInRHS::contains).collect(Collectors.toList());
    }

    public static MethodCallExpr executeCall(RuleContext context, BlockStmt ruleVariablesBlock, BlockStmt ruleConsequence, List<String> verifiedDeclUsedInRHS, MethodCallExpr onCall) {
        boolean rhsRewritten = rewriteRHS(context, ruleVariablesBlock, ruleConsequence);
        MethodCallExpr executeCall = new MethodCallExpr(onCall, EXECUTE_CALL);
        LambdaExpr executeLambda = new LambdaExpr();
        executeCall.addArgument(executeLambda);
        executeLambda.setEnclosingParameters(true);
        if (rhsRewritten) {
            executeLambda.addParameter(new Parameter(new UnknownType(), "drools"));
        }
        verifiedDeclUsedInRHS.stream().map(x -> new Parameter(new UnknownType(), x)).forEach(executeLambda::addParameter);
        executeLambda.setBody( ruleConsequence );
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

    public static BlockStmt createRuleVariables(PackageModel packageModel, RuleContext context) {
        BlockStmt ruleBlock = new BlockStmt();

        for ( Entry<String, DeclarationSpec> decl : context.declarations.entrySet() ) {
            if ( !packageModel.getGlobals().containsKey( decl.getKey() ) ) {
                addVariable( ruleBlock, decl );
            }
        }
        return ruleBlock;
    }

    private static void processQuery(InternalKnowledgePackage pkg, PackageModel packageModel, QueryDescr ruleDescr) {
        RuleContext context = new RuleContext(pkg, packageModel.getExprIdGenerator());
        visit(context, packageModel, ruleDescr);
        MethodDeclaration queryMethod = new MethodDeclaration(EnumSet.of(Modifier.PRIVATE), getQueryType(context.queryParameters), "query_" + toId(ruleDescr.getName()));

        BlockStmt queryVariables = createRuleVariables(packageModel, context);
        queryMethod.setBody(queryVariables);
        VariableDeclarationExpr queryVar = new VariableDeclarationExpr(getQueryType(context.queryParameters), QUERY_CALL);

        MethodCallExpr queryCall = new MethodCallExpr(null, QUERY_CALL);
        queryCall.addArgument(new StringLiteralExpr(ruleDescr.getName()));
        for (QueryParameter qp : context.queryParameters) {
            queryCall.addArgument(new NameExpr(toVar(qp.name)));
        }

        MethodCallExpr viewCall = new MethodCallExpr(queryCall, BUILD_CALL);
        context.expressions.forEach(viewCall::addArgument);

        AssignExpr ruleAssign = new AssignExpr(queryVar, viewCall, AssignExpr.Operator.ASSIGN);
        queryVariables.addStatement(ruleAssign);

        queryVariables.addStatement(new ReturnStmt(QUERY_CALL));
        packageModel.putQueryMethod(queryMethod);
    }

    private static void addVariable(BlockStmt ruleBlock, Entry<String, DeclarationSpec> decl) {
        ClassOrInterfaceType varType = JavaParser.parseClassOrInterfaceType(Variable.class.getCanonicalName());
        Type declType = DrlxParseUtil.classToReferenceType(decl.getValue().declarationClass );

        varType.setTypeArguments(declType);
        VariableDeclarationExpr var_ = new VariableDeclarationExpr(varType, toVar(decl.getKey()), Modifier.FINAL);

        MethodCallExpr declarationOfCall = new MethodCallExpr(null, "declarationOf");
        MethodCallExpr typeCall = new MethodCallExpr(null, "type");
        typeCall.addArgument( new ClassExpr(declType ));
        declarationOfCall.addArgument(typeCall);
        declarationOfCall.addArgument(new StringLiteralExpr(decl.getKey()));

        decl.getValue().getEntryPoint().ifPresent( ep -> {
            MethodCallExpr entryPointCall = new MethodCallExpr(null, "entryPoint");
            entryPointCall.addArgument( new StringLiteralExpr(ep ) );
            declarationOfCall.addArgument( entryPointCall );
        } );
        for ( BehaviorDescr behaviorDescr : decl.getValue().getBehaviors() ) {
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


    private static ClassOrInterfaceType getQueryType(List<QueryParameter> queryParameters) {
        Class<?> res = Query.getQueryClassByArity(queryParameters.size());
        ClassOrInterfaceType queryType = JavaParser.parseClassOrInterfaceType(res.getCanonicalName());

        Type[] genericType = queryParameters.stream()
                .map(e -> e.type)
                .map(DrlxParseUtil::classToReferenceType)
                .toArray(Type[]::new);

        if (genericType.length > 0) {
            queryType.setTypeArguments(genericType);
        }

        return queryType;
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
            if ( context.declarations.get( decl ) == null) {
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
        List<MethodCallExpr> methodCallExprs = rhs.getChildNodesByType(MethodCallExpr.class);
        List<MethodCallExpr> updateExprs = new ArrayList<>();

        boolean hasWMAs = methodCallExprs.stream()
           .filter( ModelGenerator::isWMAMethod )
           .peek( mce -> {
                if (!mce.getScope().isPresent()) {
                    mce.setScope(new NameExpr("drools"));
                }
                if (mce.getNameAsString().equals("update")) {
                    updateExprs.add(mce);
                }
           })
           .count() > 0;

        for (MethodCallExpr updateExpr : updateExprs) {
            Expression argExpr = updateExpr.getArgument( 0 );
            if (argExpr instanceof NameExpr) {
                String updatedVar = ( (NameExpr) argExpr ).getNameAsString();
                Class<?> updatedClass = context.declarations.get( updatedVar ).declarationClass;

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

        return hasWMAs;
    }

    private static boolean isWMAMethod( MethodCallExpr mce ) {
        return isDroolsScopeInWMA( mce ) && (
                mce.getNameAsString().equals("insert") ||
                mce.getNameAsString().equals("delete") ||
                mce.getNameAsString().equals("update") );
    }

    private static boolean isDroolsScopeInWMA( MethodCallExpr mce ) {
        return !mce.getScope().isPresent() || hasScope( mce, "drools" );
    }

    private static boolean hasScope( MethodCallExpr mce, String scope ) {
        return mce.getScope().get() instanceof NameExpr &&
               ( (NameExpr) mce.getScope().get() ).getNameAsString().equals( scope );
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


    private static void visit(RuleContext context, PackageModel packageModel, QueryDescr descr) {

        for (int i = 0; i < descr.getParameters().length; i++) {
            final String argument = descr.getParameters()[i];
            final String type = descr.getParameterTypes()[i];
            context.declarations.put(argument, new DeclarationSpec(context.getClassFromContext(type)));
            QueryParameter queryParameter = new QueryParameter(argument, context.getClassFromContext(type));
            context.queryParameters.add(queryParameter);
            packageModel.putQueryVariable("query_" + descr.getName(), queryParameter);
        }

        visit(context, packageModel, descr.getLhs());
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
        String expression = descr.getContent().toString();
        int dot = expression.indexOf( '.' );
        if ( dot < 0 ) {
            throw new UnsupportedOperationException( "unable to parse eval expression: " + expression );
        }
        String bindingId = expression.substring( 0, dot );

        Class<?> patternType = context.declarations.get(bindingId).declarationClass;
        DrlxParseResult drlxParseResult = drlxParse(context, packageModel, patternType, bindingId, expression);
        Expression dslExpr = buildExpressionWithIndexing(drlxParseResult);
        context.addExpression( dslExpr );
    }

    private static void visit(RuleContext context, PackageModel packageModel, AndDescr descr) {
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

        // Expression is a query, get bindings from query parameter type
        String queryName = "query_" + className;
        MethodDeclaration queryMethod = packageModel.getQueryMethod(queryName);
        List<? extends BaseDescr> descriptors = pattern.getConstraint().getDescrs();
        if(queryMethod != null) {
            createQueryCallDSL(context, packageModel, queryName, queryMethod, descriptors);
            return;
        }

        Class<?> patternType = context.getClassFromContext(className);

        if (pattern.getIdentifier() != null) {
            context.declarations.put( pattern.getIdentifier(), new DeclarationSpec( patternType, pattern ));
        }

        if (descriptors.isEmpty()) {
            MethodCallExpr dslExpr = new MethodCallExpr(null, INPUT_CALL);
            if (pattern.getIdentifier() != null) {
                dslExpr.addArgument(new NameExpr(toVar(pattern.getIdentifier())));
            } else {
                MethodCallExpr declarationOfCall = new MethodCallExpr(null, "declarationOf");
                MethodCallExpr typeCall = new MethodCallExpr(null, "type");
                typeCall.addArgument( new ClassExpr( JavaParser.parseClassOrInterfaceType(patternType.getCanonicalName()) ));
                declarationOfCall.addArgument(typeCall);
                dslExpr.addArgument( declarationOfCall );
            }
            context.addExpression( dslExpr );
        } else {
            for (BaseDescr constraint : descriptors) {
                String expression = constraint.toString();
                DrlxParseResult drlxParseResult = drlxParse(context, packageModel, patternType, pattern.getIdentifier(), expression);
                Expression dslExpr = buildExpressionWithIndexing(drlxParseResult);

                context.addExpression( dslExpr );
            }
        }
    }

    private static void createQueryCallDSL(RuleContext context, PackageModel packageModel, String queryName, MethodDeclaration queryMethod, List<? extends BaseDescr> descriptors) {
        NameExpr queryCall = new NameExpr(queryMethod.getName());
        MethodCallExpr callCall = new MethodCallExpr(queryCall, "call");

        for (int i = 0; i < descriptors.size(); i++) {
            String itemText = descriptors.get(i).getText();
            if(isLiteral(itemText)) {
                MethodCallExpr valueOfMethod = new MethodCallExpr(null, "valueOf");
                valueOfMethod.addArgument(new NameExpr(itemText));
                callCall.addArgument(valueOfMethod);
            } else {
                QueryParameter qp = packageModel.queryVariables(queryName).get(i);
                context.declarations.put(itemText, new DeclarationSpec(qp.type));
                callCall.addArgument(new NameExpr(toVar(itemText)));
            }
        }

        context.addExpression(callCall);
    }

    public static boolean isLiteral(String value) {
        return value != null && value.length() > 0 &&
                ( Character.isDigit(value.charAt(0)) || value.charAt(0) == '"' || "true".equals(value) || "false".equals(value) || "null".equals(value) );
    }

    public static DrlxParseResult drlxParse(RuleContext context, PackageModel packageModel, Class<?> patternType, String bindingId, String expression) {
        if (expression.startsWith( bindingId + "." )) {
            expression = expression.substring( bindingId.length()+1 );
        }

        Expression drlxExpr = DrlxParser.parseExpression( expression );

        String exprId;
        if ( GENERATE_EXPR_ID ) {
            exprId = context.getExprId( patternType, expression );
        }

        if ( drlxExpr instanceof BinaryExpr ) {
            BinaryExpr binaryExpr = (BinaryExpr) drlxExpr;
            Operator operator = binaryExpr.getOperator();

            IndexUtil.ConstraintType decodeConstraintType = DrlxParseUtil.toConstraintType( operator );
            Set<String> usedDeclarations = new HashSet<>();
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

            return new DrlxParseResult(patternType, exprId, bindingId, decodeConstraintType, usedDeclarations, reactOnProperties, left, right, combo, false );
        }

        if ( drlxExpr instanceof PointFreeExpr ) {
            PointFreeExpr pointFreeExpr = (PointFreeExpr) drlxExpr;

            Set<String> usedDeclarations = new HashSet<>();
            Set<String> reactOnProperties = new HashSet<>();
            DrlxParseUtil.toTypedExpression( context, packageModel, patternType, pointFreeExpr.getLeft(), usedDeclarations, reactOnProperties );
            DrlxParseUtil.toTypedExpression( context, packageModel, patternType, pointFreeExpr.getRight(), usedDeclarations, reactOnProperties );

            MethodCallExpr methodCallExpr = new MethodCallExpr( null, pointFreeExpr.getOperator().asString() );
            if (pointFreeExpr.getArg1() != null) {
                addArgumentToMethodCall( pointFreeExpr.getArg1(), methodCallExpr );
                if (pointFreeExpr.getArg2() != null) {
                    addArgumentToMethodCall( pointFreeExpr.getArg2(), methodCallExpr );
                }
            }

            return new DrlxParseResult(patternType, exprId, bindingId, null, usedDeclarations, reactOnProperties, null, null, methodCallExpr, true );
        }

        if (drlxExpr instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = (MethodCallExpr) drlxExpr;

            NameExpr _this = new NameExpr("_this");
            MethodCallExpr converted = DrlxParseUtil.toMethodCallWithClassCheck(methodCallExpr, patternType);
            Expression withThis = DrlxParseUtil.prepend(_this, converted);

            return new DrlxParseResult(patternType, exprId, bindingId, null, new HashSet<>(), new HashSet<>(), null, null, withThis, false);
        }

        throw new UnsupportedOperationException("Unknown expression: " + toDrlx(drlxExpr)); // TODO

    }

    static class DrlxParseResult {

        public DrlxParseResult(Class<?> patternType, String exprId, String bindingId, ConstraintType decodeConstraintType, Set<String> usedDeclarations, Set<String> reactOnProperties, TypedExpression left, TypedExpression right, Expression expr, boolean isStatic) {
            this.patternType = patternType;
            this.exprId = exprId;
            this.bindingId = bindingId;
            this.decodeConstraintType = decodeConstraintType;
            this.usedDeclarations = usedDeclarations;
            this.reactOnProperties = reactOnProperties;
            this.left = left;
            this.right = right;
            this.expr = expr;
            this.isStatic = isStatic;
        }

        Class<?> patternType;
        String exprId;
        String bindingId;
        ConstraintType decodeConstraintType;

        Set<String> usedDeclarations;
        Set<String> reactOnProperties;

        TypedExpression left;
        TypedExpression right;
        Expression expr;
        boolean isStatic;
    }



    private static void addArgumentToMethodCall( Expression expr, MethodCallExpr methodCallExpr ) {
        if (expr instanceof TemporalLiteralExpr ) {
            TemporalLiteralExpr tempExpr1 = (TemporalLiteralExpr) expr;
            methodCallExpr.addArgument( "" + tempExpr1.getValue() );
            methodCallExpr.addArgument( "java.util.concurrent.TimeUnit." + tempExpr1.getTimeUnit() );
        } else {
            methodCallExpr.addArgument( expr );
        }
    }

    private static Expression buildExpressionWithIndexing(DrlxParseResult drlxParseResult ) {

        String exprId = drlxParseResult.exprId;
        String bindingId = drlxParseResult.bindingId;
        Set<String> usedDeclarations = drlxParseResult.usedDeclarations;
        ConstraintType decodeConstraintType = drlxParseResult.decodeConstraintType;
        TypedExpression left = drlxParseResult.left;
        TypedExpression right = drlxParseResult.right;
        Set<String> reactOnProperties = drlxParseResult.reactOnProperties;

        MethodCallExpr exprDSL = new MethodCallExpr(null, EXPR_CALL);
        if (exprId != null && !"".equals(exprId)) {
            exprDSL.addArgument( new StringLiteralExpr(exprId) );
        }
        if (bindingId != null) {
            exprDSL.addArgument( new NameExpr(toVar(bindingId)) );
        } else {
            MethodCallExpr declarationOfCall = new MethodCallExpr(null, "declarationOf");
            MethodCallExpr typeCall = new MethodCallExpr(null, "type");
            typeCall.addArgument( new ClassExpr( JavaParser.parseClassOrInterfaceType(drlxParseResult.patternType.getCanonicalName()) ));
            declarationOfCall.addArgument(typeCall);
            exprDSL.addArgument( declarationOfCall );
        }
        usedDeclarations.stream().map( x -> new NameExpr(toVar(x))).forEach(exprDSL::addArgument);

        Expression exprArg = drlxParseResult.expr;
        if (!drlxParseResult.isStatic) {
            exprArg = generateLambdaWithoutParameters(usedDeclarations, drlxParseResult.expr);
        }

        exprDSL.addArgument(exprArg);

        Expression result = exprDSL;


        // -- all indexing stuff --
        // .indexBy(..) is only added if left is not an identity expression:
        if ( decodeConstraintType != null && !(left.getExpression() instanceof NameExpr && ((NameExpr)left.getExpression()).getName().getIdentifier().equals("_this")) ) {
            Class<?> indexType = Stream.of( left, right ).map( TypedExpression::getType )
                                       .flatMap( ModelGenerator::optToStream )
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
            indexedByDSL.addArgument( indexedBy_leftOperandExtractor );
            if ( usedDeclarations.isEmpty() ) {
                Expression indexedBy_rightValue = right.getExpression();
                indexedByDSL.addArgument( indexedBy_rightValue );
            } else if ( usedDeclarations.size() == 1 ) {
                LambdaExpr indexedBy_rightOperandExtractor = new LambdaExpr();
                indexedBy_rightOperandExtractor.addParameter(new Parameter(new UnknownType(), usedDeclarations.iterator().next()));
                indexedBy_rightOperandExtractor.setBody(new ExpressionStmt(!leftContainsThis ? left.getExpression() : right.getExpression()) );
                indexedByDSL.addArgument( indexedBy_rightOperandExtractor );
            } else {
                throw new UnsupportedOperationException( "TODO" ); // TODO: possibly not to be indexed
            }
            result = indexedByDSL;
        }
        // -- END all indexing stuff --


        // -- all reactOn stuff --
        if ( !reactOnProperties.isEmpty() ) {
            MethodCallExpr reactOnDSL = new MethodCallExpr(result, "reactOn");
            reactOnProperties.stream()
                             .map( StringLiteralExpr::new )
                             .forEach( reactOnDSL::addArgument );

            result = reactOnDSL;
        }

        return result;
    }


    /**
     * waiting for java 9 Optional improvement
     */
    static <T> Stream<T> optToStream(Optional<T> opt) {
        return opt.map( Stream::of ).orElseGet( Stream::empty );
    }

}