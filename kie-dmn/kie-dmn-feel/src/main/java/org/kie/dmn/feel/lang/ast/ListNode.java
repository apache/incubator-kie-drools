package org.kie.dmn.feel.lang.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;

public class ListNode
        extends BaseNode {

    private List<BaseNode> elements;

    public ListNode(ParserRuleContext ctx) {
        super( ctx );
        elements = new ArrayList<>();
    }

    public ListNode(ParserRuleContext ctx, List<BaseNode> elements) {
        super( ctx );
        this.elements = elements;
    }

    public ListNode(List<BaseNode> elements) {
        this.elements = elements;
    }

    public List<BaseNode> getElements() {
        return elements;
    }

    public void setElements(List<BaseNode> elements) {
        this.elements = elements;
    }

    @Override
    public List evaluate(EvaluationContext ctx) {
        return elements.stream().map( e -> e != null ? e.evaluate( ctx ) : null ).collect( Collectors.toList() );
    }

    @Override
    public Type getResultType() {
        return BuiltInType.LIST;
    }

    @Override
    public ASTNode[] getChildrenNode() {
        return elements.toArray( new ASTNode[elements.size()] );
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

}
