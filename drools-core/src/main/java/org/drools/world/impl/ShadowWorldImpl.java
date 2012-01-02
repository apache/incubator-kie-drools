package org.drools.world.impl;

import java.util.Map;

import org.drools.command.Context;
import org.drools.command.World;
import org.drools.command.impl.ContextImpl;

public class ShadowWorldImpl implements World {
    private World                world;
    
    private Context              rootShadow;

    private Map<String, Context> contexts;

    public ShadowWorldImpl(World world) {
        this.world = world;
        rootShadow = new ContextImpl( world.getName(), this, world.getContextManager() );
    }
    
    
    public Context getContext(String identifier) {
        
        Context ctx = contexts.get( identifier );
        
        if ( ctx == null ) {
            ctx = world.getContext( identifier );            
            ctx = new ContextImpl( identifier, this, ctx );
            contexts.put(  identifier, ctx );
        }
        
        return  ctx;
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
