/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util;

import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.test.model.Cheese;
import org.drools.core.util.index.TupleList;
import org.junit.Test;

import static org.junit.Assert.assertNull;

public class RightTupleListTest {
    @Test
    public void testEmptyIterator() {
        final TupleList map = new TupleList();
        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        final InternalFactHandle h1 = new DefaultFactHandle( 1,
                                                             stilton1 );
        
        assertNull( map.getFirst( new LeftTupleImpl( h1, null, true ) ) );
    }
}
