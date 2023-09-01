package org.drools.mvel.expr;

import org.drools.base.rule.Declaration;
import org.drools.core.rule.consequence.InternalMatch;
import org.mvel2.integration.PropertyHandler;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.VariableResolverFactory;

public class ActivationPropertyHandler implements PropertyHandler {    
    public ActivationPropertyHandler() {
    }  
    
    public Object getProperty(String name,
                              Object obj,
                              VariableResolverFactory variableFactory) {
        InternalMatch item = (InternalMatch) obj;
        if ( "rule".equals( name ) ) {
            return item.getRule();
        } else if ( "active".equals( name ) ) {
            return item.isQueued();
        } else if ( "objects".equals( name ) ) {
            return item.getObjects();
        } else if ( "factHandles".equals( name ) ) {
            return item.getFactHandles();
        } else if ( "declarationIds".equals( name ) ) {
            return item.getDeclarationIds();
        } else if ( "this".equals( name ) ) {
            return item;
        }

        // FIXME hack as MVEL seems to be ignoring indexed variables
        VariableResolver vr = variableFactory.getNextFactory().getVariableResolver( name );
        if ( vr != null ) {
            return vr.getValue();
        }
                
        Declaration declr = item.getTerminalNode().getSubRule().getOuterDeclarations().get( name );
        if ( declr != null ) {
            return declr.getValue( null, item.getTuple().get( declr ).getObject() );
        } else {
            return item.getRule().getMetaData( name );
        }
    }

    public Object setProperty(String name,
                              Object contextObj,
                              VariableResolverFactory variableFactory,
                              Object value) {
        throw new IllegalArgumentException( "Cannot set " + name + " as activations are immutable." );
    }

}
