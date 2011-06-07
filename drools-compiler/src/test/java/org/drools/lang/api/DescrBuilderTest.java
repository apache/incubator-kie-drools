/*
 * Copyright 2011 JBoss Inc
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

package org.drools.lang.api;

import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.PackageDescr;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * DescrBuilderTest
 */
public class DescrBuilderTest {

    @Test
    public void testPackage() {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.drools" )
                .attribute( "dialect" ).value( "mvel" ).end()
                .getDescr();

        assertEquals( "org.drools",
                      pkg.getName() );
        assertEquals( "mvel",
                      pkg.getAttribute( "dialect" ).getValue() );
        assertNull( pkg.getAttribute( "salience" ) );
    }

    @Test
    public void testPackageAttributes() {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.drools" )
                // first syntax
                .attribute( "dialect" ).value( "mvel" ).end()
                // second syntax
                .attribute( "salience",
                            "10" )
                // third syntax
                .attribute( "lock-on-active",
                            "true",
                            AttributeDescr.Type.BOOLEAN )
                .getDescr();

        assertEquals( "org.drools",
                      pkg.getName() );
        assertEquals( 3,
                      pkg.getAttributes().size() );
        assertEquals( "mvel",
                      pkg.getAttribute( "dialect" ).getValue() );
        assertEquals( "10",
                      pkg.getAttribute( "salience" ).getValue() );
        assertEquals( "true",
                      pkg.getAttribute( "lock-on-active" ).getValue() );
        assertEquals( AttributeDescr.Type.BOOLEAN,
                      pkg.getAttribute( "lock-on-active" ).getType() );
        assertNull( pkg.getAttribute( "no-loop" ) );
    }

    @Test
    public void testPackageImports() {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.drools" )
                .newImport().target( "java.util.List" ).end()
                .newImport().target( "org.drools.examples.*" ).end()
                .getDescr();

        assertEquals( 2,
                      pkg.getImports().size() );
        assertEquals( "java.util.List",
                      pkg.getImports().get( 0 ).getTarget() );
        assertEquals( "org.drools.examples.*",
                      pkg.getImports().get( 1 ).getTarget() );
    }
    
    @Test
    public void testGlobals() {
        PackageDescr pkg = DescrFactory.newPackage()
                .name( "org.drools" )
                .newGlobal().type( "java.util.List" ).identifier( "list" ).end()
                .newGlobal().type( "Person" ).identifier( "bob" ).end()
                .getDescr();

        assertEquals( 2,
                      pkg.getGlobals().size() );
        assertEquals( "java.util.List",
                      pkg.getGlobals().get( 0 ).getType() );
        assertEquals( "list",
                      pkg.getGlobals().get( 0 ).getIdentifier() );
        assertEquals( "Person",
                      pkg.getGlobals().get( 1 ).getType() );
        assertEquals( "bob",
                      pkg.getGlobals().get( 1 ).getIdentifier() );
    }
    

}
