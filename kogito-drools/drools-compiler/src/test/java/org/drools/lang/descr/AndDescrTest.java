package org.drools.lang.descr;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AndDescrTest {

    @Test
    public void testAddUnboundPatternsEtc() {
        final AndDescr and = new AndDescr();
        and.addDescr( new NotDescr() );
        and.addDescr( new PatternDescr( "Foo" ) );
        and.addDescr( new NotDescr() );

        assertEquals( 3,
                      and.getDescrs().size() );
    }

}
