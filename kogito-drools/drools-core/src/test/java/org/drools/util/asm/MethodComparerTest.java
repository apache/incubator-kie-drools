/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.util.asm;

import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.core.util.asm.MethodComparator;
import org.mvel2.asm.ClassReader;

public class MethodComparerTest {

    @Test
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
