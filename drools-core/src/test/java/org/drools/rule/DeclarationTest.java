package org.drools.rule;

import junit.framework.TestCase;

import org.drools.rule.ConstraintTest.Cheese;
import org.drools.spi.ClassObjectType;
import org.drools.spi.Extractor;
import org.drools.spi.ObjectType;

public class DeclarationTest extends TestCase {

    public void testDeclaration(){
        ObjectType stringObjectType = new ClassObjectType( String.class );

        /* Determines how the bound value is extracted from the column */
        Extractor typeOfCheeseExtractor = new Extractor() {
            public Object getValue(Object object){
                return ((Cheese) object).getType();
            }
        };

        /* Bind the extractor to a decleration */
        /* Declarations know the column they derive their value form */
        Declaration declaration = new Declaration( 3,
                                                   "typeOfCheese",
                                                   stringObjectType,
                                                   typeOfCheeseExtractor,
                                                   5 );
        assertEquals( 3,
                      declaration.getIndex() );

        assertEquals( "typeOfCheese",
                      declaration.getIdentifier() );

        assertSame( stringObjectType,
                    declaration.getObjectType() );

        assertSame( typeOfCheeseExtractor,
                    declaration.getExtractor() );

        assertEquals( 5,
                      declaration.getColumn() );

    }

    public void testGetFieldValue(){
        ObjectType stringObjectType = new ClassObjectType( String.class );

        /* Determines how the bound value is extracted from the column */
        Extractor typeOfCheeseExtractor = new Extractor() {
            public Object getValue(Object object){
                return ((Cheese) object).getType();
            }
        };

        /* Bind the extractor to a decleration */
        /* Declarations know the column they derive their value form */
        Declaration typeOfCheeseDeclaration = new Declaration( 0,
                                                               "typeOfCheese",
                                                               stringObjectType,
                                                               typeOfCheeseExtractor,
                                                               0 );

        /* Create some facts */
        Cheese cheddar = new Cheese( "cheddar",
                                     5 );

        /* Check we can extract Declarations correctly */
        assertEquals( "cheddar",
                      typeOfCheeseDeclaration.getValue( cheddar ) );
    }

}
