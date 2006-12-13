package org.drools.rule;

import org.drools.RuntimeDroolsException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class GroupElementTest extends TestCase {

    public void testPackNestedAnd() {
        GroupElement and1 = GroupElementFactory.newAndInstance();
        Column column1 = new Column( 0,
                                     null );
        and1.addChild( column1 );

        Column column2 = new Column( 0,
                                     null );
        and1.addChild( column2 );

        assertEquals( 2,
                      and1.getChildren().size() );
        assertSame( column1,
                    and1.getChildren().get( 0 ) );
        assertSame( column2,
                    and1.getChildren().get( 1 ) );

        GroupElement and2 = GroupElementFactory.newAndInstance();
        and2.addChild( and1 );

        and2.pack();
        assertEquals( 2,
                      and2.getChildren().size() );
        assertSame( column1,
                    and2.getChildren().get( 0 ) );
        assertSame( column2,
                    and2.getChildren().get( 1 ) );
    }

    public void testPackNestedOr() {
        GroupElement or1 = GroupElementFactory.newOrInstance();
        Column column1 = new Column( 0,
                                     null );
        or1.addChild( column1 );

        Column column2 = new Column( 0,
                                     null );
        or1.addChild( column2 );

        assertEquals( 2,
                      or1.getChildren().size() );
        assertSame( column1,
                    or1.getChildren().get( 0 ) );
        assertSame( column2,
                    or1.getChildren().get( 1 ) );

        GroupElement or2 = GroupElementFactory.newOrInstance();
        or2.addChild( or1 );

        or2.pack();

        assertEquals( 2,
                      or2.getChildren().size() );
        assertSame( column1,
                    or2.getChildren().get( 0 ) );
        assertSame( column2,
                    or2.getChildren().get( 1 ) );
    }

    public void testPackNestedExists() {
        GroupElement exists1 = GroupElementFactory.newExistsInstance();
        Column column1 = new Column( 0,
                                     null );
        exists1.addChild( column1 );

        assertEquals( 1,
                      exists1.getChildren().size() );
        assertSame( column1,
                    exists1.getChildren().get( 0 ) );

        GroupElement exists2 = GroupElementFactory.newExistsInstance();
        exists2.addChild( exists1 );

        exists2.pack();

        assertEquals( 1,
                      exists2.getChildren().size() );
        assertSame( column1,
                    exists2.getChildren().get( 0 ) );
    }

    public void testAddMultipleChildsIntoNot() {
        GroupElement not = GroupElementFactory.newNotInstance();

        Column column1 = new Column( 0,
                                     null );
        try {
            not.addChild( column1 );
        } catch ( RuntimeDroolsException rde ) {
            Assert.fail( "Adding a single child is not supposed to throw Exception for NOT GE: " + rde.getMessage() );
        }

        Column column2 = new Column( 0,
                                     null );
        try {
            not.addChild( column2 );
            Assert.fail( "Adding a second child into a NOT GE should throw Exception" );
        } catch ( RuntimeDroolsException rde ) {
            // everything is fine
        }
    }

    public void testAddSingleBranchAnd() {
        GroupElement and1 = GroupElementFactory.newAndInstance();
        Column column = new Column( 0,
                                    null );
        and1.addChild( column );
        assertEquals( 1,
                      and1.getChildren().size() );
        assertSame( column,
                    and1.getChildren().get( 0 ) );

        GroupElement or1 = GroupElementFactory.newOrInstance();
        or1.addChild( and1 );

        or1.pack();
        assertEquals( 1,
                      or1.getChildren().size() );
        assertSame( column,
                    or1.getChildren().get( 0 ) );
    }

    public void testAddSingleBranchOr() {
        GroupElement or1 = GroupElementFactory.newOrInstance();
        Column column = new Column( 0,
                                    null );
        or1.addChild( column );
        assertEquals( 1,
                      or1.getChildren().size() );
        assertSame( column,
                    or1.getChildren().get( 0 ) );

        GroupElement and1 = GroupElementFactory.newAndInstance();
        and1.addChild( or1 );

        and1.pack();
        assertEquals( 1,
                      and1.getChildren().size() );
        assertSame( column,
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
     *
     */
    public void testDeepNestedStructure() {
        GroupElement or1 = GroupElementFactory.newOrInstance();
        Column column1 = new Column( 0,
                                     null );
        or1.addChild( column1 );

        Column column2 = new Column( 0,
                                     null );
        or1.addChild( column2 );

        GroupElement and1 = GroupElementFactory.newAndInstance();
        and1.addChild( or1 );
        assertEquals( 1,
                      and1.getChildren().size() );
        assertSame( or1,
                    and1.getChildren().get( 0 ) );

        assertSame( column1,
                    or1.getChildren().get( 0 ) );
        assertSame( column2,
                    or1.getChildren().get( 1 ) );

        GroupElement or2 = GroupElementFactory.newOrInstance();
        or2.addChild( and1 );

        assertEquals( 1,
                      or2.getChildren().size() );
        assertSame( and1,
                    or2.getChildren().get( 0 ) );

        GroupElement or3 = GroupElementFactory.newOrInstance();
        or3.addChild( or2 );

        assertEquals( 1,
                      or2.getChildren().size() );
        assertSame( or2,
                    or3.getChildren().get( 0 ) );

        GroupElement and2 = GroupElementFactory.newAndInstance();
        and2.addChild( or3 );

        assertEquals( 1,
                      and2.getChildren().size() );
        assertSame( or3,
                    and2.getChildren().get( 0 ) );
        
        // Now pack the structure
        and2.pack();
        
        // and2 now is in fact transformed into an OR
        assertEquals( GroupElement.OR, and2.getType() );
        
        assertEquals( 2,
                      and2.getChildren().size() );
        
        assertSame( column1,
                    and2.getChildren().get( 0 ) );
        assertSame( column2,
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
     *
     */
    public void testDeepNestedStructureWithMultipleElementsInRoot() {
        GroupElement or1 = GroupElementFactory.newOrInstance();
        Column column1 = new Column( 0,
                                     null );
        or1.addChild( column1 );

        Column column2 = new Column( 0,
                                     null );
        or1.addChild( column2 );

        GroupElement and1 = GroupElementFactory.newAndInstance();
        and1.addChild( or1 );
        assertEquals( 1,
                      and1.getChildren().size() );
        assertSame( or1,
                    and1.getChildren().get( 0 ) );

        assertSame( column1,
                    or1.getChildren().get( 0 ) );
        assertSame( column2,
                    or1.getChildren().get( 1 ) );

        GroupElement or2 = GroupElementFactory.newOrInstance();
        or2.addChild( and1 );

        assertEquals( 1,
                      or2.getChildren().size() );
        assertSame( and1,
                    or2.getChildren().get( 0 ) );

        GroupElement or3 = GroupElementFactory.newOrInstance();
        or3.addChild( or2 );

        assertEquals( 1,
                      or2.getChildren().size() );
        assertSame( or2,
                    or3.getChildren().get( 0 ) );

        GroupElement and2 = GroupElementFactory.newAndInstance();
        and2.addChild( or3 );

        Column column3 = new Column( 0,
                                     null );
        and2.addChild( column3 );

        assertEquals( 2,
                      and2.getChildren().size() );
        assertSame( or3,
                    and2.getChildren().get( 0 ) );
        assertSame( column3,
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
        assertSame( column3,
                    and2.getChildren().get( 1 ) );

        
        assertEquals( 2,
                      or1.getChildren().size() );
        assertSame( column1,
                    or1.getChildren().get( 0 ) );
        assertSame( column2,
                    or1.getChildren().get( 1 ) );

    }
    
}
