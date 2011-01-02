package org.drools.decisiontable.parser;

import junit.framework.TestCase;

public class RhsBuilderTest extends TestCase {

    public void testConsBuilding() {
        RhsBuilder builder = new RhsBuilder( 9, 1, "foo");
        builder.addTemplate( 10, 1, "setFoo($param)");
        builder.addCellValue( 10,1, "42" );
        
        
        assertEquals("foo.setFoo(42);", builder.getResult());
        
        builder.clearValues();
        builder.addCellValue( 10, 1, "33" );
        assertEquals("foo.setFoo(33);", builder.getResult());
    }
    
    public void testClassicMode() {
        RhsBuilder builder = new RhsBuilder( 9, 1, "");
        builder.addTemplate( 10, 1, "p.setSomething($param);" );
        builder.addTemplate( 10, 2, "drools.clearAgenda();" );
                
        builder.addCellValue( 12, 1, "42" );       
        
        assertEquals("p.setSomething(42);", builder.getResult());
                
        builder.addCellValue( 12, 2, "Y" );
        assertEquals("p.setSomething(42);\ndrools.clearAgenda();", builder.getResult());
    }
    
    public void testEmptyCellData() {
        RhsBuilder builder = new RhsBuilder( 9, 1, "Foo");
        builder.addTemplate( 10, 1, "p.setSomething($param);" );        
        assertFalse(builder.hasValues());
    }
    
}
