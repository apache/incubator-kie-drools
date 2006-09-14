package org.drools.semantics.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.spi.AvailableVariables;
import org.drools.spi.FunctionResolver;
import org.drools.spi.TypeResolver;

public class StaticMethodFunctionResolverTest extends TestCase {
    public void test1() throws Exception {
        List list = new ArrayList();
        list.add( "org.drools.StaticMethods" );
        TypeResolver typeResolver = new ClassTypeResolver( list );

        list = new ArrayList();
        list.add( "StaticMethods.*" );
        FunctionResolver functionResolver = new StaticMethodFunctionResolver( list,
                                                                              typeResolver );

        assertEquals( "org.drools.StaticMethods",
                      functionResolver.resolveFunction( "getString1",
                                                        "a" ) );

        Map map = new HashMap();
        map.put( "a",
                 String.class );

        assertEquals( "org.drools.StaticMethods",
                      functionResolver.resolveFunction( "getString1",
                                                        "a",
                                                        new AvailableVariables( new Map[]{map} ) ) );

    }
}
