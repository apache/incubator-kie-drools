/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel.asm;

import java.io.InputStream;

import org.drools.core.util.asm.MethodCompareA;
import org.drools.core.util.asm.MethodCompareB;
import org.junit.Test;
import org.mvel2.asm.ClassReader;

import static org.junit.Assert.assertEquals;

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
