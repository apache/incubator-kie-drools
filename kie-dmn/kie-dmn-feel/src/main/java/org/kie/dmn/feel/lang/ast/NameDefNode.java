package org.kie.dmn.feel.lang.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.util.EvalHelper;

import java.util.List;

/**
 * A name is defined either as a sequence of
 * tokens or as a String. This class supports
 * both, although they should not be used
 * interchangeably.
 */
public class NameDefNode
        extends BaseNode {

    private List<String> parts;
    private String name;

    public NameDefNode(ParserRuleContext ctx, List<String> parts) {
        super( ctx );
        this.parts = parts;
    }

    public NameDefNode(ParserRuleContext ctx, String name) {
        super( ctx );
        this.name = name;
    }

    public List<String> getParts() {
        return parts;
    }

    public void setParts(List<String> parts) {
        this.parts = parts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String evaluate(EvaluationContext ctx) {
        return EvalHelper.normalizeVariableName( getText() );
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

}
