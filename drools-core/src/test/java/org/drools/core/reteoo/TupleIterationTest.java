/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.reteoo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.core.common.PropagationContext;
import org.drools.core.reteoo.TupleIterator.OnLeaf;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class TupleIterationTest {
    @Test
    public void testRootTraversal() {
        LeftTuple t0 = new LeftTuple();
        LeftTuple t1 = new LeftTuple(t0, null, (PropagationContext) null, true);
        LeftTuple t2 = new LeftTuple(t0, null, (PropagationContext) null,true);
        LeftTuple t3 = new LeftTuple(t0, null, (PropagationContext) null,true);
        
        LeftTuple t1_1 = new LeftTuple(t1, null, (PropagationContext) null,true);
        LeftTuple t1_2 = new LeftTuple(t1, null, (PropagationContext) null,true);
        
        LeftTuple t1_1_1 = new LeftTuple(t1_1, null, (PropagationContext) null,true);
        LeftTuple t1_1_1_1 = new LeftTuple(t1_1_1, null, (PropagationContext) null,true);
        LeftTuple t1_1_1_2 = new LeftTuple(t1_1_1, null, (PropagationContext) null,true);
        LeftTuple t1_1_1_3 = new LeftTuple(t1_1_1, null, (PropagationContext) null,true);
        
        LeftTuple t1_2_1 = new LeftTuple(t1_2, null, (PropagationContext) null,true);
        
        LeftTuple t1_2_2 = new LeftTuple(t1_2, null, (PropagationContext) null,true);
        LeftTuple t1_2_2_1 = new LeftTuple(t1_2_2, null,(PropagationContext) null, true);

        LeftTuple t1_2_3 = new LeftTuple(t1_2, null, (PropagationContext) null,true);

        LeftTuple t2_1 = new LeftTuple(t2, null, (PropagationContext) null,true);
        LeftTuple t2_2 = new LeftTuple(t2, null,(PropagationContext) null, true);
        LeftTuple t2_3 = new LeftTuple(t2, null, (PropagationContext) null,true);
        
        LeftTuple t2_3_1 = new LeftTuple(t2_3, null, (PropagationContext) null,true);
        LeftTuple t2_3_1_1 = new LeftTuple(t2_3_1, null, (PropagationContext) null,true);
        
        LeftTuple t2_3_2 = new LeftTuple(t2_3, null, (PropagationContext) null,true);

        
        LeftTuple[] leafs = new LeftTuple[] {
t1_1_1_1,  t1_1_1_2, t1_1_1_3, t1_2_1, t1_2_2_1, t1_2_3, t2_1, t2_2, t2_3_1_1, t2_3_2, t3                  
        };

        final List<TupleImpl> foundLeafs = new ArrayList<TupleImpl>();
        
        TupleIterator iterator = new TupleIterator();
        OnLeaf onLeaf = new OnLeaf() {

            public void execute(TupleImpl leafLeftTuple) {
                foundLeafs.add( leafLeftTuple );
            }
            
        };
        
        iterator.traverse( t0, t0, onLeaf );

        assertThat(foundLeafs).hasSameSizeAs(leafs);
        assertThat(foundLeafs).isEqualTo(Arrays.asList(leafs));
    }
    
    @Test
    public void testMidTraversal() {
        LeftTuple tm2 = new LeftTuple();
        LeftTuple tm1 = new LeftTuple(tm2, null, (PropagationContext) null,true);
        LeftTuple tm1_1 = new LeftTuple(tm1, null, (PropagationContext) null, true); // this leaf will not be included
        
        LeftTuple t0 = new LeftTuple(tm1, null, (PropagationContext) null,true); // insert two nodes before our root traversal position
        
        
        LeftTuple t1 = new LeftTuple(t0, null, (PropagationContext) null,true);
        LeftTuple t2 = new LeftTuple(t0, null, (PropagationContext) null,true);
        LeftTuple t3 = new LeftTuple(t0, null, (PropagationContext) null,true);
        
        LeftTuple t1_1 = new LeftTuple(t1, null, (PropagationContext) null,true);
        LeftTuple t1_2 = new LeftTuple(t1, null, (PropagationContext) null,true);
        
        LeftTuple t1_1_1 = new LeftTuple(t1_1, null, (PropagationContext) null,true);
        LeftTuple t1_1_1_1 = new LeftTuple(t1_1_1, null, (PropagationContext) null,true);
        LeftTuple t1_1_1_2 = new LeftTuple(t1_1_1, null, (PropagationContext) null,true);
        LeftTuple t1_1_1_3 = new LeftTuple(t1_1_1, null, (PropagationContext) null,true);
        
        LeftTuple t1_2_1 = new LeftTuple(t1_2, null, (PropagationContext) null,true);
        
        LeftTuple t1_2_2 = new LeftTuple(t1_2, null, (PropagationContext) null,true);
        LeftTuple t1_2_2_1 = new LeftTuple(t1_2_2, null, (PropagationContext) null,true);

        LeftTuple t1_2_3 = new LeftTuple(t1_2, null, (PropagationContext) null,true);

        LeftTuple t2_1 = new LeftTuple(t2, null, (PropagationContext) null,true);
        LeftTuple t2_2 = new LeftTuple(t2, null, (PropagationContext) null,true);
        LeftTuple t2_3 = new LeftTuple(t2, null, (PropagationContext) null,true);
        
        LeftTuple t2_3_1 = new LeftTuple(t2_3, null, (PropagationContext) null,true);
        LeftTuple t2_3_1_1 = new LeftTuple(t2_3_1, null, (PropagationContext) null,true);
        
        LeftTuple t2_3_2 = new LeftTuple(t2_3, null, (PropagationContext) null,true);

        
        LeftTuple[] leafs = new LeftTuple[] {
t1_1_1_1,  t1_1_1_2, t1_1_1_3, t1_2_1, t1_2_2_1, t1_2_3, t2_1, t2_2, t2_3_1_1, t2_3_2, t3                  
        };

        final List<TupleImpl> foundLeafs = new ArrayList<>();
        
        TupleIterator iterator = new TupleIterator();
        OnLeaf onLeaf = new OnLeaf() {

            public void execute(TupleImpl leafLeftTuple) {
                foundLeafs.add( leafLeftTuple );
            }
            
        };
        
        iterator.traverse( t0, t0, onLeaf );

        assertThat(foundLeafs).hasSameSizeAs(leafs);
        assertThat(foundLeafs).isEqualTo(Arrays.asList(leafs));
    }
}
