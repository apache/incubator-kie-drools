package org.drools.semantics.java;

import java.util.ArrayList;
import java.util.List;

import org.drools.spi.FunctionResolver;
import org.drools.spi.TypeResolver;

import junit.framework.TestCase;

public class StaticMethodFunctionResolverTest extends TestCase {
    public void test1() {                
        List list = new ArrayList();
        list.add( "org.drools.StaticMethods" );        
        TypeResolver typeResolver = new ClassTypeResolver( list );
        
        
        list = new ArrayList();
        list.add( "StaticMethods.*" );
        FunctionResolver functionResolver = new StaticMethodFunctionResolver( list, typeResolver );
        
        System.out.println( functionResolver.resolveFunction( "getString", 1 ) );
        
        
    }
}
