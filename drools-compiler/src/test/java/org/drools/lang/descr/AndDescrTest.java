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
        col1.addDescr( new VariableDescr( "foo",
                                               "==",
                                               "bar" ) );
        and.addDescr( col1 );

        final ColumnDescr col2 = new ColumnDescr( "Foo" );
        col2.setIdentifier( "foo" );
        col2.addDescr( new VariableDescr( "bar",
                                               "==",
                                               "baz" ) );
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
                      ((VariableDescr) col1.getDescrs().get( 0 )).getIdentifier() );
        assertEquals( "baz",
                      ((VariableDescr) col1.getDescrs().get( 1 )).getIdentifier() );
    }

}
