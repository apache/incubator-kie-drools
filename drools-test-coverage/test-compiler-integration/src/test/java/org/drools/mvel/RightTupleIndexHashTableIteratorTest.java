/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RightTupleImpl;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.util.AbstractHashTable;
import org.drools.core.util.Entry;
import org.drools.core.util.Iterator;
import org.drools.core.util.index.TupleIndexHashTable;
import org.drools.core.util.index.TupleIndexHashTable.FieldIndexHashTableFullIterator;
import org.drools.core.util.index.TupleList;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RightTupleIndexHashTableIteratorTest extends BaseTupleIndexHashTableIteratorTest {

    public RightTupleIndexHashTableIteratorTest(boolean useLambdaConstraint) {
        this.useLambdaConstraint = useLambdaConstraint;
    }

    @Test
    public void test1() {
        BetaNodeFieldConstraint constraint0 = createFooThisEqualsDBetaConstraint(useLambdaConstraint);

        BetaNodeFieldConstraint[] constraints = new BetaNodeFieldConstraint[]{constraint0};

        RuleBaseConfiguration config = new RuleBaseConfiguration();

        BetaConstraints betaConstraints = null;

        betaConstraints = new SingleBetaConstraints(constraints, config);

        BetaMemory betaMemory = betaConstraints.createBetaMemory(config, NodeTypeEnums.JoinNode);

        KieBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        KieSession ss = kBase.newKieSession();

        InternalFactHandle fh1 = (InternalFactHandle) ss.insert(new Foo("brie", 1));
        InternalFactHandle fh2 = (InternalFactHandle) ss.insert(new Foo("brie", 1));
        InternalFactHandle fh3 = (InternalFactHandle) ss.insert(new Foo("soda", 1));
        InternalFactHandle fh4 = (InternalFactHandle) ss.insert(new Foo("soda", 1));
        InternalFactHandle fh5 = (InternalFactHandle) ss.insert(new Foo("bread", 3));
        InternalFactHandle fh6 = (InternalFactHandle) ss.insert(new Foo("bread", 3));
        InternalFactHandle fh7 = (InternalFactHandle) ss.insert(new Foo("cream", 3));
        InternalFactHandle fh8 = (InternalFactHandle) ss.insert(new Foo("gorda", 15));
        InternalFactHandle fh9 = (InternalFactHandle) ss.insert(new Foo("beer", 16));

        InternalFactHandle fh10 = (InternalFactHandle) ss.insert(new Foo("mars", 0));
        InternalFactHandle fh11 = (InternalFactHandle) ss.insert(new Foo("snicker", 0));
        InternalFactHandle fh12 = (InternalFactHandle) ss.insert(new Foo("snicker", 0));
        InternalFactHandle fh13 = (InternalFactHandle) ss.insert(new Foo("snicker", 0));

        betaMemory.getRightTupleMemory().add(new RightTupleImpl(fh1, null));
        betaMemory.getRightTupleMemory().add(new RightTupleImpl(fh2, null));
        betaMemory.getRightTupleMemory().add(new RightTupleImpl(fh3, null));
        betaMemory.getRightTupleMemory().add(new RightTupleImpl(fh4, null));
        betaMemory.getRightTupleMemory().add(new RightTupleImpl(fh5, null));
        betaMemory.getRightTupleMemory().add(new RightTupleImpl(fh6, null));
        betaMemory.getRightTupleMemory().add(new RightTupleImpl(fh7, null));
        betaMemory.getRightTupleMemory().add(new RightTupleImpl(fh8, null));
        betaMemory.getRightTupleMemory().add(new RightTupleImpl(fh9, null));

        TupleIndexHashTable hashTable = (TupleIndexHashTable) betaMemory.getRightTupleMemory();
        // can't create a 0 hashCode, so forcing 
        TupleList rightTupleList = new TupleList();
        rightTupleList.add(new RightTupleImpl(fh10, null));
        hashTable.getTable()[0] = rightTupleList;
        rightTupleList = new TupleList();
        rightTupleList.add(new RightTupleImpl(fh11, null));
        rightTupleList.add(new RightTupleImpl(fh12, null));
        rightTupleList.add(new RightTupleImpl(fh13, null));
        ((TupleList) hashTable.getTable()[0]).setNext(rightTupleList);

        List tableIndexList = createTableIndexListForAssertion(hashTable);
        assertThat(tableIndexList.size()).isEqualTo(5);

        // This tests the hashcode index allocation. If the rehash function (or any other way hashcodes are computed) changes, these numbers will change.
        // standard-drl and exec-model have different getRightExtractor().getIndex() value so hashCode changes
        if (useLambdaConstraint) {
            assertTableIndex(tableIndexList, 0, 0, 3);
            assertTableIndex(tableIndexList, 1, 49, 3);
            assertTableIndex(tableIndexList, 2, 51, 3);
            assertTableIndex(tableIndexList, 3, 60, 2);
            assertTableIndex(tableIndexList, 4, 61, 2);
        } else {
            assertTableIndex(tableIndexList, 0, 0, 3);
            assertTableIndex(tableIndexList, 1, 102, 2);
            assertTableIndex(tableIndexList, 2, 103, 2);
            assertTableIndex(tableIndexList, 3, 115, 3);
            assertTableIndex(tableIndexList, 4, 117, 3);
        }

        List resultList = new ArrayList<LeftTupleImpl>();
        Iterator it = betaMemory.getRightTupleMemory().iterator();
        for (RightTuple rightTuple = (RightTuple) it.next(); rightTuple != null; rightTuple = (RightTuple) it.next()) {
            resultList.add(rightTuple);
        }

        assertThat(resultList.size()).isEqualTo(13);
    }

    @Test
    public void testLastBucketInTheTable() {
        // JBRULES-2574
        // setup the entry array with an element in the first bucket, one 
        // in the middle and one in the last bucket
        Entry[] entries = new Entry[10];
        entries[0] = mock(TupleList.class);
        entries[5] = mock(TupleList.class);
        entries[9] = mock(TupleList.class);

        RightTuple[] tuples = new RightTuple[]{mock(RightTuple.class), mock(RightTuple.class), mock(RightTuple.class)};

        // set return values for methods
        when(entries[0].getNext()).thenReturn(null);
        when(((TupleList) entries[0]).getFirst()).thenReturn(tuples[0]);

        when(entries[5].getNext()).thenReturn(null);
        when(((TupleList) entries[5]).getFirst()).thenReturn(tuples[1]);

        when(entries[9].getNext()).thenReturn(null);
        when(((TupleList) entries[9]).getFirst()).thenReturn(tuples[2]);

        // create the mock table for the iterator
        AbstractHashTable table = mock(AbstractHashTable.class);
        when(table.getTable()).thenReturn(entries);

        // create the iterator
        FieldIndexHashTableFullIterator iterator = new FieldIndexHashTableFullIterator(table);

        // test it
        assertThat(iterator.next()).isSameAs(tuples[0]);
        assertThat(iterator.next()).isSameAs(tuples[1]);
        assertThat(iterator.next()).isSameAs(tuples[2]);
        assertThat(iterator.next()).isNull();
    }
}
