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
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.compiler.compiler.DrlExprParser;
import org.drools.compiler.lang.descr.AccumulateDescr;
import org.drools.compiler.lang.descr.AndDescr;
import org.drools.compiler.lang.descr.AtomicExprDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.BehaviorDescr;
import org.drools.compiler.lang.descr.ConstraintConnectiveDescr;
import org.drools.compiler.lang.descr.EntryPointDescr;
import org.drools.compiler.lang.descr.ExistsDescr;
import org.drools.compiler.lang.descr.NotDescr;
import org.drools.compiler.lang.descr.OrDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.RelationalExprDescr;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.Behavior;
import org.drools.core.rule.Pattern;
import org.drools.core.time.TimeUtils;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.index.IndexUtil;
import org.drools.core.util.index.IndexUtil.ConstraintType;
import org.drools.drlx.DrlxParser;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.body.MethodDeclaration;
import org.drools.javaparser.ast.body.Parameter;
import org.drools.javaparser.ast.drlx.expr.PointFreeExpr;
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
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.javaparser.ast.expr.UnaryExpr;
import org.drools.javaparser.ast.expr.VariableDeclarationExpr;
import org.drools.javaparser.ast.stmt.BlockStmt;
import org.drools.javaparser.ast.stmt.ExpressionStmt;
import org.drools.javaparser.ast.stmt.ReturnStmt;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.javaparser.ast.type.PrimitiveType;
import org.drools.javaparser.ast.type.Type;
import org.drools.javaparser.ast.type.TypeParameter;
import org.drools.javaparser.ast.type.UnknownType;
import org.drools.model.BitMask;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.RuleDescrImpl;
import org.kie.internal.builder.conf.LanguageLevelOption;

import static org.drools.javaparser.printer.PrintUtil.toDrlx;
import static org.drools.modelcompiler.builder.generator.StringUtil.toId;

public class ModelGenerator {

    private static final ClassOrInterfaceType RULE_TYPE = JavaParser.parseClassOrInterfaceType( Rule.class.getCanonicalName() );
    private static final ClassOrInterfaceType BITMASK_TYPE = JavaParser.parseClassOrInterfaceType( BitMask.class.getCanonicalName() );

    public static final boolean GENERATE_EXPR_ID = true;

    public static PackageModel generateModel( InternalKnowledgePackage pkg, List<RuleDescrImpl> rules ) {
        String name = pkg.getName();
        PackageModel packageModel = new PackageModel( name );
        packageModel.addImports(pkg.getTypeResolver().getImports());
        for ( RuleDescrImpl descr : rules ) {
            RuleDescr ruleDescr = descr.getDescr();

            MethodDeclaration ruleMethod = new MethodDeclaration();
            ruleMethod.setModifiers(EnumSet.of(Modifier.PRIVATE));
            ruleMethod.setType( RULE_TYPE );
            ruleMethod.setName( "rule_" + toId( ruleDescr.getName() ) );

            BlockStmt ruleBlock = new BlockStmt();
            ruleMethod.setBody(ruleBlock);

            RuleContext context = new RuleContext( pkg, packageModel.getExprIdGenerator() );

            visit(context, ruleDescr.getLhs());

            for ( Entry<String, DeclarationSpec> decl : context.declarations.entrySet() ) {
                ClassOrInterfaceType varType = JavaParser.parseClassOrInterfaceType(Variable.class.getCanonicalName());
                Type declType = classToReferenceType( decl.getValue().declarationClass );

                varType.setTypeArguments(declType);
                VariableDeclarationExpr var_ = new VariableDeclarationExpr(varType, "var_" + decl.getKey(), Modifier.FINAL);

                MethodCallExpr declarationOfCall = new MethodCallExpr(null, "declarationOf");
                MethodCallExpr typeCall = new MethodCallExpr(null, "type");
                typeCall.addArgument( new ClassExpr( declType ));
                declarationOfCall.addArgument(typeCall);
                decl.getValue().getEntryPoint().ifPresent( ep -> {
                    MethodCallExpr entryPointCall = new MethodCallExpr(null, "entryPoint");
                    entryPointCall.addArgument( new StringLiteralExpr( ep ) );
                    declarationOfCall.addArgument( entryPointCall );
                } );
                for ( BehaviorDescr behaviorDescr : decl.getValue().getBehaviors() ) {
                    MethodCallExpr windowCall = new MethodCallExpr(null, "window");
                    if ( Behavior.BehaviorType.TIME_WINDOW.matches( behaviorDescr.getSubType() ) ) {
                        windowCall.addArgument( "Window.Type.TIME" );
                        windowCall.addArgument( "" + TimeUtils.parseTimeString( behaviorDescr.getParameters().get( 0 ) ) );
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

            VariableDeclarationExpr ruleVar = new VariableDeclarationExpr( RULE_TYPE, "rule");

            MethodCallExpr ruleCall = new MethodCallExpr(null, "rule");
            ruleCall.addArgument( new StringLiteralExpr( ruleDescr.getName() ) );

            MethodCallExpr viewCall = new MethodCallExpr(ruleCall, "view");
            context.expressions.forEach(viewCall::addArgument);

            String ruleConsequenceAsBlock = rewriteConsequenceBlock( context, ruleDescr.getConsequence().toString().trim() );
            BlockStmt ruleConsequence = JavaParser.parseBlock( "{" + ruleConsequenceAsBlock + "}" );
            List<String> declUsedInRHS = ruleConsequence.getChildNodesByType(NameExpr.class).stream().map(NameExpr::getNameAsString).collect(Collectors.toList());
            List<String> verifiedDeclUsedInRHS = context.declarations.keySet().stream().filter(declUsedInRHS::contains).collect(Collectors.toList());

            boolean rhsRewritten = rewriteRHS(context, ruleBlock, ruleConsequence);

            MethodCallExpr thenCall = new MethodCallExpr(viewCall, "then");
            MethodCallExpr onCall = null;

            if (!verifiedDeclUsedInRHS.isEmpty()) {
                onCall = new MethodCallExpr( null, "on" );
                verifiedDeclUsedInRHS.stream().map( k -> "var_" + k ).forEach( onCall::addArgument );
            }

            MethodCallExpr executeCall = new MethodCallExpr(onCall, "execute");
            LambdaExpr executeLambda = new LambdaExpr();
            executeCall.addArgument(executeLambda);
            executeLambda.setEnclosingParameters(true);
            if (rhsRewritten) {
                executeLambda.addParameter(new Parameter(new UnknownType(), "drools"));
            }
            verifiedDeclUsedInRHS.stream().map(x -> new Parameter(new UnknownType(), x)).forEach(executeLambda::addParameter);
            executeLambda.setBody( ruleConsequence );

            thenCall.addArgument( executeCall );

            AssignExpr ruleAssign = new AssignExpr(ruleVar, thenCall, AssignExpr.Operator.ASSIGN);
            ruleBlock.addStatement(ruleAssign);

            ruleBlock.addStatement( new ReturnStmt("rule") );
            System.out.println(ruleMethod);
            packageModel.putRuleMethod("rule_" + toId( ruleDescr.getName() ), ruleMethod);
        }

        packageModel.print();
        return packageModel;
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

    private static void visit( RuleContext context, BaseDescr descr ) {
        if ( descr instanceof AndDescr) {
            visit( context, ( (AndDescr) descr ));
        } else if ( descr instanceof OrDescr) {
            visit( context, ( (OrDescr) descr ));
        } else if ( descr instanceof PatternDescr && ((PatternDescr)descr).getSource() instanceof AccumulateDescr) {
            visit( context, ( (AccumulateDescr)((PatternDescr) descr).getSource() ));
        } else if ( descr instanceof PatternDescr ) {
            visit( context, ( (PatternDescr) descr ));
        } else if ( descr instanceof NotDescr) {
            visit( context, ( (NotDescr) descr ));
        } else if ( descr instanceof ExistsDescr) {
            visit( context, ( (ExistsDescr) descr ));
        } else {
            throw new UnsupportedOperationException("TODO"); // TODO
        }
    }

    private static void visit( RuleContext context, AccumulateDescr descr ) {
        final MethodCallExpr accumulateDSL = new MethodCallExpr(null, "accumulate");
        context.addExpression(accumulateDSL);
        context.pushExprPointer( accumulateDSL::addArgument );
        visit(context, descr.getInputPattern());
        for(AccumulateDescr.AccumulateFunctionCallDescr function: descr.getFunctions()) {
            visit(context, function, accumulateDSL);
        }
        context.popExprPointer();
    }

    private static void visit(RuleContext context, AccumulateDescr.AccumulateFunctionCallDescr function, MethodCallExpr accumulateDSL) {

        context.pushExprPointer(accumulateDSL::addArgument);

        final MethodCallExpr functionDSL = new MethodCallExpr(null, function.getFunction());

        final Expression expr = DrlxParser.parseExpression(function.getParams()[0]);
        if(expr instanceof MethodCallExpr) {
            final MethodCallExpr methodCallExpr = (MethodCallExpr) expr;

            final NameExpr scope = (NameExpr) methodCallExpr.getScope().orElseThrow(UnsupportedOperationException::new);
            final Class clazz =  context.declarations.get(scope.getName().asString()).declarationClass;

            LambdaExpr lambdaExpr = new LambdaExpr();
            lambdaExpr.setEnclosingParameters( true );
            lambdaExpr.addParameter( new Parameter( new TypeParameter(clazz.getName()), "$p" ) );

            lambdaExpr.setBody(new ExpressionStmt(methodCallExpr));

            functionDSL.addArgument(lambdaExpr);

            try {
                Class<?> declClass = clazz.getMethod(methodCallExpr.getName().asString()).getReturnType();
                context.declarations.put(function.getBind(), new DeclarationSpec(declClass));
            } catch ( NoSuchMethodException e ) {
                throw new UnsupportedOperationException("Aggregate function result type", e);
            }
        }

        final MethodCallExpr asDSL = new MethodCallExpr(functionDSL, "as");
        asDSL.addArgument(new NameExpr("var_"+ function.getBind()));

        accumulateDSL.addArgument(asDSL);

        context.popExprPointer();
    }

    private static Type classToReferenceType( Class<?> declClass ) {
        Type parsedType = JavaParser.parseType( declClass.getCanonicalName() );
        return parsedType instanceof PrimitiveType ?
               ((PrimitiveType)parsedType).toBoxedType() :
               parsedType.getElementType();
    }

    private static void visit( RuleContext context, NotDescr descr ) {
        final MethodCallExpr notDSL = new MethodCallExpr(null, "not");
        context.addExpression(notDSL);
        context.pushExprPointer( notDSL::addArgument );
        for (BaseDescr subDescr : descr.getDescrs()) {
            visit( context, subDescr );
        }
        context.popExprPointer();
    }

    private static void visit( RuleContext context, ExistsDescr descr ) {
        final MethodCallExpr existsDSL = new MethodCallExpr(null, "exists");
        context.addExpression(existsDSL);
        context.pushExprPointer( existsDSL::addArgument );
        for (Object subDescr : descr.getDescrs()) {
            if(subDescr instanceof BaseDescr)
                visit( context, (BaseDescr)subDescr );
        }
        context.popExprPointer();
    }

    private static void visit( RuleContext context, AndDescr descr ) {
        // if it's the first (implied) `and` wrapping the first level of patterns, skip adding it to the DSL.
        if ( context.getExprPointerLevel() != 1 ) {
            final MethodCallExpr andDSL = new MethodCallExpr(null, "and");
            context.addExpression(andDSL);
            context.pushExprPointer( andDSL::addArgument );
        }
        for (BaseDescr subDescr : descr.getDescrs()) {
            visit( context, subDescr );
        }
        if ( context.getExprPointerLevel() != 1 ) {
            context.popExprPointer();
        }
    }

    private static void visit( RuleContext context, OrDescr descr ) {
        final MethodCallExpr orDSL = new MethodCallExpr(null, "or");
        context.addExpression(orDSL);
        context.pushExprPointer( orDSL::addArgument );
        for (BaseDescr subDescr : descr.getDescrs()) {
            visit( context, subDescr );
        }
        context.popExprPointer();
    }

    private static void visit(RuleContext context, PatternDescr pattern ) {
        Class<?> patternType;
        try {
            patternType = context.pkg.getTypeResolver().resolveType( pattern.getObjectType() );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException( e );
        }

        if (pattern.getIdentifier() != null) {
            context.declarations.put( pattern.getIdentifier(), new DeclarationSpec( patternType, pattern ));
        }

        if (pattern.getConstraint().getDescrs().isEmpty()) {
            MethodCallExpr dslExpr = new MethodCallExpr(null, "input");
            if (pattern.getIdentifier() != null) {
                dslExpr.addArgument(new NameExpr("var_"+pattern.getIdentifier()));
            } else {
                MethodCallExpr declarationOfCall = new MethodCallExpr(null, "declarationOf");
                MethodCallExpr typeCall = new MethodCallExpr(null, "type");
                typeCall.addArgument( new ClassExpr( JavaParser.parseClassOrInterfaceType(patternType.getCanonicalName()) ));
                declarationOfCall.addArgument(typeCall);
                dslExpr.addArgument( declarationOfCall );
            }
            System.out.println("Adding newExpression: "+dslExpr);
            context.addExpression( dslExpr );
        } else {
            for (BaseDescr constraint : pattern.getConstraint().getDescrs()) {
                String expression = constraint.toString();
                Expression dslExpr = drlxParse(context, patternType, pattern.getIdentifier(), expression);

                System.out.println("Adding newExpression: "+dslExpr);
                context.addExpression( dslExpr );
            }
        }
    }

    private static Expression drlxParse(RuleContext context, Class<?> patternType, String bindingId, String expression) {
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
            TypedExpression left = DrlxParseUtil.toTypedExpression( context, patternType, binaryExpr.getLeft(), usedDeclarations, reactOnProperties );
            TypedExpression right = DrlxParseUtil.toTypedExpression( context, patternType, binaryExpr.getRight(), usedDeclarations, reactOnProperties );

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

            return buildDslExpression( patternType, exprId, bindingId, decodeConstraintType, usedDeclarations, reactOnProperties, left, right, combo, false );
        }

        if ( drlxExpr instanceof PointFreeExpr ) {
            PointFreeExpr pointFreeExpr = (PointFreeExpr) drlxExpr;

            Set<String> usedDeclarations = new HashSet<>();
            Set<String> reactOnProperties = new HashSet<>();
            DrlxParseUtil.toTypedExpression( context, patternType, pointFreeExpr.getLeft(), usedDeclarations, reactOnProperties );
            DrlxParseUtil.toTypedExpression( context, patternType, pointFreeExpr.getRight(), usedDeclarations, reactOnProperties );

            MethodCallExpr methodCallExpr = new MethodCallExpr( null, pointFreeExpr.getOperator().asString() );
            if (pointFreeExpr.getArg1() != null) {
                addArgumentToMethodCall( pointFreeExpr.getArg1(), methodCallExpr );
                if (pointFreeExpr.getArg2() != null) {
                    addArgumentToMethodCall( pointFreeExpr.getArg2(), methodCallExpr );
                }
            }

            return buildDslExpression( patternType, exprId, bindingId, null, usedDeclarations, reactOnProperties, null, null, methodCallExpr, true );
        }

        if (drlxExpr instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = (MethodCallExpr)drlxExpr;

            NameExpr _this = new NameExpr("_this");

            MethodCallExpr withThis = DrlxParseUtil.preprendNameExprToMethodCallExpr(_this, methodCallExpr);

            return buildDslExpression(patternType, exprId, bindingId, null, new HashSet<>(), new HashSet<>(), null, null, withThis, false);
        }

        throw new UnsupportedOperationException("Unknown expression: " + toDrlx(drlxExpr)); // TODO

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

    private static Expression mvelParse(RuleContext context, Pattern pattern, String bindingId, String expression) {
        DrlExprParser drlExprParser = new DrlExprParser( LanguageLevelOption.DRL6_STRICT );
        ConstraintConnectiveDescr result = drlExprParser.parse( expression );
        if ( result.getDescrs().size() != 1 ) {
            throw new UnsupportedOperationException("TODO"); // TODO
        }

        BaseDescr singletonDescr = result.getDescrs().get(0);
        if ( !(singletonDescr instanceof RelationalExprDescr) ) {
            throw new UnsupportedOperationException("TODO"); // TODO
        }

        System.out.println(singletonDescr);
        RelationalExprDescr relationalExprDescr = (RelationalExprDescr) singletonDescr;
        IndexUtil.ConstraintType decodeConstraintType = IndexUtil.ConstraintType.decode( relationalExprDescr.getOperator() );
        // to be visited
        // TODO what if not atomicExprDescr ?
        Set<String> usedDeclarations = new HashSet<>();
        Set<String> reactOnProperties = new HashSet<>();
        TypedExpression left = MvelParseUtil.toTypedExpression( context, pattern, (AtomicExprDescr) relationalExprDescr.getLeft(), usedDeclarations, reactOnProperties );
        TypedExpression right = MvelParseUtil.toTypedExpression( context, pattern, (AtomicExprDescr) relationalExprDescr.getRight(), usedDeclarations, reactOnProperties );
        String combo;
        switch ( relationalExprDescr.getOperator() ) {
            case "==":
                combo = left.getExpressionAsString() + ".equals(" + right.getExpressionAsString() + ")";
                break;
            case "!=":
                combo = "!" + left.getExpressionAsString() + ".equals(" + right.getExpressionAsString() + ")";
                break;
            default:
                combo = left.getExpressionAsString() + " " + relationalExprDescr.getOperator() + " " + right.getExpressionAsString();
        }

        return buildDslExpression( pattern.getObjectType().getValueType().getClassType(), null, bindingId, decodeConstraintType, usedDeclarations, reactOnProperties, left, right, new NameExpr( combo ), false );
    }

    private static Expression buildDslExpression( Class<?> patternType, String exprId, String bindingId, ConstraintType decodeConstraintType,
                                                  Set<String> usedDeclarations, Set<String> reactOnProperties,
                                                  TypedExpression left, TypedExpression right, Expression expr, boolean isStatic ) {

        MethodCallExpr exprDSL = new MethodCallExpr(null, "expr");
        if (exprId != null && !"".equals(exprId)) {
            exprDSL.addArgument( new StringLiteralExpr(exprId) );
        }
        if (bindingId != null) {
            exprDSL.addArgument( new NameExpr("var_" + bindingId) );
        } else {
            MethodCallExpr declarationOfCall = new MethodCallExpr(null, "declarationOf");
            MethodCallExpr typeCall = new MethodCallExpr(null, "type");
            typeCall.addArgument( new ClassExpr( JavaParser.parseClassOrInterfaceType(patternType.getCanonicalName()) ));
            declarationOfCall.addArgument(typeCall);
            exprDSL.addArgument( declarationOfCall );
        }
        usedDeclarations.stream().map( x -> new NameExpr( "var_" + x )).forEach(exprDSL::addArgument);

        Expression exprArg = expr;
        if (!isStatic) {
            LambdaExpr lambdaExpr = new LambdaExpr();
            lambdaExpr.setEnclosingParameters( true );
            lambdaExpr.addParameter( new Parameter( new UnknownType(), "_this" ) );
            usedDeclarations.stream().map( s -> new Parameter( new UnknownType(), s ) ).forEach( lambdaExpr::addParameter );
            lambdaExpr.setBody( new ExpressionStmt( expr ) );
            exprArg = lambdaExpr;
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
            indexedBy_leftOperandExtractor.setBody( new ExpressionStmt( left.getExpression() ) );

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
                indexedBy_rightOperandExtractor.setBody( new ExpressionStmt( right.getExpression() ) );
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

    public static class RuleContext {
        private final InternalKnowledgePackage pkg;
        private DRLExprIdGenerator exprIdGenerator;

        Map<String, DeclarationSpec> declarations = new HashMap<>();
        Deque<Consumer<Expression>> exprPointer = new LinkedList<>();
        List<Expression> expressions = new ArrayList<>();

        public RuleContext(InternalKnowledgePackage pkg, DRLExprIdGenerator exprIdGenerator) {
            this.pkg = pkg;
            this.exprIdGenerator = exprIdGenerator;
            exprPointer.push( this.expressions::add );
        }

        public void addExpression(Expression e) {
            exprPointer.peek().accept(e);
        }
        public void pushExprPointer(Consumer<Expression> p) {
            exprPointer.push(p);
        }
        public Consumer<Expression> popExprPointer() {
            return exprPointer.pop();
        }
        public int getExprPointerLevel() {
            return exprPointer.size();
        }

        public InternalKnowledgePackage getPkg() {
            return pkg;
        }

        public String getExprId(Class<?> patternType, String drlConstraint) {
            return exprIdGenerator.getExprId(patternType, drlConstraint);
        }
    }

    public static class DeclarationSpec {
        final Class<?> declarationClass;
        final Optional<PatternDescr> optPattern;

        public DeclarationSpec( Class<?> declarationClass, PatternDescr pattern ) {
            this.declarationClass = declarationClass;
            this.optPattern = Optional.of(pattern);
        }

        public DeclarationSpec( Class<?> declarationClass ) {
            this.declarationClass = declarationClass;
            this.optPattern = Optional.empty();
        }

        Optional<String> getEntryPoint() {
            return optPattern.flatMap(pattern -> pattern.getSource() instanceof EntryPointDescr ?
                                                    Optional.of(((EntryPointDescr) pattern.getSource()).getEntryId()) :
                                                    Optional.empty()
            );
        }

        public List<BehaviorDescr> getBehaviors() {
            return optPattern.map(PatternDescr::getBehaviors).orElse(Collections.emptyList());

        }
    }
}