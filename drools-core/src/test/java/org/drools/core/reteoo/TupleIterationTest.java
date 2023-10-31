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
        JoinNodeLeftTuple t0 = new JoinNodeLeftTuple();
        JoinNodeLeftTuple t1 = new JoinNodeLeftTuple(t0, null, (PropagationContext) null, true);
        JoinNodeLeftTuple t2 = new JoinNodeLeftTuple(t0, null, (PropagationContext) null,true);
        JoinNodeLeftTuple t3 = new JoinNodeLeftTuple(t0, null, (PropagationContext) null,true);
        
        JoinNodeLeftTuple t1_1 = new JoinNodeLeftTuple(t1, null, (PropagationContext) null,true);
        JoinNodeLeftTuple t1_2 = new JoinNodeLeftTuple(t1, null, (PropagationContext) null,true);
        
        JoinNodeLeftTuple t1_1_1 = new JoinNodeLeftTuple(t1_1, null, (PropagationContext) null,true);
        JoinNodeLeftTuple t1_1_1_1 = new JoinNodeLeftTuple(t1_1_1, null, (PropagationContext) null,true);
        JoinNodeLeftTuple t1_1_1_2 = new JoinNodeLeftTuple(t1_1_1, null, (PropagationContext) null,true);
        JoinNodeLeftTuple t1_1_1_3 = new JoinNodeLeftTuple(t1_1_1, null, (PropagationContext) null,true);
        
        JoinNodeLeftTuple t1_2_1 = new JoinNodeLeftTuple(t1_2, null, (PropagationContext) null,true);
        
        JoinNodeLeftTuple t1_2_2 = new JoinNodeLeftTuple(t1_2, null, (PropagationContext) null,true);
        JoinNodeLeftTuple t1_2_2_1 = new JoinNodeLeftTuple(t1_2_2, null,(PropagationContext) null, true);

        JoinNodeLeftTuple t1_2_3 = new JoinNodeLeftTuple(t1_2, null, (PropagationContext) null,true);

        JoinNodeLeftTuple t2_1 = new JoinNodeLeftTuple(t2, null, (PropagationContext) null,true);
        JoinNodeLeftTuple t2_2 = new JoinNodeLeftTuple(t2, null,(PropagationContext) null, true);
        JoinNodeLeftTuple t2_3 = new JoinNodeLeftTuple(t2, null, (PropagationContext) null,true);
        
        JoinNodeLeftTuple t2_3_1 = new JoinNodeLeftTuple(t2_3, null, (PropagationContext) null,true);
        JoinNodeLeftTuple t2_3_1_1 = new JoinNodeLeftTuple(t2_3_1, null, (PropagationContext) null,true);
        
        JoinNodeLeftTuple t2_3_2 = new JoinNodeLeftTuple(t2_3, null, (PropagationContext) null,true);

        
        JoinNodeLeftTuple[] leafs = new JoinNodeLeftTuple[] {
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

        assertThat(foundLeafs).hasSameSizeAs(leafs);
        assertThat(foundLeafs).isEqualTo(Arrays.asList(leafs));
    }
    
    @Test
    public void testMidTraversal() {
        JoinNodeLeftTuple tm2 = new JoinNodeLeftTuple();
        JoinNodeLeftTuple tm1 = new JoinNodeLeftTuple(tm2, null, (PropagationContext) null,true);
        LeftTuple tm1_1 = new JoinNodeLeftTuple(tm1, null, (PropagationContext) null, true); // this leaf will not be included
        
        JoinNodeLeftTuple t0 = new JoinNodeLeftTuple(tm1, null, (PropagationContext) null,true); // insert two nodes before our root traversal position
        
        
        JoinNodeLeftTuple t1 = new JoinNodeLeftTuple(t0, null, (PropagationContext) null,true);
        JoinNodeLeftTuple t2 = new JoinNodeLeftTuple(t0, null, (PropagationContext) null,true);
        JoinNodeLeftTuple t3 = new JoinNodeLeftTuple(t0, null, (PropagationContext) null,true);
        
        JoinNodeLeftTuple t1_1 = new JoinNodeLeftTuple(t1, null, (PropagationContext) null,true);
        JoinNodeLeftTuple t1_2 = new JoinNodeLeftTuple(t1, null, (PropagationContext) null,true);
        
        JoinNodeLeftTuple t1_1_1 = new JoinNodeLeftTuple(t1_1, null, (PropagationContext) null,true);
        JoinNodeLeftTuple t1_1_1_1 = new JoinNodeLeftTuple(t1_1_1, null, (PropagationContext) null,true);
        JoinNodeLeftTuple t1_1_1_2 = new JoinNodeLeftTuple(t1_1_1, null, (PropagationContext) null,true);
        JoinNodeLeftTuple t1_1_1_3 = new JoinNodeLeftTuple(t1_1_1, null, (PropagationContext) null,true);
        
        JoinNodeLeftTuple t1_2_1 = new JoinNodeLeftTuple(t1_2, null, (PropagationContext) null,true);
        
        JoinNodeLeftTuple t1_2_2 = new JoinNodeLeftTuple(t1_2, null, (PropagationContext) null,true);
        JoinNodeLeftTuple t1_2_2_1 = new JoinNodeLeftTuple(t1_2_2, null, (PropagationContext) null,true);

        JoinNodeLeftTuple t1_2_3 = new JoinNodeLeftTuple(t1_2, null, (PropagationContext) null,true);

        JoinNodeLeftTuple t2_1 = new JoinNodeLeftTuple(t2, null, (PropagationContext) null,true);
        JoinNodeLeftTuple t2_2 = new JoinNodeLeftTuple(t2, null, (PropagationContext) null,true);
        JoinNodeLeftTuple t2_3 = new JoinNodeLeftTuple(t2, null, (PropagationContext) null,true);
        
        JoinNodeLeftTuple t2_3_1 = new JoinNodeLeftTuple(t2_3, null, (PropagationContext) null,true);
        JoinNodeLeftTuple t2_3_1_1 = new JoinNodeLeftTuple(t2_3_1, null, (PropagationContext) null,true);
        
        JoinNodeLeftTuple t2_3_2 = new JoinNodeLeftTuple(t2_3, null, (PropagationContext) null,true);

        
        JoinNodeLeftTuple[] leafs = new JoinNodeLeftTuple[] {
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

        assertThat(foundLeafs).hasSameSizeAs(leafs);
        assertThat(foundLeafs).isEqualTo(Arrays.asList(leafs));
    }
}
