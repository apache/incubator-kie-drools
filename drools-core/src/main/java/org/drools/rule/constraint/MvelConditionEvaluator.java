package org.drools.rule.constraint;

import org.drools.base.mvel.MVELCompilationUnit;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.*;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.ast.*;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExecutableAccessor;
import org.mvel2.compiler.ExecutableLiteral;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.util.ASTLinkedList;

import java.util.Map;

import static org.drools.rule.constraint.EvaluatorHelper.valuesAsMap;

public class MvelConditionEvaluator implements ConditionEvaluator, MapConditionEvaluator {

    protected final Declaration[] declarations;
    private final ParserConfiguration parserConfiguration;
    protected ExecutableStatement executableStatement;
    protected MVELCompilationUnit compilationUnit;

    private boolean evaluated = false;

    MvelConditionEvaluator(ParserConfiguration configuration, String expression, Declaration[] declarations) {
        this.declarations = declarations;
        this.parserConfiguration = configuration;
        executableStatement = (ExecutableStatement)MVEL.compileExpression(expression, new ParserContext(parserConfiguration));
    }

    public MvelConditionEvaluator(MVELCompilationUnit compilationUnit, ParserConfiguration parserConfiguration, ExecutableStatement executableStatement, Declaration[] declarations) {
        this.declarations = declarations;
        this.compilationUnit = compilationUnit;
        this.parserConfiguration = parserConfiguration;
        this.executableStatement = executableStatement;
    }

    public boolean evaluate(Object object, Map<String, Object> vars) {
        return evaluate(executableStatement, object, vars);
    }

    public boolean evaluate(Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        return evaluate(executableStatement, object, workingMemory, leftTuple);
    }

    public boolean evaluate(ExecutableStatement statement, Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        if (compilationUnit == null) {
            Map<String, Object> vars = valuesAsMap(object, workingMemory, leftTuple, declarations);
            return evaluate(statement, object, vars);
        }

        VariableResolverFactory factory = compilationUnit.createFactory();
        compilationUnit.updateFactory( null, null, object,
                                       leftTuple, null, workingMemory,
                                       workingMemory.getGlobalResolver(),
                                       factory );

        org.drools.rule.Package pkg = workingMemory.getRuleBase().getPackage( "MAIN" );
        if ( pkg != null ) {
            MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData("mvel");
            factory.setNextFactory( data.getFunctionFactory() );
        }

        return (Boolean) MVEL.executeExpression( statement, object, factory );
    }

    private boolean evaluate(ExecutableStatement statement, Object object, Map<String, Object> vars) {
        return vars == null ? (Boolean)MVEL.executeExpression(statement, object) : (Boolean)MVEL.executeExpression(statement, object, vars);
    }

    ConditionAnalyzer.Condition getAnalyzedCondition() {
        return new ConditionAnalyzer(executableStatement, declarations).analyzeCondition();
    }

    ConditionAnalyzer.Condition getAnalyzedCondition(Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        ensureCompleteEvaluation(object, workingMemory, leftTuple);
        return new ConditionAnalyzer(executableStatement, declarations).analyzeCondition();
    }

    private void ensureCompleteEvaluation(Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        if (!evaluated) {
            ASTNode rootNode = getRootNode();
            if (rootNode != null) {
                ensureCompleteEvaluation(rootNode, object, workingMemory, leftTuple);
            }
            evaluated = true;
        }
    }

    private void ensureCompleteEvaluation(ASTNode node, Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple) {
        node = unwrap(node);
        if (node == null || !(node instanceof And || node instanceof Or)) {
            return;
        }
        ensureBranchEvaluation(object, workingMemory, leftTuple, ((BooleanNode)node).getLeft());
        ensureBranchEvaluation(object, workingMemory, leftTuple, ((BooleanNode)node).getRight());
    }

    private ASTNode unwrap(ASTNode node) {
        while (node instanceof Negation || node instanceof LineLabel || node instanceof Substatement) {
            node = unwrapNegation(node);
            node = unwrapSubstatement(node);
        }
        return node;
    }

    private void ensureBranchEvaluation(Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple, ASTNode node) {
        if (!isEvaluated(node)) {
            ASTNode next = node.nextASTNode;
            node.nextASTNode = null;
            evaluate(asCompiledExpression(node), object, workingMemory, leftTuple);
            node.nextASTNode = next;
        }
        ensureCompleteEvaluation(node, object, workingMemory, leftTuple);
    }

    private ASTNode unwrapNegation(ASTNode node) {
        if (node instanceof Negation) {
            ExecutableStatement statement = ((Negation)node).getStatement();
            return statement instanceof ExecutableAccessor ? ((ExecutableAccessor)statement).getNode() : null;
        }
        return node;
    }

    private ASTNode unwrapSubstatement(ASTNode node) {
        if (node instanceof LineLabel) {
            return node.nextASTNode;
        }
        return node instanceof Substatement ? ((ExecutableAccessor)((Substatement)node).getStatement()).getNode() : node;
    }

    private boolean isEvaluated(ASTNode node) {
        node = unwrapSubstatement(node);
        if (node instanceof Contains) {
            return ((Contains)node).getFirstStatement().getAccessor() != null;
        }
        return node instanceof BinaryOperation ? ((BooleanNode) node).getLeft().getAccessor() != null : node.getAccessor() != null;
    }

    private CompiledExpression asCompiledExpression(ASTNode node) {
        return new CompiledExpression(new ASTLinkedList(node), null, Object.class, parserConfiguration, false);
    }

    private ASTNode getRootNode() {
        if (executableStatement instanceof ExecutableLiteral) {
            return null;
        }
        return executableStatement instanceof CompiledExpression ? ((CompiledExpression) executableStatement).getFirstNode() : ((ExecutableAccessor) executableStatement).getNode();
    }
}
