package org.drools.core.util;

import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.JoinNodeLeftTuple;
import org.drools.core.test.model.Cheese;
import org.drools.core.util.index.TupleList;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RightTupleListTest {
    @Test
    public void testEmptyIterator() {
        final TupleList map = new TupleList();
        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        final InternalFactHandle h1 = new DefaultFactHandle( 1,
                                                             stilton1 );

        assertThat(map.getFirst(new JoinNodeLeftTuple( h1, null, true ))).isNull();
    }
}
