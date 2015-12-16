/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.core.reteoo.TupleIterator.OnLeaf;

import org.drools.core.spi.PropagationContext;
import org.junit.Test;
import static org.junit.Assert.*;


public class TupleIterationTest {
    @Test
    public void testRootTraversal() {
        LeftTupleImpl t0 = new LeftTupleImpl();
        LeftTupleImpl t1 = new LeftTupleImpl(t0, null, (PropagationContext) null, true);
        LeftTupleImpl t2 = new LeftTupleImpl(t0, null, (PropagationContext) null,true);
        LeftTupleImpl t3 = new LeftTupleImpl(t0, null, (PropagationContext) null,true);
        
        LeftTupleImpl t1_1 = new LeftTupleImpl(t1, null, (PropagationContext) null,true);
        LeftTupleImpl t1_2 = new LeftTupleImpl(t1, null, (PropagationContext) null,true);
        
        LeftTupleImpl t1_1_1 = new LeftTupleImpl(t1_1, null, (PropagationContext) null,true);
        LeftTupleImpl t1_1_1_1 = new LeftTupleImpl(t1_1_1, null, (PropagationContext) null,true);
        LeftTupleImpl t1_1_1_2 = new LeftTupleImpl(t1_1_1, null, (PropagationContext) null,true);
        LeftTupleImpl t1_1_1_3 = new LeftTupleImpl(t1_1_1, null, (PropagationContext) null,true);
        
        LeftTupleImpl t1_2_1 = new LeftTupleImpl(t1_2, null, (PropagationContext) null,true);
        
        LeftTupleImpl t1_2_2 = new LeftTupleImpl(t1_2, null, (PropagationContext) null,true);
        LeftTupleImpl t1_2_2_1 = new LeftTupleImpl(t1_2_2, null,(PropagationContext) null, true);

        LeftTupleImpl t1_2_3 = new LeftTupleImpl(t1_2, null, (PropagationContext) null,true);

        LeftTupleImpl t2_1 = new LeftTupleImpl(t2, null, (PropagationContext) null,true);
        LeftTupleImpl t2_2 = new LeftTupleImpl(t2, null,(PropagationContext) null, true);
        LeftTupleImpl t2_3 = new LeftTupleImpl(t2, null, (PropagationContext) null,true);
        
        LeftTupleImpl t2_3_1 = new LeftTupleImpl(t2_3, null, (PropagationContext) null,true);
        LeftTupleImpl t2_3_1_1 = new LeftTupleImpl(t2_3_1, null, (PropagationContext) null,true);
        
        LeftTupleImpl t2_3_2 = new LeftTupleImpl(t2_3, null, (PropagationContext) null,true);

        
        LeftTupleImpl[] leafs = new LeftTupleImpl[] {
t1_1_1_1,  t1_1_1_2, t1_1_1_3, t1_2_1, t1_2_2_1, t1_2_3, t2_1, t2_2, t2_3_1_1, t2_3_2, t3                  
        };

        final List<LeftTuple> foundLeafs = new ArrayList<LeftTuple>();
        
        TupleIterator iterator = new TupleIterator();
        OnLeaf onLeaf = new OnLeaf() {

            public void execute(LeftTuple leafLeftTuple) {
                foundLeafs.add( leafLeftTuple );
            }
            
        };
        
        iterator.traverse( t0, t0, onLeaf );
        
        assertEquals( leafs.length, foundLeafs.size() );
        assertEquals( Arrays.asList( leafs ), foundLeafs );
    }
    
    @Test
    public void testMidTraversal() {
        LeftTupleImpl tm2 = new LeftTupleImpl();
        LeftTupleImpl tm1 = new LeftTupleImpl(tm2, null, (PropagationContext) null,true);
        LeftTuple tm1_1 = new LeftTupleImpl(tm1, null, (PropagationContext) null,true); // this leaf will not be included
        
        LeftTupleImpl t0 = new LeftTupleImpl(tm1, null, (PropagationContext) null,true); // insert two nodes before our root traversal position
        
        
        LeftTupleImpl t1 = new LeftTupleImpl(t0, null, (PropagationContext) null,true);
        LeftTupleImpl t2 = new LeftTupleImpl(t0, null, (PropagationContext) null,true);
        LeftTupleImpl t3 = new LeftTupleImpl(t0, null, (PropagationContext) null,true);
        
        LeftTupleImpl t1_1 = new LeftTupleImpl(t1, null, (PropagationContext) null,true);
        LeftTupleImpl t1_2 = new LeftTupleImpl(t1, null, (PropagationContext) null,true);
        
        LeftTupleImpl t1_1_1 = new LeftTupleImpl(t1_1, null, (PropagationContext) null,true);
        LeftTupleImpl t1_1_1_1 = new LeftTupleImpl(t1_1_1, null, (PropagationContext) null,true);
        LeftTupleImpl t1_1_1_2 = new LeftTupleImpl(t1_1_1, null, (PropagationContext) null,true);
        LeftTupleImpl t1_1_1_3 = new LeftTupleImpl(t1_1_1, null, (PropagationContext) null,true);
        
        LeftTupleImpl t1_2_1 = new LeftTupleImpl(t1_2, null, (PropagationContext) null,true);
        
        LeftTupleImpl t1_2_2 = new LeftTupleImpl(t1_2, null, (PropagationContext) null,true);
        LeftTupleImpl t1_2_2_1 = new LeftTupleImpl(t1_2_2, null, (PropagationContext) null,true);

        LeftTupleImpl t1_2_3 = new LeftTupleImpl(t1_2, null, (PropagationContext) null,true);

        LeftTupleImpl t2_1 = new LeftTupleImpl(t2, null, (PropagationContext) null,true);
        LeftTupleImpl t2_2 = new LeftTupleImpl(t2, null, (PropagationContext) null,true);
        LeftTupleImpl t2_3 = new LeftTupleImpl(t2, null, (PropagationContext) null,true);
        
        LeftTupleImpl t2_3_1 = new LeftTupleImpl(t2_3, null, (PropagationContext) null,true);
        LeftTupleImpl t2_3_1_1 = new LeftTupleImpl(t2_3_1, null, (PropagationContext) null,true);
        
        LeftTupleImpl t2_3_2 = new LeftTupleImpl(t2_3, null, (PropagationContext) null,true);

        
        LeftTupleImpl[] leafs = new LeftTupleImpl[] {
t1_1_1_1,  t1_1_1_2, t1_1_1_3, t1_2_1, t1_2_2_1, t1_2_3, t2_1, t2_2, t2_3_1_1, t2_3_2, t3                  
        };

        final List<LeftTuple> foundLeafs = new ArrayList<LeftTuple>();
        
        TupleIterator iterator = new TupleIterator();
        OnLeaf onLeaf = new OnLeaf() {

            public void execute(LeftTuple leafLeftTuple) {
                foundLeafs.add( leafLeftTuple );
            }
            
        };
        
        iterator.traverse( t0, t0, onLeaf );
        
        assertEquals( leafs.length, foundLeafs.size() );
        assertEquals( Arrays.asList( leafs ), foundLeafs );
    }
}
