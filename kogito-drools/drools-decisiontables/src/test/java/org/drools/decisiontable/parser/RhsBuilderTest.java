package org.drools.decisiontable.parser;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class RhsBuilderTest {

    @Test
    public void testConsBuilding() {
        RhsBuilder builder = new RhsBuilder( ActionType.Code.ACTION, 9, 1, "foo");
        builder.addTemplate( 10, 1, "setFoo($param)");
        builder.addCellValue( 10,1, "42" );
        
        
        assertEquals("foo.setFoo(42);", builder.getResult());
        
        builder.clearValues();
        builder.addCellValue( 10, 1, "33" );
        assertEquals("foo.setFoo(33);", builder.getResult());
    }
    
    @Test
    public void testClassicMode() {
        RhsBuilder builder = new RhsBuilder( ActionType.Code.ACTION, 9, 1, "");
        builder.addTemplate( 10, 1, "p.setSomething($param);" );
        builder.addTemplate( 10, 2, "drools.clearAgenda();" );
                
        builder.addCellValue( 12, 1, "42" );
        
        assertEquals("p.setSomething(42);", builder.getResult());
                
        builder.addCellValue( 12, 2, "Y" );
        assertEquals("p.setSomething(42);\ndrools.clearAgenda();", builder.getResult());
    }

    @Test
    public void testMetadata() {
        RhsBuilder builder = new RhsBuilder( ActionType.Code.METADATA, 9, 1, "");
        builder.addTemplate( 10, 1, "Author($param)" );
                
        builder.addCellValue( 12, 1, "A. U. Thor" );
        assertEquals("Author(A. U. Thor)", builder.getResult());
        builder.clearValues();
        
        builder.addCellValue( 13, 1, "P. G. Wodehouse" );
        assertEquals("Author(P. G. Wodehouse)", builder.getResult());
    }

    @Test
    public void testEmptyCellData() {
        RhsBuilder builder = new RhsBuilder( ActionType.Code.ACTION, 9, 1, "Foo");
        builder.addTemplate( 10, 1, "p.setSomething($param);" );
        assertFalse(builder.hasValues());
    }
    
}
