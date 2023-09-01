package org.kie.dmn.feel.lang.ast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.runtime.functions.CustomFEELFunction;
import org.kie.dmn.feel.runtime.functions.DTInvokerFunction;
import org.kie.dmn.feel.runtime.functions.JavaFunction;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.feel.util.Msg;

public class ContextNode
        extends BaseNode {

    private List<ContextEntryNode> entries = new ArrayList<>();
    private MapBackedType parsedResultType = new MapBackedType();

    public ContextNode(ParserRuleContext ctx) {
        super( ctx );
    }

    public ContextNode(ParserRuleContext ctx, ListNode list) {
        super( ctx );
        for( BaseNode node : list.getElements() ) {
            ContextEntryNode entry = (ContextEntryNode) node;
            entries.add( entry );
            parsedResultType.addField(entry.getName().getText(), entry.getResultType());
        }
    }

    public List<ContextEntryNode> getEntries() {
        return entries;
    }

    public void setEntries(List<ContextEntryNode> entries) {
        this.entries = entries;
    }

    @Override
    public Object evaluate(EvaluationContext ctx) {
        try {
            ctx.enterFrame();
            Map<String, Object> c = new LinkedHashMap<>();
            for( ContextEntryNode cen : entries ) {
                String name = EvalHelper.normalizeVariableName( cen.evaluateName( ctx ) );
                if (c.containsKey(name)) {
                    ctx.notifyEvt( astEvent( FEELEvent.Severity.ERROR, Msg.createMessage( Msg.DUPLICATE_KEY_CTX, name)) );
                    return null;
                }
                Object value = cen.evaluate( ctx );
                if( value instanceof CustomFEELFunction ) {
                    // helpful for debugging
                    ((CustomFEELFunction) value).setName( name );
                } else if( value instanceof JavaFunction ) {
                    ((JavaFunction) value).setName( name );
                } else if ( value instanceof DTInvokerFunction ) {
                    ((DTInvokerFunction) value).setName(name);
                }

                ctx.setValue( name, value );
                c.put( name, value );
            }
            return c;
        } finally {
            ctx.exitFrame();
        }
    }

    @Override
    public Type getResultType() {
        return parsedResultType;
    }

    @Override
    public ASTNode[] getChildrenNode() {
        return entries.toArray( new ASTNode[entries.size()] );
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
}
