package org.drools.util.asm;

import java.io.InputStream;

import junit.framework.TestCase;

import org.drools.core.util.asm.MethodComparator;
import org.mvel2.asm.ClassReader;

public class MethodComparerTest extends TestCase {

    public void testMethodCompare() throws Exception {
        final MethodComparator comp = new MethodComparator();
        boolean result = comp.equivalent( "evaluate",
                                          new ClassReader( getClassData( MethodCompareA.class ) ),
                                          "evaluate",
                                          new ClassReader( getClassData( MethodCompareB.class ) ) );
        assertEquals( true,
                      result );

        result = comp.equivalent( "evaluate",
                                  new ClassReader( getClassData( MethodCompareA.class ) ),
                                  "evaluate2",
                                  new ClassReader( getClassData( MethodCompareA.class ) ) );
        assertEquals( false,
                      result );

        result = comp.equivalent( "evaluate",
                                  new ClassReader( getClassData( MethodCompareB.class ) ),
                                  "evaluate2",
                                  new ClassReader( getClassData( MethodCompareA.class ) ) );
        assertEquals( false,
                      result );

        result = comp.equivalent( "evaluate",
                                  new ClassReader( getClassData( MethodCompareB.class ) ),
                                  "evaluate",
                                  new ClassReader( getClassData( MethodCompareA.class ) ) );
        assertEquals( true,
                      result );

        result = comp.equivalent( "evaluate",
                                  new ClassReader( getClassData( MethodCompareA.class ) ),
                                  "evaluate",
                                  new ClassReader( getClassData( MethodCompareA.class ) ) );
        assertEquals( true,
                      result );

        result = comp.equivalent( "evaluate",
                                  new ClassReader( getClassData( MethodCompareA.class ) ),
                                  "askew",
                                  new ClassReader( getClassData( MethodCompareA.class ) ) );
        assertEquals( false,
                      result );

    }

    private InputStream getClassData(final Class clazz) {
        final String name = getResourcePath( clazz );
        return clazz.getResourceAsStream( name );
    }

    private String getResourcePath(final Class clazz) {
        return "/" + clazz.getName().replaceAll( "\\.",
                                                 "/" ) + ".class";
    }

}
