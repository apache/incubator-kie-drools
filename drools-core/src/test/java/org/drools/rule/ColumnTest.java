package org.drools.rule;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.base.ClassObjectType;
import org.drools.facttemplates.Fact;
import org.drools.facttemplates.FactTemplate;
import org.drools.facttemplates.FactTemplateImpl;
import org.drools.facttemplates.FactTemplateObjectType;
import org.drools.facttemplates.FieldTemplate;
import org.drools.facttemplates.FieldTemplateImpl;
import org.drools.spi.Extractor;
import org.drools.spi.ObjectType;

public class ColumnTest extends TestCase {

    public void testDeclarationsObjectType() throws Exception {
        ObjectType type = new ClassObjectType(Cheese.class);
        Column col = new Column(0, type, "foo");
        Declaration dec = col.getDeclaration();
        Extractor ext = dec.getExtractor();
        assertEquals(Cheese.class, ext.getExtractToClass());
        
        Cheese stilton = new Cheese("stilton", 42);
        
        assertEquals(stilton, dec.getValue( stilton ));
        
        
        
    }
    
    public void testDeclarationsFactTemplate() throws Exception {
        
        
        Package pkg = new Package( "org.store" );
        FieldTemplate cheeseName = new FieldTemplateImpl( "name",
                                                          0,
                                                          String.class );
        FieldTemplate cheesePrice = new FieldTemplateImpl( "price",
                                                           1,
                                                           Integer.class );
        FieldTemplate[] fields = new FieldTemplate[]{cheeseName, cheesePrice};
        FactTemplate cheese = new FactTemplateImpl( pkg,
                                                    "Cheese",
                                                    fields );
        
        ObjectType type = new FactTemplateObjectType(cheese);
        
        Column col = new Column(0, type, "foo");
        Declaration dec = col.getDeclaration();
        Extractor ext = dec.getExtractor();
        assertEquals(Fact.class, ext.getExtractToClass());
        
        Fact stilton = cheese.createFact( 10 );
        stilton.setFieldValue( "name", "stilton" );
        stilton.setFieldValue( "price", new Integer( 200 ) );        
        
        assertEquals(stilton, dec.getValue( stilton ));
    }
    
    
}
