/**
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

package org.drools.util;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.Cheese;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.core.util.RightTupleList;
import org.drools.reteoo.LeftTuple;

public class RightTupleListTest {
    @Test
    public void testEmptyIterator() {
        final RightTupleList map = new RightTupleList();
        final Cheese stilton1 = new Cheese( "stilton",
                                            35 );
        final InternalFactHandle h1 = new DefaultFactHandle( 1,
                                                             stilton1 );                        
        
        assertNull( map.getFirst( new LeftTuple( h1, null,
                                                 true ), null ) );
    }
}
