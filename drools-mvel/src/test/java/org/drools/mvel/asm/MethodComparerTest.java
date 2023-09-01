package org.drools.mvel.asm;

import java.io.InputStream;

import org.drools.core.util.asm.MethodCompareA;
import org.drools.core.util.asm.MethodCompareB;
import org.junit.Test;
import org.mvel2.asm.ClassReader;

import static org.assertj.core.api.Assertions.assertThat;

public class MethodComparerTest {

    @Test
    public void testMethodCompare() throws Exception {
        final MethodComparator comp = new MethodComparator();
        boolean result = comp.equivalent( "evaluate",
                                          new ClassReader( getClassData( MethodCompareA.class ) ),
                                          "evaluate",
                                          new ClassReader( getClassData( MethodCompareB.class ) ) );
        assertThat(result).isEqualTo(true);

        result = comp.equivalent( "evaluate",
                                  new ClassReader( getClassData( MethodCompareA.class ) ),
                                  "evaluate2",
                                  new ClassReader( getClassData( MethodCompareA.class ) ) );
        assertThat(result).isEqualTo(false);

        result = comp.equivalent( "evaluate",
                                  new ClassReader( getClassData( MethodCompareB.class ) ),
                                  "evaluate2",
                                  new ClassReader( getClassData( MethodCompareA.class ) ) );
        assertThat(result).isEqualTo(false);

        result = comp.equivalent( "evaluate",
                                  new ClassReader( getClassData( MethodCompareB.class ) ),
                                  "evaluate",
                                  new ClassReader( getClassData( MethodCompareA.class ) ) );
        assertThat(result).isEqualTo(true);

        result = comp.equivalent( "evaluate",
                                  new ClassReader( getClassData( MethodCompareA.class ) ),
                                  "evaluate",
                                  new ClassReader( getClassData( MethodCompareA.class ) ) );
        assertThat(result).isEqualTo(true);

        result = comp.equivalent( "evaluate",
                                  new ClassReader( getClassData( MethodCompareA.class ) ),
                                  "askew",
                                  new ClassReader( getClassData( MethodCompareA.class ) ) );
        assertThat(result).isEqualTo(false);

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
