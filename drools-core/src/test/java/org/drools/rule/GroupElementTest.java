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

package org.drools.rule;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.Person;
import org.drools.RuntimeDroolsException;
import org.drools.base.ClassObjectType;

public class GroupElementTest {

    @Test
    public void testPackNestedAnd() {
        final GroupElement and1 = GroupElementFactory.newAndInstance();
        final Pattern pattern1 = new Pattern( 0,
                                     null );
        and1.addChild( pattern1 );

        final Pattern pattern2 = new Pattern( 0,
                                     null );
        and1.addChild( pattern2 );

        assertEquals( 2,
                      and1.getChildren().size() );
        assertSame( pattern1,
                    and1.getChildren().get( 0 ) );
        assertSame( pattern2,
                    and1.getChildren().get( 1 ) );

        final GroupElement and2 = GroupElementFactory.newAndInstance();
        and2.addChild( and1 );

        and2.pack();
        assertEquals( 2,
                      and2.getChildren().size() );
        assertSame( pattern1,
                    and2.getChildren().get( 0 ) );
        assertSame( pattern2,
                    and2.getChildren().get( 1 ) );
    }
    
    @Test
    public void testDeclarationOrdering() {
        final GroupElement and1 = GroupElementFactory.newAndInstance();
        final Pattern pattern1 = new Pattern( 0,
                                              new ClassObjectType( Person.class),
                                              "x" );
        and1.addChild( pattern1 );

        final Pattern pattern2 = new Pattern( 2,
                                              new ClassObjectType( Person.class),
                                              "y"  );
        and1.addChild( pattern2 );
        
        Declaration x1 = ( Declaration ) and1.getInnerDeclarations().get( "x" );
        Declaration y1 = ( Declaration ) and1.getInnerDeclarations().get( "y" );
        Declaration z1 = ( Declaration ) and1.getInnerDeclarations().get( "z" );
        assertNotNull( x1 );
        assertNotNull( y1 );
        assertNull( z1);

        assertEquals( 2,
                      and1.getChildren().size() );
        assertSame( pattern1,
                    and1.getChildren().get( 0 ) );
        assertSame( pattern2,
                    and1.getChildren().get( 1 ) );

        final GroupElement and2 = GroupElementFactory.newAndInstance();
        and2.addChild( and1 );
        
        final Pattern pattern3 = new Pattern( 3,
                                              new ClassObjectType( Person.class),
                                              "x" );        
        and2.addChild( pattern3 );
        
        and2.pack();
        assertEquals( 3,
                      and2.getChildren().size() );
        assertSame( pattern1,
                    and2.getChildren().get( 0 ) );
        assertSame( pattern2,
                    and2.getChildren().get( 1 ) );
        assertSame( pattern3,
                    and2.getChildren().get( 2 ) );        
        
        Declaration x2 = ( Declaration ) and2.getInnerDeclarations().get( "x" );
        Declaration y2 = ( Declaration ) and2.getInnerDeclarations().get( "y" );
        Declaration z2 = ( Declaration ) and2.getInnerDeclarations().get( "z" );
        assertNotNull( x2 );        
        assertNotNull( y2 );
        assertNull( z2);    
        
        assertNotSame( x1, x2);
        assertSame( x2, pattern3.getDeclaration() );
        assertSame( y1, y2);
        assertSame( z1, z2);
    }    

    @Test
    public void testPackNestedOr() {
        final GroupElement or1 = GroupElementFactory.newOrInstance();
        final Pattern pattern1 = new Pattern( 0,
                                     null );
        or1.addChild( pattern1 );

        final Pattern pattern2 = new Pattern( 0,
                                     null );
        or1.addChild( pattern2 );

        assertEquals( 2,
                      or1.getChildren().size() );
        assertSame( pattern1,
                    or1.getChildren().get( 0 ) );
        assertSame( pattern2,
                    or1.getChildren().get( 1 ) );

        final GroupElement or2 = GroupElementFactory.newOrInstance();
        or2.addChild( or1 );

        or2.pack();

        assertEquals( 2,
                      or2.getChildren().size() );
        assertSame( pattern1,
                    or2.getChildren().get( 0 ) );
        assertSame( pattern2,
                    or2.getChildren().get( 1 ) );
    }

    @Test
    public void testPackNestedExists() {
        final GroupElement exists1 = GroupElementFactory.newExistsInstance();
        final Pattern pattern1 = new Pattern( 0,
                                     null );
        exists1.addChild( pattern1 );

        assertEquals( 1,
                      exists1.getChildren().size() );
        assertSame( pattern1,
                    exists1.getChildren().get( 0 ) );

        final GroupElement exists2 = GroupElementFactory.newExistsInstance();
        exists2.addChild( exists1 );

        exists2.pack();

        assertEquals( 1,
                      exists2.getChildren().size() );
        assertSame( pattern1,
                    exists2.getChildren().get( 0 ) );
    }

    @Test
    public void testAddMultipleChildsIntoNot() {
        final GroupElement not = GroupElementFactory.newNotInstance();

        final Pattern pattern1 = new Pattern( 0,
                                     null );
        try {
            not.addChild( pattern1 );
        } catch ( final RuntimeDroolsException rde ) {
            fail( "Adding a single child is not supposed to throw Exception for NOT GE: " + rde.getMessage() );
        }

        final Pattern pattern2 = new Pattern( 0,
                                     null );
        try {
            not.addChild( pattern2 );
            fail( "Adding a second child into a NOT GE should throw Exception" );
        } catch ( final RuntimeDroolsException rde ) {
            // everything is fine
        }
    }

    @Test
    public void testAddSingleBranchAnd() {
        final GroupElement and1 = GroupElementFactory.newAndInstance();
        final Pattern pattern = new Pattern( 0,
                                    null );
        and1.addChild( pattern );
        assertEquals( 1,
                      and1.getChildren().size() );
        assertSame( pattern,
                    and1.getChildren().get( 0 ) );

        final GroupElement or1 = GroupElementFactory.newOrInstance();
        or1.addChild( and1 );

        or1.pack();
        assertEquals( 1,
                      or1.getChildren().size() );
        assertSame( pattern,
                    or1.getChildren().get( 0 ) );
    }

    @Test
    public void testAddSingleBranchOr() {
        final GroupElement or1 = GroupElementFactory.newOrInstance();
        final Pattern pattern = new Pattern( 0,
                                    null );
        or1.addChild( pattern );
        assertEquals( 1,
                      or1.getChildren().size() );
        assertSame( pattern,
                    or1.getChildren().get( 0 ) );

        final GroupElement and1 = GroupElementFactory.newAndInstance();
        and1.addChild( or1 );

        and1.pack();
        assertEquals( 1,
                      and1.getChildren().size() );
        assertSame( pattern,
                    and1.getChildren().get( 0 ) );
    }

    /**
     * This test tests deep nested structures, and shall transform this:
     * 
     *    AND2
     *     |
     *    OR3
     *     |
     *    OR2
     *     |
     *    AND1
     *     |
     *    OR1
     *    / \
     *   C1  C2
     *   
     * Into this:
     * 
     *   OR1
     *   / \
     *  C1 C2
     */
    @Test
    public void testDeepNestedStructure() {
        final GroupElement or1 = GroupElementFactory.newOrInstance();
        final Pattern pattern1 = new Pattern( 0,
                                     null );
        or1.addChild( pattern1 );

        final Pattern pattern2 = new Pattern( 0,
                                     null );
        or1.addChild( pattern2 );

        final GroupElement and1 = GroupElementFactory.newAndInstance();
        and1.addChild( or1 );
        assertEquals( 1,
                      and1.getChildren().size() );
        assertSame( or1,
                    and1.getChildren().get( 0 ) );

        assertSame( pattern1,
                    or1.getChildren().get( 0 ) );
        assertSame( pattern2,
                    or1.getChildren().get( 1 ) );

        final GroupElement or2 = GroupElementFactory.newOrInstance();
        or2.addChild( and1 );

        assertEquals( 1,
                      or2.getChildren().size() );
        assertSame( and1,
                    or2.getChildren().get( 0 ) );

        final GroupElement or3 = GroupElementFactory.newOrInstance();
        or3.addChild( or2 );

        assertEquals( 1,
                      or2.getChildren().size() );
        assertSame( or2,
                    or3.getChildren().get( 0 ) );

        final GroupElement and2 = GroupElementFactory.newAndInstance();
        and2.addChild( or3 );

        assertEquals( 1,
                      and2.getChildren().size() );
        assertSame( or3,
                    and2.getChildren().get( 0 ) );

        // Now pack the structure
        and2.pack();

        // and2 now is in fact transformed into an OR
        assertEquals( GroupElement.Type.OR,
                      and2.getType() );

        assertEquals( 2,
                      and2.getChildren().size() );

        assertSame( pattern1,
                    and2.getChildren().get( 0 ) );
        assertSame( pattern2,
                    and2.getChildren().get( 1 ) );

    }

    /**
     * This test tests deep nested structures, and shall transform this:
     * 
     *      AND2
     *      / \ 
     *    OR3  C3
     *     |
     *    OR2
     *     |
     *    AND1
     *     |
     *    OR1
     *    / \
     *   C1  C2
     *   
     * Into this:
     * 
     *     AND2
     *     /  \
     *   OR1  C3
     *   / \
     *  C1 C2
     */
    @Test
    public void testDeepNestedStructureWithMultipleElementsInRoot() {
        final GroupElement or1 = GroupElementFactory.newOrInstance();
        final Pattern pattern1 = new Pattern( 0,
                                     null );
        or1.addChild( pattern1 );

        final Pattern pattern2 = new Pattern( 0,
                                     null );
        or1.addChild( pattern2 );

        final GroupElement and1 = GroupElementFactory.newAndInstance();
        and1.addChild( or1 );
        assertEquals( 1,
                      and1.getChildren().size() );
        assertSame( or1,
                    and1.getChildren().get( 0 ) );

        assertSame( pattern1,
                    or1.getChildren().get( 0 ) );
        assertSame( pattern2,
                    or1.getChildren().get( 1 ) );

        final GroupElement or2 = GroupElementFactory.newOrInstance();
        or2.addChild( and1 );

        assertEquals( 1,
                      or2.getChildren().size() );
        assertSame( and1,
                    or2.getChildren().get( 0 ) );

        final GroupElement or3 = GroupElementFactory.newOrInstance();
        or3.addChild( or2 );

        assertEquals( 1,
                      or2.getChildren().size() );
        assertSame( or2,
                    or3.getChildren().get( 0 ) );

        final GroupElement and2 = GroupElementFactory.newAndInstance();
        and2.addChild( or3 );

        final Pattern pattern3 = new Pattern( 0,
                                     null );
        and2.addChild( pattern3 );

        assertEquals( 2,
                      and2.getChildren().size() );
        assertSame( or3,
                    and2.getChildren().get( 0 ) );
        assertSame( pattern3,
                    and2.getChildren().get( 1 ) );

        // Now pack the structure
        and2.pack();

        // and2 now is in fact transformed into an OR
        assertTrue( and2.isAnd() );

        assertEquals( 2,
                      and2.getChildren().size() );

        // order must be the same
        assertSame( or1,
                    and2.getChildren().get( 0 ) );
        assertSame( pattern3,
                    and2.getChildren().get( 1 ) );

        assertEquals( 2,
                      or1.getChildren().size() );
        assertSame( pattern1,
                    or1.getChildren().get( 0 ) );
        assertSame( pattern2,
                    or1.getChildren().get( 1 ) );

    }

}
