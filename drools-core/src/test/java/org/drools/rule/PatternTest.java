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

public class PatternTest extends TestCase {

    public void testDeclarationsObjectType() throws Exception {
        final ObjectType type = new ClassObjectType( Cheese.class );
        final Pattern col = new Pattern( 0,
                                       type,
                                       "foo" );
        final Declaration dec = col.getDeclaration();
        final Extractor ext = dec.getExtractor();
        assertEquals( Cheese.class,
                      ext.getExtractToClass() );

        final Cheese stilton = new Cheese( "stilton",
                                           42 );

        assertEquals( stilton,
                      dec.getValue( null, stilton ) );

    }

    public void testDeclarationsFactTemplate() throws Exception {

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

        final ObjectType type = new FactTemplateObjectType( cheese );

        final Pattern col = new Pattern( 0,
                                       type,
                                       "foo" );
        final Declaration dec = col.getDeclaration();
        final Extractor ext = dec.getExtractor();
        assertEquals( Fact.class,
                      ext.getExtractToClass() );

        final Fact stilton = cheese.createFact( 10 );
        stilton.setFieldValue( "name",
                               "stilton" );
        stilton.setFieldValue( "price",
                               new Integer( 200 ) );

        assertEquals( stilton,
                      dec.getValue( null, stilton ) );
    }

}
