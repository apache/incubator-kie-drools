package org.drools.facttemplates;

import junit.framework.TestCase;

import org.drools.rule.Pattern;
import org.drools.rule.Declaration;
import org.drools.rule.Package;
import org.drools.spi.Extractor;

public class FactTemplateFieldExtractorTest extends TestCase {
    public void testExtractor() {
        final Package pkg = new Package( "org.store" );

        final FieldTemplate cheeseName = new FieldTemplateImpl( "name",
                                                                0,
                                                                String.class );
        final FieldTemplate cheesePrice = new FieldTemplateImpl( "price",
                                                                 1,
                                                                 Integer.class );
        final FieldTemplate[] fields = new FieldTemplate[]{cheeseName, cheesePrice};
        final FactTemplate cheese = new FactTemplateImpl( pkg,
                                                          "Cheese",
                                                          fields );

        final Extractor extractName = new FactTemplateFieldExtractor( cheese,
                                                                      0 );
        final Extractor extractPrice = new FactTemplateFieldExtractor( cheese,
                                                                       1 );

        final Fact stilton = cheese.createFact( 10 );
        stilton.setFieldValue( "name",
                               "stilton" );
        stilton.setFieldValue( "price",
                               new Integer( 200 ) );

        assertEquals( "stilton",
                      extractName.getValue( stilton ) );

        assertEquals( new Integer( 200 ),
                      extractPrice.getValue( stilton ) );

        final Fact brie = cheese.createFact( 12 );
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
        final Package pkg = new Package( "org.store" );

        final FieldTemplate cheeseName = new FieldTemplateImpl( "name",
                                                                0,
                                                                String.class );
        final FieldTemplate cheesePrice = new FieldTemplateImpl( "price",
                                                                 1,
                                                                 Integer.class );
        final FieldTemplate[] fields = new FieldTemplate[]{cheeseName, cheesePrice};
        final FactTemplate cheese = new FactTemplateImpl( pkg,
                                                          "Cheese",
                                                          fields );

        final Extractor extractName = new FactTemplateFieldExtractor( cheese,
                                                                      0 );

        final Pattern pattern = new Pattern( 0,
                                          new FactTemplateObjectType( cheese ) );

        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractName,
                                                         pattern );

        final Fact brie = cheese.createFact( 12 );
        brie.setFieldValue( "name",
                            "brie" );
        brie.setFieldValue( "price",
                            new Integer( 55 ) );

        // Check we can extract Declarations correctly 
        assertEquals( "brie",
                      declaration.getValue( brie ) );
    }
}
