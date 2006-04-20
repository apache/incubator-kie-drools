package org.drools.rule;

import junit.framework.TestCase;

public class GroupElementTest extends TestCase {
    public void test1() {
        //dummy test
        assertTrue(true);
    }
//    public void testAddNestedAnd() {
//        And and1 = new And();
//        Column column1 = new Column(0, null);
//        and1.addChild( column1 );
//        
//        Column column2 = new Column(0, null);
//        and1.addChild( column2 );
//        
//        assertEquals( 2, and1.getChildren().size() );
//        assertSame( column1, and1.getChildren().get( 0 ) );
//        assertSame( column2, and1.getChildren().get( 1 ) );
//        
//        And and2 = new And();
//        and2.addChild( and1 );
//        assertEquals( 2, and2.getChildren().size() );
//        assertSame( column1, and2.getChildren().get( 0 ) );
//        assertSame( column2, and2.getChildren().get( 1 ) );
//    }
//    
//    public void testAddNestedOr() {
//        Or or1 = new Or();
//        Column column1 = new Column(0, null);
//        or1.addChild( column1 );
//        
//        Column column2 = new Column(0, null);
//        or1.addChild( column2 );
//        
//        assertEquals( 2, or1.getChildren().size() );
//        assertSame( column1, or1.getChildren().get( 0 ) );
//        assertSame( column2, or1.getChildren().get( 1 ) );
//        
//        Or or2 = new Or();
//        or2.addChild( or1 );
//        assertEquals( 2, or2.getChildren().size() );
//        assertSame( column1, or2.getChildren().get( 0 ) );
//        assertSame( column2, or2.getChildren().get( 1 ) );
//    }    
//    
//    public void testAddSingleBranchAnd() {
//        And and1 = new And();
//        Column column = new Column(0, null);
//        and1.addChild( column );
//        assertEquals( 1, and1.getChildren().size() );
//        assertSame( column, and1.getChildren().get( 0 ) );
//        
//        Or or1= new Or();
//        or1.addChild( and1 );
//        assertEquals( 1, or1.getChildren().size() );
//        assertSame( column, or1.getChildren().get( 0 ) );
//    }        
//    
//    public void testAddSingleBranchOr() {
//        Or or1 = new Or();
//        Column column = new Column(0, null);
//        or1.addChild( column );
//        assertEquals( 1, or1.getChildren().size() );
//        assertSame( column, or1.getChildren().get( 0 ) );
//        
//        And and1= new And();
//        and1.addChild( or1 );
//        assertEquals( 1, and1.getChildren().size() );
//        assertSame( column, and1.getChildren().get( 0 ) );
//    }        
//    
//    public void testX() {
//        Or or1 = new Or();
//        Column column1 = new Column(0, null);
//        or1.addChild( column1 );
//        
//        Column column2 = new Column(0, null);
//        or1.addChild( column2 );
//        
//        And and1 = new And();
//        and1.addChild( or1 );
//        assertEquals( 1, and1.getChildren().size() );
//        assertSame( or1, and1.getChildren().get( 0 ) );
//        
//        assertSame( column1, or1.getChildren().get( 0 ) );
//        assertSame( column2, or1.getChildren().get( 1 ) );        
//        
//        Or or2 = new Or();
//        or2.addChild( and1 );
//        
//        assertEquals( 2, or1.getChildren().size() );
//        assertSame( column1, or1.getChildren().get( 0 ) );
//        assertSame( column2, or2.getChildren().get( 1 ) );          
//        
//    }      
}
