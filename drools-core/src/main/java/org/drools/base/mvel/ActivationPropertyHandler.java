package org.drools.base.mvel;

import org.drools.common.AgendaItem;
import org.drools.rule.Declaration;
import org.mvel2.integration.PropertyHandler;
import org.mvel2.integration.VariableResolverFactory;

public class ActivationPropertyHandler implements PropertyHandler {    
    public ActivationPropertyHandler() {
    }  
    
    public Object getProperty(String name,
                              Object obj,
                              VariableResolverFactory variableFactory) {
        AgendaItem item = ( AgendaItem ) obj;
        Declaration declr = item.getRuleTerminalNode().getSubRule().getOuterDeclarations().get( name );
        if ( declr != null ) {
            return declr.getValue( null, item.getTuple().get( declr ).getObject() );
        }
        
        if ( "rule".equals( name ) ) {
            throw new IllegalArgumentException( name + " field does not exist on Activation.s" );
        }
        
        return item.getRule();
    }

    public Object setProperty(String name,
                              Object contextObj,
                              VariableResolverFactory variableFactory,
                              Object value) {
        throw new IllegalArgumentException( "Cannot set " + name + " as activations are immutable." );
    }

}
