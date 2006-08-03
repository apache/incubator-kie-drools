package org.drools.lang.descr;

import junit.framework.TestCase;

public class AndDescrTest extends TestCase {

    public void testAddUnboundColumnsEtc() {
        final AndDescr and = new AndDescr();
        and.addDescr( new NotDescr() );
        and.addDescr( new ColumnDescr( "Foo" ) );
        and.addDescr( new NotDescr() );

        assertEquals( 3,
                      and.getDescrs().size() );
    }

    /** This is for combining bound columns with the same name patterns together */
    public void testAddBoundCols() {
        final AndDescr and = new AndDescr();
        final ColumnDescr col1 = new ColumnDescr( "Foo" );
        col1.setIdentifier( "foo" );        
        FieldConstraintDescr fieldConstraint1 = new FieldConstraintDescr("foo" );
        fieldConstraint1.addRestriction( new VariableRestrictionDescr("==", "bar") );        
        col1.addDescr( fieldConstraint1 );                
        and.addDescr( col1 );

        final ColumnDescr col2 = new ColumnDescr( "Foo" );
        col2.setIdentifier( "foo" );
        FieldConstraintDescr fieldConstraint2 = new FieldConstraintDescr("bar" );
        fieldConstraint2.addRestriction( new VariableRestrictionDescr("==", "baz") );        
        col2.addDescr( fieldConstraint2 );
        and.addDescr( col2 );

        and.addDescr( new NotDescr() );

        final ColumnDescr col3 = new ColumnDescr( "Foo" );
        and.addDescr( col3 ); //will not be combined, as not bound

        final ColumnDescr col4 = new ColumnDescr( "Bar" );
        col4.setIdentifier( "foo" );
        and.addDescr( col4 ); //even though has a name, should be left alone

        assertEquals( 4,
                      and.getDescrs().size() );
        assertEquals( col1,
                      and.getDescrs().get( 0 ) );
        assertTrue( and.getDescrs().get( 1 ) instanceof NotDescr );
        assertEquals( col3,
                      and.getDescrs().get( 2 ) );
        assertEquals( col4,
                      and.getDescrs().get( 3 ) );

        assertEquals( 2,
                      col1.getDescrs().size() );
        assertEquals( "bar",
                      ( (VariableRestrictionDescr)( (FieldConstraintDescr) col1.getDescrs().get( 0 )).getRestrictions().get( 0 )).getIdentifier() );                     
        assertEquals( "baz",
                      ( (VariableRestrictionDescr)( (FieldConstraintDescr) col1.getDescrs().get( 1 )).getRestrictions().get( 0 )).getIdentifier() );
    }

}
