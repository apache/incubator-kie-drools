package org.drools.facttemplates;

import org.drools.rule.Package;

import junit.framework.TestCase;

public class FactTemplateTest extends TestCase {
    public void testFieldsAndGetters() {
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

        assertEquals( "org.store",
                      cheese.getPackage().getName() );
        assertEquals( "Cheese",
                      cheese.getName() );

        assertEquals( 2,
                      cheese.getNumberOfFields() );

        assertSame( fields,
                    cheese.getAllFieldTemplates() );

        assertSame( cheeseName,
                    cheese.getFieldTemplate( 0 ) );
        assertSame( cheesePrice,
                    cheese.getFieldTemplate( 1 ) );

        assertSame( cheeseName,
                    cheese.getFieldTemplate( "name" ) );
        assertSame( cheesePrice,
                    cheese.getFieldTemplate( "price" ) );

        assertEquals( 0,
                      cheese.getFieldTemplateIndex( "name" ) );
        assertEquals( 1,
                      cheese.getFieldTemplateIndex( "price" ) );
    }

    public void testEqualsAndHashCode() {
        Package pkg = new Package( "org.store" );
        
        // Create cheese1 with name and price fields
        FieldTemplate cheeseName = new FieldTemplateImpl( "name",
                                                          0,
                                                          String.class );
        FieldTemplate cheesePrice = new FieldTemplateImpl( "price",
                                                           1,
                                                           Integer.class );
        FieldTemplate[] fields1 = new FieldTemplate[]{cheeseName, cheesePrice};
        FactTemplate cheese1 = new FactTemplateImpl( pkg,
                                                     "Cheese",
                                                     fields1 );

        // Create cheese2 with type and price fields
        FieldTemplate cheeseType = new FieldTemplateImpl( "type",
                                                          0,
                                                          String.class );
        FieldTemplate[] fields2 = new FieldTemplate[]{cheeseType, cheesePrice};
        FactTemplate cheese2 = new FactTemplateImpl( pkg,
                                                     "Cheese",
                                                     fields2 );

        assertNotSame( cheese1, cheese2 );
        
        assertFalse( cheese1.equals( cheese2 ) );
        
        assertFalse( cheese1.hashCode() == cheese2.hashCode() );
        
        
        
        // create cheese3 with name and price fields, using new instances
        FieldTemplate cheeseName2 = new FieldTemplateImpl( "name",
                                                           0,
                                                           String.class );
        FieldTemplate cheesePrice2 = new FieldTemplateImpl( "price",
                                                            1,
                                                            Integer.class );
        FieldTemplate[] fields3 = new FieldTemplate[]{cheeseName2, cheesePrice2};
        FactTemplate cheese3 = new FactTemplateImpl( pkg,
                                                     "Cheese",
                                                     fields3 );
        
        assertNotSame( cheese1, cheese3 );
        assertNotSame( cheese1.getAllFieldTemplates(), cheese3.getAllFieldTemplates() );
        assertEquals( cheese1, cheese3 );
        assertEquals( cheese1.hashCode(), cheese3.hashCode() );        
    }
    
    public void testFacts() {
        Package pkg = new Package( "org.store" );
        FieldTemplate cheeseName = new FieldTemplateImpl( "name",
                                                          0,
                                                          String.class );
        FieldTemplate cheesePrice = new FieldTemplateImpl( "price",
                                                           1,
                                                           Integer.class );
        FieldTemplate[] fields1 = new FieldTemplate[]{cheeseName, cheesePrice};
        FactTemplate cheese1 = new FactTemplateImpl( pkg,
                                                     "Cheese",
                                                     fields1 );        
        
        Fact stilton1 = cheese1.createFact( 10 );
        stilton1.setFieldValue( "name", "stilton" );
        stilton1.setFieldValue( "price", new Integer( 200 ) );

        Fact stilton2 = cheese1.createFact( 11 );
        stilton2.setFieldValue( 0, "stilton" );
        stilton2.setFieldValue( 1, new Integer( 200 ) );
        
        assertEquals( stilton1, stilton2 );
        assertEquals( stilton1.hashCode(), stilton2.hashCode() );
        
        Fact brie = cheese1.createFact( 12 );
        brie.setFieldValue( "name", "brie" );
        brie.setFieldValue( "price", new Integer( 55 ) );
        
        assertFalse( stilton1.equals( brie ));
        assertFalse( stilton1.hashCode() ==  brie.hashCode() );
        
    }
}
