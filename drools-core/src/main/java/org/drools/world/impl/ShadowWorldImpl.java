package org.drools.world.impl;

import java.util.Map;

import org.drools.command.Context;
import org.drools.command.ShadowContext;
import org.drools.command.ShadowWorld;
import org.drools.command.World;
import org.drools.command.impl.ContextImpl;

public class ShadowWorldImpl implements ShadowWorld {
    private World                world;
    
    private Context              rootShadow;

    private Map<String, ShadowContext> contexts;

    public ShadowWorldImpl(World world) {
        this.world = world;
        rootShadow = new ShadowContextImpl( world.getName(), world, this );
    }
    
    public Context createContext(String identifier) {
        Context actualCtx = world.getContext( identifier );
        if ( actualCtx == null ) {
            actualCtx = world.createContext( identifier );
        }
        
        ShadowContext shadowCtx = contexts.get( identifier );
        if ( shadowCtx == null ) {
            shadowCtx = new ShadowContextImpl( world.getName(), world, this );
            contexts.put( identifier, shadowCtx );
        }
         
        return null;
    }    
    
    public ShadowContext getContext(String identifier) {
//        
//        Context ctx = contexts.get( identifier );
//        
//        if ( ctx == null ) {
//            ctx = world.getContext( identifier );            
//            ctx = new ContextImpl( identifier, this, ctx );
//            contexts.put(  identifier, ctx );
//        }
        
        return  null; //ctx;
    }

    public World getContextManager() {
        return this;
    }



    public String getName() {
        return world.getName();
    }

    public Object get(String identifier) {
        return rootShadow.get( identifier );
    }



    public void set(String identifier,
                    Object value) {
        rootShadow.set(identifier, value);
    }



    public void remove(String identifier) {
        rootShadow.remove(  identifier );
    }

}
