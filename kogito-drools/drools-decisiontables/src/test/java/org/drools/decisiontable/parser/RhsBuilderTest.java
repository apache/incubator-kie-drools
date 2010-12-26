package org.drools.decisiontable.parser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RhsBuilderTest {

    @Test
    public void testConsBuilding() {
        RhsBuilder builder = new RhsBuilder("foo");
        builder.addTemplate( 1, "setFoo($param)");
        builder.addCellValue( 1, "42" );
        
        
        assertEquals("foo.setFoo(42);", builder.getResult());
        
        builder.clearValues();
        builder.addCellValue( 1, "33" );
        assertEquals("foo.setFoo(33);", builder.getResult());
    }
    
    @Test
    public void testClassicMode() {
        RhsBuilder builder = new RhsBuilder("");
        builder.addTemplate( 1, "p.setSomething($param);" );
        builder.addTemplate( 2, "drools.clearAgenda();" );
                
        builder.addCellValue( 1, "42" );       
        
        assertEquals("p.setSomething(42);", builder.getResult());
                
        builder.addCellValue( 2, "Y" );
        assertEquals("p.setSomething(42);\ndrools.clearAgenda();", builder.getResult());
    }
    
    @Test
    public void testEmptyCellData() {
        RhsBuilder builder = new RhsBuilder("Foo");
        builder.addTemplate( 1, "p.setSomething($param);" );        
        assertFalse(builder.hasValues());
    }
    
}
