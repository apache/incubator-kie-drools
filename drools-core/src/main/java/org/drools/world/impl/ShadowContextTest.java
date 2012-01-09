package org.drools.world.impl;

import org.drools.command.Context;
import org.drools.command.ShadowWorld;
import org.drools.command.World;

import org.junit.Test;
import static org.junit.Assert.*;

public class ShadowContextTest {

    @Test
    public void test1() {
        World world = new WorldImpl();
        
        world.set( "k1", "v1" );
        assertEquals( "v1", world.get( "k1" ) );
        
        Context ctx1 = world.createContext( "p1" );
        
        ctx1.set( "k2", "v2" );
                
        assertEquals( "v2", ctx1.get( "k2" ) );
        assertEquals( "v1", ctx1.get( "k1" ) );
        
        //Context ctx2 = world.createContext( "p1" );                
    }
    
    @Test
    public void test2() {
        World world = new WorldImpl();
        
        ShadowWorld shadowWorld = new ShadowWorldImpl( world );
    }
}
