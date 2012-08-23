package org.drools.core.reteoo;

import org.drools.spi.Consequence;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

import org.drools.common.ActivationGroupNode;
import org.drools.common.ActivationNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.LogicalDependency;
import org.drools.core.reteoo.*;
import org.drools.core.util.BinaryHeapQueue;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListNode;
import org.drools.core.util.Queueable;
import org.drools.reteoo.LeftTupleImpl;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.runtime.rule.FactHandle;
import org.drools.spi.Activation;
import org.drools.spi.AgendaGroup;
import org.drools.spi.ConflictResolver;
import org.drools.spi.PropagationContext;

/**
 * Thes test class uses auxiliary test classes in org.drools.util:
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

            Queueable[] elems = group.getQueue();
            for ( Queueable elem : elems ) {
                Item item = (Item) elem;
                //        System.out.print( " " + item.getSalience() + "/"  + item.getActivationNumber() + "/" + item.getIndex() );
                if ( item.getIndex() % 2 == 0 ) {
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
         * @param name
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
         * @see org.drools.spi.AgendaGroup#size()
         */
        public int size() {
            return this.queue.size();
        }

        public void add(final Item item) {
            this.queue.enqueue( (Queueable) item );
        }

        public Item getNext() {
            return (Item) this.queue.dequeue();
        }

        /**
         * Iterates a PriorityQueue removing empty entries until it finds a populated entry and return true,
         * otherwise it returns false;
         *
         * @param priorityQueue
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
            this.queue.dequeue( agendaItem.getIndex() );
        }

        public Queueable[] getQueue() {
            return (Queueable[]) this.queue.toArray( new Queueable[size()] );
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
        Queueable,
        Activation {

        private static int actNo = 1;

        private int        index;
        private long       activationNumber;
        private Group      group;
        private int        salience;

        public Item(Group group,
                    int salience) {
            this.group = group;
            this.salience = salience;
            this.activationNumber = actNo++;
        }

        public void dequeue() {
            if ( this.group != null ) {
                this.group.remove( this );
            }
            this.index = -1;
        }

        public void enqueued(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public int getSalience() {
            return salience;
        }

        public long getActivationNumber() {
            return activationNumber;
        }

        public void addLogicalDependency(LogicalDependency arg0) {
            // TODO Auto-generated method stub

        }

        public ActivationGroupNode getActivationGroupNode() {
            // TODO Auto-generated method stub
            return null;
        }

        public ActivationNode getActivationNode() {
            // TODO Auto-generated method stub
            return null;
        }

        public AgendaGroup getAgendaGroup() {
            // TODO Auto-generated method stub
            return null;
        }

        public LinkedList getLogicalDependencies() {
            // TODO Auto-generated method stub
            return null;
        }

        public PropagationContext getPropagationContext() {
            // TODO Auto-generated method stub
            return null;
        }

        public Rule getRule() {
            // TODO Auto-generated method stub
            return null;
        }

        public Consequence getConsequence() {
            // TODO Auto-generated method stub
            return null;
        }

        public GroupElement getSubRule() {
            // TODO Auto-generated method stub
            return null;
        }

        public LeftTupleImpl getTuple() {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isActivated() {
            // TODO Auto-generated method stub
            return false;
        }

        public void remove() {
            // TODO Auto-generated method stub

        }

        public void setActivated(boolean arg0) {
            // TODO Auto-generated method stub

        }

        public void setActivationGroupNode(ActivationGroupNode arg0) {
            // TODO Auto-generated method stub

        }

        public void setActivationNode(ActivationNode arg0) {
            // TODO Auto-generated method stub

        }

        public void setLogicalDependencies(LinkedList arg0) {
            // TODO Auto-generated method stub

        }

        public List<String> getDeclarationIDs() {
            // TODO Auto-generated method stub
            return null;
        }

        public Object getDeclarationValue(String arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        public List< ? extends FactHandle> getFactHandles() {
            // TODO Auto-generated method stub
            return null;
        }

        public List<Object> getObjects() {
            // TODO Auto-generated method stub
            return null;
        }

        public InternalFactHandle getFactHandle() {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isAdded() {
            // TODO Auto-generated method stub
            return false;
        }
        
        public boolean isActive() {
            return isActivated();
        }

        public void addBlocked(LogicalDependency node) {
            // TODO Auto-generated method stub
            
        }

        public LinkedList getBlocked() {
            // TODO Auto-generated method stub
            return null;
        }

        public void setBlocked(LinkedList justified) {
            // TODO Auto-generated method stub
            
        }

        public void addBlocked(LinkedListNode node) {
            // TODO Auto-generated method stub
            
        }

        public LinkedList getBlockers() {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isMatched() {
            // TODO Auto-generated method stub
            return false;
        }

        public void setMatched(boolean matched) {
            // TODO Auto-generated method stub
            
        }            
    }

    public static class ItemConflictResolver
        implements
        ConflictResolver {

        /**
           * 
           */
        private static final long                serialVersionUID = 1L;
        public static final ItemConflictResolver INSTANCE         = new ItemConflictResolver();

        public static ItemConflictResolver getInstance() {
            return ItemConflictResolver.INSTANCE;
        }

        /**
         * @see ConflictResolver
         */
        public final int compare(final Object existing,
                                 final Object adding) {
            return compare( (Item) existing,
                            (Item) adding );
        }

        public final int compare(final Item existing,
                                 final Item adding) {
            final int s1 = existing.getSalience();
            final int s2 = adding.getSalience();

            if ( s1 != s2 ) {
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
