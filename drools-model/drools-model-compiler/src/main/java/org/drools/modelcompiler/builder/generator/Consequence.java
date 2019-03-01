package org.drools.modelcompiler.builder.generator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.compiler.RuleBuildError;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.constraint.parser.printer.PrintUtil;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.util.StringUtils;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import org.drools.constraint.parser.ast.expr.CommaSeparatedMethodCallExpr;
import org.drools.constraint.parser.ast.expr.DrlxExpression;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.UnknownType;
import org.drools.model.BitMask;
import org.drools.model.Variable;
import org.drools.model.bitmask.AllSetButLastBitMask;
import org.drools.model.consequences.ConsequenceImpl;
import org.drools.model.functions.ScriptBlock;
import org.drools.model.impl.DeclarationImpl;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.consequence.DroolsImpl;
import org.drools.modelcompiler.consequence.MVELConsequence;

import static java.util.stream.Collectors.toSet;

import static org.drools.core.util.ClassUtils.getter2property;
import static org.drools.core.util.ClassUtils.setter2property;
import static com.github.javaparser.JavaParser.parseExpression;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.findAllChildrenRecursive;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.hasScope;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.isNameExprWithName;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.parseBlock;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BREAKING_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.EXECUTESCRIPT_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.EXECUTE_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ON_CALL;

public class Consequence {

    public static final Set<String> knowledgeHelperMethods = new HashSet<>();
    public static final Set<String> implicitDroolsMethods = new HashSet<>();

    private static final Expression asKnoledgeHelperExpression = parseExpression("((" + DroolsImpl.class.getCanonicalName() + ") drools).asKnowledgeHelper()");

    static {
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
        knowledgeHelperMethods.add("run");
        knowledgeHelperMethods.add("guard");
    }

    private final RuleContext context;
    private final PackageModel packageModel;

    public Consequence(RuleContext context) {
        this.context = context;
        this.packageModel = context.getPackageModel();
    }

    public MethodCallExpr createCall(RuleDescr ruleDescr, String consequenceString, BlockStmt ruleVariablesBlock, boolean isBreaking) {
        BlockStmt ruleConsequence = null;

        if (context.getRuleDialect() == RuleContext.RuleDialect.JAVA) {
            // mvel consequences will be treated as a ScriptBlock
            ruleConsequence = rewriteConsequence( consequenceString );
            if ( ruleConsequence != null ) {
                ruleConsequence.findAll( Expression.class )
                        .stream()
                        .filter( s -> isNameExprWithName( s, "kcontext" ) )
                        .forEach( n -> n.replace( new CastExpr( toClassOrInterfaceType( org.kie.api.runtime.rule.RuleContext.class ), new NameExpr( "drools" ) ) ) );
            } else {
                return null;
            }
        }

        Set<String> usedDeclarationInRHS = extractUsedDeclarations(ruleConsequence, consequenceString);

        Set<String> usedUnusableDeclarations = new HashSet<>(context.getUnusableOrBinding());
        usedUnusableDeclarations.retainAll(usedDeclarationInRHS);

        for(String s : usedUnusableDeclarations) {
            context.addCompilationError( new InvalidExpressionErrorResult(String.format("%s cannot be resolved to a variable", s)) );
        }

        MethodCallExpr onCall = onCall(usedDeclarationInRHS);
        if (isBreaking) {
            onCall = new MethodCallExpr(onCall, BREAKING_CALL);
        }
        MethodCallExpr executeCall = null;
        if (context.getRuleDialect() == RuleContext.RuleDialect.JAVA) {
            executeCall = executeCall(ruleVariablesBlock, ruleConsequence, usedDeclarationInRHS, onCall);
        } else if (context.getRuleDialect() == RuleContext.RuleDialect.MVEL) {
            executeCall = executeScriptCall(ruleDescr, onCall);
        }

        if(context.getRuleDialect() == RuleContext.RuleDialect.MVEL) {
            validateMvelConsequence(ruleDescr, consequenceString);
        }

        return executeCall;
    }

    private String addImports(String consequenceString) {
        for (String i : packageModel.getImports()) {
            if (i.equals(packageModel.getName() + ".*")) {
                continue; // skip same-package star import.
            }
            consequenceString = String.format("import %s; %s", i, consequenceString);
        }
        return consequenceString;
    }


    private void validateMvelConsequence(RuleDescr ruleDescr, String consequenceWithoutImports) {
        final String consequenceString = addImports(consequenceWithoutImports);
        ConsequenceValidation consequenceValidation = new ConsequenceValidation(context.getPackageModel().getName(), consequenceString, ruleDescr);
        for (DeclarationSpec d : context.getAllDeclarations()) {
            consequenceValidation.addVariable(d);
        }
        packageModel.addConsequenceValidation(consequenceValidation);
    }

    private BlockStmt rewriteConsequence(String consequence) {
        String ruleConsequenceAsBlock = rewriteConsequenceBlock(consequence.trim());
        try {
            return parseBlock( ruleConsequenceAsBlock );
        } catch (ParseProblemException e) {
            context.addCompilationError( new InvalidExpressionErrorResult( "Unable to parse consequence caused by: " + e.getMessage() ) );
        }
        return null;
    }

    private Set<String> extractUsedDeclarations(BlockStmt ruleConsequence, String consequenceString) {
        Set<String> existingDecls = new HashSet<>();
        existingDecls.addAll(context.getAvailableBindings());
        existingDecls.addAll(packageModel.getGlobals().keySet());
        if (context.getRuleUnitDescr() != null) {
            existingDecls.addAll(context.getRuleUnitDescr().getUnitVars());
        }

        if (context.getRuleDialect() == RuleContext.RuleDialect.MVEL) {
            return existingDecls.stream().filter(d -> containsWord(d, consequenceString)).collect(toSet());
        }

        Set<String> declUsedInRHS = ruleConsequence.findAll(NameExpr.class).stream().map(NameExpr::getNameAsString).collect(toSet());
        return existingDecls.stream().filter(declUsedInRHS::contains).collect(toSet());
    }

    public static boolean containsWord(String word, String body) {
        // $ is quite a common character for a drools binding but it's not considered a word for the regexp engine
        // By converting to a character is easier to write the regexp
        final String wordWithDollarReplaced = word.replace("$", "犬");
        final String bodyWithDollarReplaced = body.replace("$", "犬");

        Pattern p = Pattern.compile("\\b" + wordWithDollarReplaced + "\\b");
        Matcher m = p.matcher(bodyWithDollarReplaced);
        return m.find();
    }

    private MethodCallExpr executeCall(BlockStmt ruleVariablesBlock, BlockStmt ruleConsequence, Collection<String> verifiedDeclUsedInRHS, MethodCallExpr onCall) {
        boolean requireDrools = rewriteRHS(ruleVariablesBlock, ruleConsequence);
        MethodCallExpr executeCall = new MethodCallExpr(onCall, onCall == null ? "D." + EXECUTE_CALL : EXECUTE_CALL);
        LambdaExpr executeLambda = new LambdaExpr();
        executeCall.addArgument(executeLambda);
        executeLambda.setEnclosingParameters(true);
        if (requireDrools) {
            executeLambda.addParameter(new Parameter(new UnknownType(), "drools"));
        }
        verifiedDeclUsedInRHS.stream().map(x -> new Parameter(new UnknownType(), x)).forEach(executeLambda::addParameter);
        executeLambda.setBody(ruleConsequence);
        return executeCall;
    }

    private MethodCallExpr executeScriptCall(RuleDescr ruleDescr, MethodCallExpr onCall) {
        MethodCallExpr executeCall = new MethodCallExpr(onCall, onCall == null ? "D." + EXECUTESCRIPT_CALL : EXECUTESCRIPT_CALL);
        executeCall.addArgument(new StringLiteralExpr("mvel"));
        executeCall.addArgument(packageModel.getName() + "." + packageModel.getRulesFileName() + ".class");

        ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
        objectCreationExpr.setType(StringBuilder.class.getCanonicalName());
        Expression mvelSB = objectCreationExpr;

        for (String i : packageModel.getImports()) {
            if (i.equals(packageModel.getName() + ".*")) {
                continue; // skip same-package star import.
            }
            mvelSB = appendImport( mvelSB, i );
        }

        StringLiteralExpr mvelScriptBodyStringLiteral = new StringLiteralExpr();
        mvelScriptBodyStringLiteral.setString(ruleDescr.getConsequence().toString()); // use the setter method in order for the string literal be properly escaped.

        MethodCallExpr appendCall = new MethodCallExpr(mvelSB, "append");
        appendCall.addArgument(mvelScriptBodyStringLiteral);

        executeCall.addArgument(new MethodCallExpr(appendCall, "toString"));
        return executeCall;
    }

    private MethodCallExpr appendImport( Expression mvelSB, String i ) {
        MethodCallExpr appendCall = new MethodCallExpr(mvelSB, "append");
        StringLiteralExpr importAsStringLiteral = new StringLiteralExpr();
        importAsStringLiteral.setString("import " + i + ";\n"); // use the setter method in order for the string literal be properly escaped.
        appendCall.addArgument(importAsStringLiteral);
        return appendCall;
    }

    private MethodCallExpr onCall(Collection<String> usedArguments) {
        MethodCallExpr onCall = null;

        if (!usedArguments.isEmpty()) {
            onCall = new MethodCallExpr(null, ON_CALL);
            usedArguments.stream().map(context::getVar).forEach(onCall::addArgument);
        }
        return onCall;
    }

    private String rewriteConsequenceBlock(String consequence) {
        int modifyPos = StringUtils.indexOfOutOfQuotes(consequence, "modify");
        if (modifyPos < 0) {
            return consequence;
        }

        int lastCopiedEnd = 0;
        StringBuilder sb = new StringBuilder();
        sb.append(consequence.substring(lastCopiedEnd, modifyPos));
        lastCopiedEnd = modifyPos + 1;

        for (; modifyPos >= 0; modifyPos = StringUtils.indexOfOutOfQuotes(consequence, "modify", modifyPos+6)) {
            int declStart = consequence.indexOf('(', modifyPos + 6);
            int declEnd = declStart > 0 && consequence.indexOf(')', declStart + 1) >= 0 ? StringUtils.findEndOfMethodArgsIndex(consequence, declStart) : -1;
            if (declEnd < 0) {
                continue;
            }
            String decl = consequence.substring(declStart + 1, declEnd).trim();
            int blockStart = consequence.indexOf('{', declEnd + 1);
            int blockEnd = consequence.indexOf('}', blockStart + 1);
            if (blockEnd < 0) {
                continue;
            }

            if (lastCopiedEnd < modifyPos) {
                sb.append(consequence.substring(lastCopiedEnd, modifyPos));
            }

            Expression declAsExpr = JavaParser.parseExpression( decl );
            if (decl.indexOf( '(' ) >= 0) {
                declAsExpr = new EnclosedExpr( declAsExpr );
            }
            String originalBlock = consequence.substring(blockStart + 1, blockEnd).trim();
            if(!"".equals(originalBlock)) {
                DrlxExpression modifyBlock = DrlxParseUtil.parseExpression(originalBlock);
                Expression expr = modifyBlock.getExpr();
                List<Expression> originalMethodCalls;
                if (expr instanceof CommaSeparatedMethodCallExpr) {
                    originalMethodCalls = ((CommaSeparatedMethodCallExpr) expr).getExpressions();
                } else {
                    originalMethodCalls = Collections.singletonList(expr);
                }
                for (Expression e : originalMethodCalls) {
                    MethodCallExpr mc = (MethodCallExpr) e;
                    Expression mcWithScope = org.drools.modelcompiler.builder.generator.DrlxParseUtil.prepend(declAsExpr, mc);
                    modifyBlock.replace(mc, mcWithScope);
                    sb.append(PrintUtil.printConstraint(mc));
                    sb.append(";\n");
                }
            }

            sb.append("update(").append(decl).append(");\n");
            lastCopiedEnd = blockEnd + 1;
            modifyPos = lastCopiedEnd - 6;
        }

        if (lastCopiedEnd < consequence.length()) {
            sb.append(consequence.substring(lastCopiedEnd));
        }

        return sb.toString();
    }

    private boolean rewriteRHS(BlockStmt ruleBlock, BlockStmt rhs) {
        AtomicBoolean requireDrools = new AtomicBoolean(false);
        List<MethodCallExpr> methodCallExprs = rhs.findAll(MethodCallExpr.class);
        List<MethodCallExpr> updateExprs = new ArrayList<>();

        Map<String, String> newDeclarations = new HashMap<>();
        for (VariableDeclarator variableDeclarator : rhs.findAll(VariableDeclarator.class)) {
            variableDeclarator.getInitializer().ifPresent( init -> newDeclarations.put( variableDeclarator.getNameAsString(), init.toString() ) );
        }

        for (MethodCallExpr methodCallExpr : methodCallExprs) {
            if (isDroolsMethod(methodCallExpr)) {
                if (!methodCallExpr.getScope().isPresent()) {
                    methodCallExpr.setScope(new NameExpr("drools"));
                }
                if (knowledgeHelperMethods.contains(methodCallExpr.getNameAsString())) {
                    methodCallExpr.setScope(asKnoledgeHelperExpression);
                } else if (methodCallExpr.getNameAsString().equals("update")) {
                    if (methodCallExpr.toString().contains( "FactHandle" )) {
                        methodCallExpr.setScope( new NameExpr( "((org.drools.modelcompiler.consequence.DroolsImpl) drools)" ) );
                    }
                    updateExprs.add(methodCallExpr);
                } else if (methodCallExpr.getNameAsString().equals("retract")) {
                    methodCallExpr.setName(new SimpleName("delete"));
                }
                requireDrools.set(true);
            }
        }

        Set<String> initializedBitmaskFields = new HashSet<>();
        for (MethodCallExpr updateExpr : updateExprs) {
            Expression argExpr = updateExpr.getArgument(0);
            if (argExpr instanceof NameExpr) {
                String updatedVar = ((NameExpr) argExpr).getNameAsString();
                Set<String> modifiedProps = findModifiedProperties( methodCallExprs, updateExpr, updatedVar );

                if (!initializedBitmaskFields.contains(updatedVar)) {
                    MethodCallExpr bitMaskCreation = createBitMaskInitialization(newDeclarations, updatedVar, modifiedProps);
                    ruleBlock.addStatement(createBitMaskField(updatedVar, bitMaskCreation));
                }

                updateExpr.addArgument("mask_" + updatedVar);
                initializedBitmaskFields.add(updatedVar);
            }
        }

        return requireDrools.get();
    }

    private MethodCallExpr createBitMaskInitialization(Map<String, String> newDeclarations, String updatedVar, Set<String> modifiedProps) {
        MethodCallExpr bitMaskCreation;
        if (modifiedProps != null) {
            String declarationVar = newDeclarations.containsKey(updatedVar) ? newDeclarations.get(updatedVar) : updatedVar;
            Class<?> updatedClass = context.getDeclarationById(declarationVar).map(DeclarationSpec::getDeclarationClass).orElseThrow(RuntimeException::new);

            bitMaskCreation = new MethodCallExpr(new NameExpr(BitMask.class.getCanonicalName()), "getPatternMask");
            bitMaskCreation.addArgument(new ClassExpr(toClassOrInterfaceType(updatedClass)));
            modifiedProps.forEach(s -> bitMaskCreation.addArgument(new StringLiteralExpr(s)));
        } else {
            bitMaskCreation = new MethodCallExpr(new NameExpr(AllSetButLastBitMask.class.getCanonicalName()), "get");
        }
        return bitMaskCreation;
    }

    private AssignExpr createBitMaskField(String updatedVar, MethodCallExpr bitMaskCreation) {
        VariableDeclarationExpr bitMaskVar = new VariableDeclarationExpr(toClassOrInterfaceType(BitMask.class), "mask_" + updatedVar, Modifier.finalModifier());
        return new AssignExpr(bitMaskVar, bitMaskCreation, AssignExpr.Operator.ASSIGN);
    }

    private Set<String> findModifiedProperties( List<MethodCallExpr> methodCallExprs, MethodCallExpr updateExpr, String updatedVar ) {
        Set<String> modifiedProps = new HashSet<>();
        for (MethodCallExpr methodCall : methodCallExprs.subList(0, methodCallExprs.indexOf(updateExpr))) {
            if (methodCall.getScope().isPresent() && hasScope(methodCall, updatedVar)) {
                String propName = methodToProperty(methodCall);
                if (propName != null) {
                    modifiedProps.add(propName);
                } else {
                    // if we were unable to detect the property the mask has to be all set, so avoid the rest of the cycle
                    return null;
                }
            }
        }
        return modifiedProps;
    }

    private String methodToProperty(MethodCallExpr mce) {
        String propertyName = setter2property(mce.getNameAsString());

        if (propertyName == null && mce.getArguments().isEmpty()) {
            propertyName = getter2property(mce.getNameAsString());
        }

        // TODO also register additional property in case the invoked method is annotated with @Modifies

        return propertyName;
    }

    private static boolean isDroolsMethod(MethodCallExpr mce) {
        final boolean hasDroolsScope = hasScope(mce, "drools");
        final boolean isImplicitDroolsMethod = !mce.getScope().isPresent() && implicitDroolsMethods.contains(mce.getNameAsString());
        final boolean hasDroolsAsParameter = findAllChildrenRecursive(mce).stream().anyMatch(a -> isNameExprWithName(a, "drools"));
        return hasDroolsScope || isImplicitDroolsMethod || hasDroolsAsParameter;
    }
}
