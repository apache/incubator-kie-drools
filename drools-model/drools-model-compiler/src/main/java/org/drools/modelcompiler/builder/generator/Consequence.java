package org.drools.modelcompiler.builder.generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.UnknownType;
import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.util.StringUtils;
import org.drools.model.BitMask;
import org.drools.model.bitmask.AllSetButLastBitMask;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.CompilationProblemErrorResult;
import org.drools.modelcompiler.builder.errors.InvalidExpressionErrorResult;
import org.drools.modelcompiler.builder.errors.MvelCompilationError;
import org.drools.modelcompiler.consequence.DroolsImpl;
import org.drools.mvelcompiler.ModifyCompiler;
import org.drools.mvelcompiler.MvelCompiler;
import org.drools.mvelcompiler.MvelCompilerException;
import org.drools.mvelcompiler.ParsingResult;
import org.drools.mvelcompiler.context.MvelCompilerContext;

import static java.util.stream.Collectors.toSet;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.core.util.ClassUtils.getter2property;
import static org.drools.core.util.ClassUtils.setter2property;
import static org.drools.modelcompiler.builder.PackageModel.DOMAIN_CLASSESS_METADATA_FILE_NAME;
import static org.drools.modelcompiler.builder.PackageModel.DOMAIN_CLASS_METADATA_INSTANCE;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.addCurlyBracesToBlock;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.findAllChildrenRecursive;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getClassFromType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.isNameExprWithName;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.parseBlock;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BREAKING_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.EXECUTE_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.ON_CALL;
import static org.drools.modelcompiler.util.ClassUtil.asJavaSourceName;
import static org.drools.mvel.parser.printer.PrintUtil.printConstraint;

public class Consequence {

    public static final Set<String> knowledgeHelperMethods = new HashSet<>();
    public static final Set<String> implicitDroolsMethods = new HashSet<>();

    private Expression createAsKnowledgeHelperExpression() {
        return parseExpression(String.format("((%s) drools).asKnowledgeHelper()", DroolsImpl.class.getCanonicalName()));
    }

    static {
        implicitDroolsMethods.add("insert");
        implicitDroolsMethods.add("insertLogical");
        implicitDroolsMethods.add("delete");
        implicitDroolsMethods.add("retract");
        implicitDroolsMethods.add("update");

        knowledgeHelperMethods.add("getWorkingMemory");
        knowledgeHelperMethods.add("getRule");
        knowledgeHelperMethods.add("getMatch");
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
            executeCall = executeCall(ruleVariablesBlock, ruleConsequence, usedDeclarationInRHS, onCall, Collections.emptySet());
        } else if (context.getRuleDialect() == RuleContext.RuleDialect.MVEL) {
            executeCall = createExecuteCallMvel(ruleDescr, ruleVariablesBlock, usedDeclarationInRHS, onCall);
        }

        return executeCall;
    }

    private MethodCallExpr createExecuteCallMvel(RuleDescr ruleDescr, BlockStmt ruleVariablesBlock, Set<String> usedDeclarationInRHS, MethodCallExpr onCall) {
        String mvelBlock = addCurlyBracesToBlock(ruleDescr.getConsequence().toString());
        MvelCompilerContext mvelCompilerContext = new MvelCompilerContext(context.getTypeResolver());

        for(DeclarationSpec d : context.getAllDeclarations()) {
            Class<?> clazz = getClassFromType(context.getTypeResolver(), d.getRawType());
            mvelCompilerContext.addDeclaration(d.getBindingId(), clazz);
        }

        ParsingResult compile;
        try {
            compile = new MvelCompiler(mvelCompilerContext).compile(mvelBlock);
        } catch (MvelCompilerException e) {
            context.addCompilationError(new CompilationProblemErrorResult(new MvelCompilationError(e)) );
            return null;
        }

        return executeCall(ruleVariablesBlock,
                                  compile.statementResults(),
                                  usedDeclarationInRHS,
                                  onCall,
                                  compile.getUsedBindings());
    }
    private BlockStmt rewriteConsequence(String consequence) {
        String ruleConsequenceAsBlock = rewriteModifyBlock(consequence.trim());
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
        } else if (context.getRuleDialect() == RuleContext.RuleDialect.JAVA) {
            Set<String> declUsedInRHS = ruleConsequence.findAll(NameExpr.class).stream().map(NameExpr::getNameAsString).collect(toSet());
            return existingDecls.stream().filter(declUsedInRHS::contains).collect(toSet());
        } else {
            throw new IllegalArgumentException("Unknown rule dialect " + context.getRuleDialect() + "!");
        }
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

    private MethodCallExpr executeCall(BlockStmt ruleVariablesBlock, BlockStmt ruleConsequence, Collection<String> verifiedDeclUsedInRHS, MethodCallExpr onCall, Set<String> modifyProperties) {

        for (String modifiedProperty : modifyProperties) {
            NodeList<Expression> arguments = nodeList(new NameExpr(modifiedProperty));
            MethodCallExpr update = new MethodCallExpr(new NameExpr("drools"), "update",
                                                       arguments);
            ruleConsequence.getStatements().add(new ExpressionStmt(update));
        }


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

    private MethodCallExpr onCall(Collection<String> usedArguments) {
        MethodCallExpr onCall = null;

        if (!usedArguments.isEmpty()) {
            onCall = new MethodCallExpr(null, ON_CALL);
            usedArguments.stream().map(context::getVar).forEach(onCall::addArgument);
        }
        return onCall;
    }

    private String rewriteModifyBlock(String consequence) {
        int modifyPos = StringUtils.indexOfOutOfQuotes(consequence, "modify");
        if (modifyPos < 0) {
            return consequence;
        }

        ModifyCompiler modifyCompiler = new ModifyCompiler();
        ParsingResult compile = modifyCompiler.compile(addCurlyBracesToBlock(consequence));

        return printConstraint(compile.statementResults());
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
                    methodCallExpr.setScope(createAsKnowledgeHelperExpression());
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
            Expression argExpr = updateExpr.getArgument( 0 );
            if ( argExpr instanceof NameExpr ) {

                String updatedVar = (( NameExpr ) argExpr).getNameAsString();
                Class<?> updatedClass = findUpdatedClass( newDeclarations, updatedVar );

                if (context.isPropertyReactive(updatedClass)) {

                    if ( !initializedBitmaskFields.contains( updatedVar ) ) {
                        Set<String> modifiedProps = findModifiedProperties( methodCallExprs, updateExpr, updatedVar );
                        MethodCallExpr bitMaskCreation = createBitMaskInitialization( newDeclarations, updatedClass, modifiedProps );
                        ruleBlock.addStatement( createBitMaskField( updatedVar, bitMaskCreation ) );
                    }

                    updateExpr.addArgument( "mask_" + updatedVar );
                    initializedBitmaskFields.add( updatedVar );
                }
            }
        }

        return requireDrools.get();
    }

    private Class<?> findUpdatedClass( Map<String, String> newDeclarations, String updatedVar ) {
        String declarationVar = newDeclarations.getOrDefault(updatedVar, updatedVar);
        return context.getDeclarationById(declarationVar).map( DeclarationSpec::getDeclarationClass).orElseThrow(RuntimeException::new);
    }

    private MethodCallExpr createBitMaskInitialization(Map<String, String> newDeclarations, Class<?> updatedClass, Set<String> modifiedProps) {
        MethodCallExpr bitMaskCreation;
        if (modifiedProps != null && !modifiedProps.isEmpty()) {
            String domainClassSourceName = asJavaSourceName( updatedClass );
            bitMaskCreation = new MethodCallExpr(new NameExpr(BitMask.class.getCanonicalName()), "getPatternMask");
            bitMaskCreation.addArgument( DOMAIN_CLASSESS_METADATA_FILE_NAME + packageModel.getPackageUUID() + "." + domainClassSourceName + DOMAIN_CLASS_METADATA_INSTANCE );
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
            DrlxParseUtil.RemoveRootNodeResult removeRootNodeViaScope = DrlxParseUtil.findRemoveRootNodeViaScope(methodCall);
            Optional<Expression> root = removeRootNodeViaScope.getRootNode()
                    .filter(s -> isNameExprWithName(s, updatedVar));
            if (methodCall.getScope().isPresent() && root.isPresent()) {
                String propName = methodToProperty(methodCall, removeRootNodeViaScope.getFirstChild());
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

    private String methodToProperty(MethodCallExpr mce, Expression getter) {
        String propertyName = setter2property(mce.getNameAsString());

        if (propertyName == null && getter.isMethodCallExpr()) {
            propertyName = getter2property(getter.asMethodCallExpr().getNameAsString());
        }

        // TODO also register additional property in case the invoked method is annotated with @Modifies

        return propertyName;
    }

    private static boolean isDroolsMethod(MethodCallExpr mce) {
        final boolean hasDroolsScope = DrlxParseUtil.findRootNodeViaScope(mce)
                .filter(s -> isNameExprWithName(s, "drools"))
                .isPresent();
        final boolean isImplicitDroolsMethod = !mce.getScope().isPresent() && implicitDroolsMethods.contains(mce.getNameAsString());
        final boolean hasDroolsAsParameter = findAllChildrenRecursive(mce).stream().anyMatch(a -> isNameExprWithName(a, "drools"));
        return hasDroolsScope || isImplicitDroolsMethod || hasDroolsAsParameter;
    }
}
