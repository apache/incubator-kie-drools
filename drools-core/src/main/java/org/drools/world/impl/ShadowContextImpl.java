package org.drools.world.impl;

import org.drools.command.Context;
import org.drools.command.ShadowContext;
import org.drools.command.ShadowWorld;
import org.drools.command.World;
import org.drools.command.impl.ContextImpl;

public class ShadowContextImpl extends ContextImpl implements ShadowContext {
    
    private Context actualCtx;

    public ShadowContextImpl(String name,
                             World manager) {
        super( name,
               manager );
        // TODO Auto-generated constructor stub
    }

    public ShadowWorld getContextManager() {
        return (ShadowWorld) super.getContextManager();
    }

    public void set(String identifier,
                    Object value,
                    boolean shadow) {
        if ( !shadow ) {
            actualCtx.set( identifier, value );
        } else {
            super.set( identifier,value );
        }
    }
    
    

}
