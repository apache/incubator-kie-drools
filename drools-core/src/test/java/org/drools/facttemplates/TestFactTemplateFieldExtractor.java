package org.drools.facttemplates;

import org.drools.Cheese;
import org.drools.rule.Declaration;
import org.drools.spi.Extractor;

import junit.framework.TestCase;

public class TestFactTemplateFieldExtractor extends TestCase {
    public void testExtractor() {
        FieldTemplate cheeseName = new FieldTemplateImpl( "name",
                                                          0,
                                                          String.class );
        FieldTemplate cheesePrice = new FieldTemplateImpl( "price",
                                                           1,
                                                           Integer.class );
        FieldTemplate[] fields = new FieldTemplate[]{cheeseName, cheesePrice};
        FactTemplate cheese = new FactTemplateImpl( "org.store",
                                                    "Cheese",
                                                    fields );

        Extractor extractName = new FactTemplateFieldExtractor( cheese,
                                                                0 );
        Extractor extractPrice = new FactTemplateFieldExtractor( cheese,
                                                                 1 );

        Fact stilton = cheese.createFact( 10 );
        stilton.setFieldValue( "name",
                               "stilton" );
        stilton.setFieldValue( "price",
                               new Integer( 200 ) );

        assertEquals( "stilton",
                      extractName.getValue( stilton ) );

        assertEquals( new Integer( 200 ),
                      extractPrice.getValue( stilton ) );

        Fact brie = cheese.createFact( 12 );
        brie.setFieldValue( "name",
                            "brie" );
        brie.setFieldValue( "price",
                            new Integer( 55 ) );

        assertEquals( "brie",
                      extractName.getValue( brie ) );

        assertEquals( new Integer( 55 ),
                      extractPrice.getValue( brie ) );
    }
    
    public void testDeclaration() {
        FieldTemplate cheeseName = new FieldTemplateImpl( "name",
                                                          0,
                                                          String.class );
        FieldTemplate cheesePrice = new FieldTemplateImpl( "price",
                                                           1,
                                                           Integer.class );
        FieldTemplate[] fields = new FieldTemplate[]{cheeseName, cheesePrice};
        FactTemplate cheese = new FactTemplateImpl( "org.store",
                                                    "Cheese",
                                                    fields );
        
        Extractor extractName = new FactTemplateFieldExtractor( cheese,
                                                                0 );        
        
        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractName,
                                                         0 );

        Fact brie = cheese.createFact( 12 );
        brie.setFieldValue( "name",
                            "brie" );
        brie.setFieldValue( "price",
                            new Integer( 55 ) );

        /* Check we can extract Declarations correctly */
        assertEquals( "brie",
                      declaration.getValue( brie ) );        
    }
}
