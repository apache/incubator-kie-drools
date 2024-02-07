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
package org.drools.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.consequence.ConflictResolver;
import org.drools.base.rule.consequence.Consequence;
import org.drools.core.common.ActivationGroupNode;
import org.drools.core.common.ActivationNode;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.core.common.PropagationContext;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.Tuple;
import org.drools.core.reteoo.TupleImpl;
import org.drools.core.rule.consequence.InternalMatch;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Thes test class uses auxiliary test classes in org.kie.util:
 * Group and Item as a mock-up for the corresponding Agenda classes.
 * 
 * The test testShuffled uses a sequence of shuffled Item arrays, inserts
 * them in the "random" order, retracts and reinserts the Items at an even
 * position. Finally, Items are retrieved and the order is checked.
 * 
 * Experience has shown that at least 6 Items are required to demonstrate
 * a certain bug, so don't reduce the max parameter.
 * 
 */
public class BinaryHeapQueueTest {

    private List<Integer[]>  perms = new ArrayList<Integer[]>();
    private final static int max   = 20;

    // NOT really permutations, just some shuffling
    private void shuffle(Integer[] a,
                         int lim) {
        if ( lim == 0 ) {
            Integer[] p = a.clone();
            perms.add( p );
        } else {
            shuffle( a,
                     lim - 1 );
            Integer h = a[lim];
            a[lim] = a[lim - 1];
            a[lim - 1] = h;
            shuffle( a,
                     lim - 1 );
        }
    }

    @Test
    public void testShuffled() {

        long time = System.currentTimeMillis();
        for (int k = 0; k < 10; k++) {
            for (Integer[] perm : perms) {
                Group group = new Group("group");

                for (Integer i : perm) {
                    Item item = new Item(group,
                                         i);
                    group.add(item);
                }

                InternalMatch[] elems = group.getQueue().toArray(new InternalMatch[0]);
                for (InternalMatch elem : elems) {
                    Item item = (Item) elem;
                    //        System.out.print( " " + item.getSalience() + "/"  + item.getActivationNumber() + "/" + item.getQueueIndex() );
                    if (item.getQueueIndex() % 2 == 0) {
                        group.remove(item);
                        group.add(item);
                    }
                }
                boolean ok = true;
                StringBuilder sb = new StringBuilder("queue:");
                for (int i = max - 1; i >= 0; i--) {
                    int sal = group.getNext().getSalience();
                    sb.append(" ").append(sal);
                    if (sal != i) {
                        ok = false;
                    }
                }
                assertThat(ok).as("incorrect order in " + sb.toString()).isTrue();
                //      System.out.println( sb.toString() );
            }
        }
        System.out.println("time:" + (System.currentTimeMillis() - time));
    }

    @Before
    public void setup() {
        System.out.println( "Running setup" );
        Integer[] a = new Integer[max];
        for ( int i = 0; i < max; i++ ) {
            a[i] = i;
        }
        shuffle( a,max - 1 );
        //    System.out.println( "The size is " + perms.size() );
    }

    public static class Group {

        private static final long serialVersionUID = 510l;

        private String            name;

        /** Items in the agenda. */
        private Queue<Item>      queue;

        /**
         * Construct an <code>AgendaGroup</code> with the given name.
         *
         * The <AgendaGroup> name.
         */
        public Group() {
        }

        public Group(final String name) {
            this.name = name;
            this.queue = QueueFactory.createQueue(ItemConflictResolver.INSTANCE );
        }

        public String getName() {
            return this.name;
        }

        public void clear() {
            this.queue.clear();
        }

        /* (non-Javadoc)
         * @see org.kie.spi.AgendaGroup#size()
         */
        public int size() {
            return this.queue.size();
        }

        public void add(final Item item) {
            this.queue.enqueue(item);
        }

        public Item getNext() {
            return (Item) this.queue.dequeue();
        }

        /**
         * Iterates a PriorityQueue removing empty entries until it finds a populated entry and return true,
         * otherwise it returns false;
         *
         * @return
         */
        public boolean isEmpty() {
            return this.queue.isEmpty();
        }

        public String toString() {
            return "AgendaGroup '" + this.name + "'";
        }

        public boolean equals(final Object object) {
            if (!(object instanceof Group)) {
                return false;
            }

            if ( ((Group) object).name.equals( this.name ) ) {
                return true;
            }

            return false;
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public void remove(Item agendaItem) {
            this.queue.dequeue( agendaItem );
        }

        public Collection<Item> getQueue() {
            return this.queue.getAll();
        }
    }


    public static class Item implements InternalMatch {

        private static int actNo = 1;

        private int   index;
        private long  activationNumber;
        private Group group;
        private int   salience;

        public Item(Group group,
                    int salience) {
            this.group = group;
            this.salience = salience;
            this.activationNumber = actNo++;
        }

        public void dequeue() {
            if (this.group != null) {
                this.group.remove(this);
            }
            this.index = -1;
        }

        public void setQueueIndex(int index) {
            this.index = index;
        }

        public int getQueueIndex() {
            return index;
        }

        public int getSalience() {
            return salience;
        }

        public long getActivationNumber() {
            return activationNumber;
        }

        public ActivationGroupNode getActivationGroupNode() {
            return null;
        }

        public ActivationNode getActivationNode() {
            return null;
        }

        public InternalAgendaGroup getAgendaGroup() {
            return null;
        }

        public InternalRuleFlowGroup getRuleFlowGroup() {
            return null;
        }

        public PropagationContext getPropagationContext() {
            return null;
        }

        public RuleImpl getRule() {
            return null;
        }

        public Consequence getConsequence() {
            // TODO Auto-generated method stub
            return null;
        }

        public TupleImpl getTuple() {
            return null;
        }

        public boolean isQueued() {
            return false;
        }

        public void remove() {
        }

        public void setQueued(boolean arg0) {
        }

        public void setActivationGroupNode(ActivationGroupNode arg0) {
        }

        public void setActivationNode(ActivationNode arg0) {
        }

        public List<String> getDeclarationIds() {
            return null;
        }

        public Object getDeclarationValue(String arg0) {
            return null;
        }

        public List<FactHandle> getFactHandles() {
            return null;
        }

        public List<Object> getObjects() {
            return null;
        }

        public InternalFactHandle getActivationFactHandle() {
            return null;
        }

        public boolean isAdded() {
            return false;
        }

        public boolean isMatched() {
            return false;
        }

        public void setMatched(boolean matched) { }

        public boolean isActive() {
            return false;
        }

        public void setActive(boolean active) { }

        @Override
        public RuleAgendaItem getRuleAgendaItem() {
            return null;
        }

        @Override
        public void setActivationFactHandle(InternalFactHandle factHandle) {

        }

        @Override
        public TerminalNode getTerminalNode() {
            return null;
        }

        @Override
        public String toExternalForm() {
            return null;
        }

        @Override
        public Runnable getCallback() {
            return null;
        }

        @Override
        public void setCallback(Runnable callback) {

        }
    }

    public static class ItemConflictResolver
            implements
            ConflictResolver<Item> {

        private static final long                 serialVersionUID = 1L;
        public static final  ItemConflictResolver INSTANCE         = new ItemConflictResolver();

        public static ItemConflictResolver getInstance() {
            return ItemConflictResolver.INSTANCE;
        }

        public final int compare(final Item existing,
                                 final Item adding) {
            final int s1 = existing.getSalience();
            final int s2 = adding.getSalience();

            if (s1 != s2) {
                return s1 - s2;
            }

            // we know that no two activations will have the same number
            return (int) (existing.getActivationNumber() - adding.getActivationNumber());
        }

    }
}
