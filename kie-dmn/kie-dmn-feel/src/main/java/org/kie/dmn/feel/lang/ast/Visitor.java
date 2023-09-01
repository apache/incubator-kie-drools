package org.kie.dmn.feel.lang.ast;

public interface Visitor<T> {
    T visit(ASTNode n);
    T visit(DashNode n);
    T visit(BooleanNode n);
    T visit(NumberNode n);
    T visit(StringNode n);
    T visit(NullNode n);
    T visit(CTypeNode n);
    T visit(NameDefNode n);
    T visit(NameRefNode n);
    T visit(QualifiedNameNode n);
    T visit(InfixOpNode n);
    T visit(InstanceOfNode n);
    T visit(IfExpressionNode n);
    T visit(ForExpressionNode n);
    T visit(BetweenNode n);
    T visit(ContextNode n);
    T visit(ContextEntryNode n);
    T visit(FilterExpressionNode n);
    T visit(FunctionDefNode n);
    T visit(FunctionInvocationNode n);
    T visit(NamedParameterNode n);
    T visit(InNode n);
    T visit(IterationContextNode n);
    T visit(ListNode n);
    T visit(PathExpressionNode n);
    T visit(QuantifiedExpressionNode n);
    T visit(RangeNode n);
    T visit(SignedUnaryNode n);
    T visit(UnaryTestNode n);
    T visit(UnaryTestListNode n);
    T visit(FormalParameterNode n);
    T visit(AtLiteralNode n);
    T visit(ListTypeNode n);
    T visit(ContextTypeNode n);
    T visit(FunctionTypeNode n);
}