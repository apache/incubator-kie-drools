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

}
