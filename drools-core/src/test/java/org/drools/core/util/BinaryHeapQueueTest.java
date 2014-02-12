package org.drools.core.util;

import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.spi.Consequence;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.core.common.ActivationGroupNode;
import org.drools.core.common.ActivationNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.rule.GroupElement;
import org.drools.core.spi.Activation;
import org.drools.core.spi.ConflictResolver;
import org.drools.core.spi.PropagationContext;

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
    private final static int max   = 6;

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

    @Before
    public void setup() {
        System.out.println( "Running setup" );
        Integer[] a = new Integer[max];
        for ( int i = 0; i < max; i++ ) {
            a[i] = i;
        }
        shuffle( a,
                 max - 1 );
        //    System.out.println( "The size is " + perms.size() );
    }

    @Test
    public void testShuffled() {

        for ( Integer[] perm : perms ) {
            Group group = new Group( "group" );

            for ( Integer i : perm ) {
                Item item = new Item( group,
                                      i );
                group.add( item );
            }

            Activation[] elems = group.getQueue();
            for (Activation elem : elems ) {
                Item item = (Item) elem;
                //        System.out.print( " " + item.getSalience() + "/"  + item.getActivationNumber() + "/" + item.getQueueIndex() );
                if ( item.getQueueIndex() % 2 == 0 ) {
                    group.remove( item );
                    group.add( item );
                }
            }
            boolean ok = true;
            StringBuilder sb = new StringBuilder( "queue:" );
            for ( int i = max - 1; i >= 0; i-- ) {
                int sal = group.getNext().getSalience();
                sb.append( " " ).append( sal );
                if ( sal != i ) ok = false;
            }
            assertTrue( "incorrect order in " + sb.toString(),
                        ok );
            //      System.out.println( sb.toString() );
        }
    }

    public static class Group {

        private static final long serialVersionUID = 510l;

        private String            name;

        /** Items in the agenda. */
        private BinaryHeapQueue   queue;

        /**
         * Construct an <code>AgendaGroup</code> with the given name.
         *
         * The <AgendaGroup> name.
         */
        public Group() {
        }

        public Group(final String name) {
            this.name = name;
            this.queue = new BinaryHeapQueue( ItemConflictResolver.INSTANCE );
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
            this.queue.enqueue( (Activation) item );
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

        public boolean equal(final Object object) {
            if ( (object == null) || !(object instanceof Group) ) {
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

        public Activation[] getQueue() {
            return (Activation[]) this.queue.toArray(new Activation[size()] );
        }
    }

    public static class IsTuple extends BaseMatcher<List<InternalFactHandle>> {
        private final InternalFactHandle[] expected;

        public IsTuple(List<InternalFactHandle> tupleAsList) {
            expected = tupleAsList.toArray( new InternalFactHandle[tupleAsList.size()] );
        }

        public IsTuple(InternalFactHandle[] tuple) {
            expected = tuple;
        }

        public boolean matches(Object arg) {
            if ( arg == null || !(arg.getClass().isArray() && InternalFactHandle.class.isAssignableFrom( arg.getClass().getComponentType() )) ) {
                return false;
            }
            InternalFactHandle[] actual = (InternalFactHandle[]) arg;
            return Arrays.equals( expected,
                                  actual );
        }

        public void describeTo(Description description) {
            description.appendValue( expected );
        }

        /**
         * Is the value equal to another value, as tested by the
         * {@link java.lang.Object#equals} invokedMethod?
         */
        @Factory
        public static Matcher<List<InternalFactHandle>> isTuple(List<InternalFactHandle> operand) {
            return new IsTuple( operand );
        }

        public static Matcher< ? super List<InternalFactHandle>> isTuple(InternalFactHandle... operands) {
            return new IsTuple( operands );
        }
    }

    public static class Item
            implements
            Activation {

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

        public void addLogicalDependency(LogicalDependency arg0) {
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

        public LinkedList getLogicalDependencies() {
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

        public GroupElement getSubRule() {
            return null;
        }

        public LeftTupleImpl getTuple() {
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


        public void setLogicalDependencies(LinkedList<LogicalDependency> arg0) {
        }

        public List<String> getDeclarationIds() {
            return null;
        }

        public Object getDeclarationValue(String arg0) {
            return null;
        }

        public List<? extends FactHandle> getFactHandles() {
            return null;
        }

        public List<Object> getObjects() {
            return null;
        }

        public InternalFactHandle getFactHandle() {
            return null;
        }

        public boolean isAdded() {
            return false;
        }

        public void addBlocked(LogicalDependency node) {
        }

        public LinkedList getBlocked() {
            return null;
        }

        public void setBlocked(LinkedList<LogicalDependency> justified) {
        }

        public void addBlocked(LinkedListNode node) {
        }

        public LinkedList getBlockers() {
            return null;
        }

        public boolean isMatched() {
            return false;
        }

        public void setMatched(boolean matched) { }

        public boolean isActive() {
            return false;
        }

        public void setActive(boolean active) { }

        public boolean isRuleAgendaItem() {
            return false;
        }
    }

    public static class ItemConflictResolver
            implements
            ConflictResolver {

        /**
         *
         */
        private static final long                 serialVersionUID = 1L;
        public static final  ItemConflictResolver INSTANCE         = new ItemConflictResolver();

        public static ItemConflictResolver getInstance() {
            return ItemConflictResolver.INSTANCE;
        }

        /**
         * @see ConflictResolver
         */
        public final int compare(final Object existing,
                                 final Object adding) {
            return compare((Item) existing,
                           (Item) adding);
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

        public int compare(Activation arg0,
                           Activation arg1) {
            return 0;
        }

    }
}
