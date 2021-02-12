package org.kie.dmn.feel.lang.ast.visitor;

import java.util.stream.Stream;

import org.kie.dmn.feel.codegen.feel11.DefaultedVisitor;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.AtLiteralNode;
import org.kie.dmn.feel.lang.ast.ContextEntryNode;
import org.kie.dmn.feel.lang.ast.ContextNode;
import org.kie.dmn.feel.lang.ast.ForExpressionNode;
import org.kie.dmn.feel.lang.ast.FormalParameterNode;
import org.kie.dmn.feel.lang.ast.FunctionDefNode;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.ast.IterationContextNode;
import org.kie.dmn.feel.lang.ast.QuantifiedExpressionNode;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.parser.feel11.ScopeHelper;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;

public class ASTTemporalConstantVisitor extends DefaultedVisitor<ASTNode> {

    ScopeHelper scopeHelper = new ScopeHelper();

    public ASTTemporalConstantVisitor() {
        Stream.of(BuiltInFunctions.getFunctions()).forEach(f -> scopeHelper.addType(f.getName(), BuiltInType.FUNCTION));
        // TODO profiles?
    }

    @Override
    public ASTNode defaultVisit(ASTNode n) {
        for (ASTNode children : n.getChildrenNode()) {
            children.accept(this);
        }
        return n;
    }

    @Override
    public ASTNode visit(ASTNode n) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ASTNode visit(ForExpressionNode n) {
        scopeHelper.pushScope();
        for (IterationContextNode ic : n.getIterationContexts()) {
            scopeHelper.addType(ic.getName().getName(), BuiltInType.UNKNOWN);
            ic.accept(this);
        }
        n.getExpression().accept(this);
        scopeHelper.popScope();
        return n;
    }

    @Override
    public ASTNode visit(ContextNode n) {
        scopeHelper.pushScope();
        for (ContextEntryNode ce : n.getEntries()) {
            scopeHelper.addType(ce.getName().getText(), BuiltInType.UNKNOWN);
            ce.accept(this);
        }
        scopeHelper.popScope();
        return n;
    }

    @Override
    public ASTNode visit(QuantifiedExpressionNode n) {
        scopeHelper.pushScope();
        for (IterationContextNode ic : n.getIterationContexts()) {
            scopeHelper.addType(ic.getName().getName(), BuiltInType.UNKNOWN);
            ic.accept(this);
        }
        n.getExpression().accept(this);
        scopeHelper.popScope();
        return n;
    }

    @Override
    public ASTNode visit(FunctionDefNode n) {
        scopeHelper.pushScope();
        for (FormalParameterNode fp : n.getFormalParameters()) {
            scopeHelper.addType(fp.getName().getName(), BuiltInType.UNKNOWN);
        }
        n.getBody().accept(this);
        scopeHelper.popScope();
        return n;
    }

    @Override
    public ASTNode visit(FunctionInvocationNode n) {
        System.out.println(scopeHelper.resolveType("date"));
        // TODO Auto-generated method stub
        return super.visit(n);
    }


    @Override
    public ASTNode visit(AtLiteralNode n) {
        // TODO Auto-generated method stub
        return super.visit(n);
    }

}
