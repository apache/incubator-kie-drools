package org.drools.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;

import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.RuleTerminalNode.TerminalNodeMemory;
import org.drools.util.AbstractHashTable;
import org.drools.util.Entry;
import org.drools.util.ReflectiveVisitor;
import org.drools.util.RightTupleIndexHashTable;
import org.drools.util.RightTupleList;

public class MemoryVisitor extends ReflectiveVisitor
    implements
    Externalizable {
    private InternalWorkingMemory workingMemory;
    private int                   indent = 0;

    /**
     * Constructor.
     */
    public MemoryVisitor() {
    }

    public MemoryVisitor(final InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        workingMemory = (InternalWorkingMemory) in.readObject();
        indent = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( workingMemory );
        out.writeInt( indent );
    }

    /**
     * RuleBaseImpl visits its Rete.
     */
    public void visitReteooRuleBase(final ReteooRuleBase ruleBase) {
        visit( (ruleBase).getRete() );
    }

    /**
     * Rete visits each of its ObjectTypeNodes.
     */
    public void visitRete(final Rete rete) {
        for ( ObjectTypeNode node : rete.getObjectTypeNodes() ) {
            visit( node );
        }
    }

    public void visitObjectTypeNode(final ObjectTypeNode node) {
        System.out.println( indent() + node );

        final RightTupleList memory = (RightTupleList) this.workingMemory.getNodeMemory( node );
        checkObjectHashTable( memory );

        this.indent++;
        try {
            final Field field = ObjectSource.class.getDeclaredField( "sink" );
            field.setAccessible( true );
            final ObjectSinkPropagator sink = (ObjectSinkPropagator) field.get( node );
            final ObjectSink[] sinks = sink.getSinks();
            for ( int i = 0, length = sinks.length; i < length; i++ ) {
                visit( sinks[i] );
            }
        } catch ( final Exception e ) {
            e.printStackTrace();
        }
        this.indent--;
    }

    public void visitAlphaNode(final AlphaNode node) {
        System.out.println( indent() + node );

        final RightTupleList memory = (RightTupleList) this.workingMemory.getNodeMemory( node );
        checkObjectHashTable( memory );

        this.indent++;
        try {
            final Field field = ObjectSource.class.getDeclaredField( "sink" );
            field.setAccessible( true );
            final ObjectSinkPropagator sink = (ObjectSinkPropagator) field.get( node );
            final ObjectSink[] sinks = sink.getSinks();
            for ( int i = 0, length = sinks.length; i < length; i++ ) {
                visit( sinks[i] );
            }
        } catch ( final Exception e ) {
            e.printStackTrace();
        }
        this.indent--;
    }

    public void visitLeftInputAdapterNode(final LeftInputAdapterNode node) {
        System.out.println( indent() + node );

        this.indent++;
        try {
            final Field field = LeftTupleSource.class.getDeclaredField( "sink" );
            field.setAccessible( true );
            final LeftTupleSinkPropagator sink = (LeftTupleSinkPropagator) field.get( node );
            final LeftTupleSink[] sinks = sink.getSinks();
            for ( int i = 0, length = sinks.length; i < length; i++ ) {
                visit( sinks[i] );
            }
        } catch ( final Exception e ) {
            e.printStackTrace();
        }
        this.indent--;
    }

    public void visitJoinNode(final JoinNode node) {
        System.out.println( indent() + node );

        try {
            final BetaMemory memory = (BetaMemory) this.workingMemory.getNodeMemory( node );
            checkObjectHashTable( memory.getRightTupleMemory() );
            checkLeftTupleMemory( memory.getLeftTupleMemory() );
        } catch ( final Exception e ) {
            e.printStackTrace();
        }

        this.indent++;
        try {
            final Field field = LeftTupleSource.class.getDeclaredField( "sink" );
            field.setAccessible( true );
            final LeftTupleSinkPropagator sink = (LeftTupleSinkPropagator) field.get( node );
            final LeftTupleSink[] sinks = sink.getSinks();
            for ( int i = 0, length = sinks.length; i < length; i++ ) {
                visit( sinks[i] );
            }
        } catch ( final Exception e ) {
            e.printStackTrace();
        }
        this.indent--;
    }

    public void visitNotNode(final NotNode node) {
        System.out.println( indent() + node );
        try {
            final BetaMemory memory = (BetaMemory) this.workingMemory.getNodeMemory( node );
            checkObjectHashTable( memory.getRightTupleMemory() );
            checkLeftTupleMemory( memory.getLeftTupleMemory() );
        } catch ( final Exception e ) {
            e.printStackTrace();
        }

        this.indent++;
        try {
            final Field field = LeftTupleSource.class.getDeclaredField( "sink" );
            field.setAccessible( true );
            final LeftTupleSinkPropagator sink = (LeftTupleSinkPropagator) field.get( node );
            final LeftTupleSink[] sinks = sink.getSinks();
            for ( int i = 0, length = sinks.length; i < length; i++ ) {
                visit( sinks[i] );
            }
        } catch ( final Exception e ) {
            e.printStackTrace();
        }
        this.indent--;
    }

    public void visitTerminalNode(final RuleTerminalNode node) {
        System.out.println( indent() + node );
        final TerminalNodeMemory memory = (TerminalNodeMemory) this.workingMemory.getNodeMemory( node );
        checkLeftTupleMemory( memory.getTupleMemory() );
    }

    //    private void checkObjectHashMap(final ObjectHashMap map) {
    //        final Entry[] entries = map.getTable();
    //        int count = 0;
    //        for ( int i = 0, length = entries.length; i < length; i++ ) {
    //            if ( entries[i] != null ) {
    //                count++;
    //            }
    //        }
    //
    //        System.out.println( "ObjectHashMap: " + indent() + map.size() + ":" + count );
    //        if ( map.size() != count ) {
    //            System.out.println( indent() + "error" );
    //        }
    //    }

    private void checkObjectHashTable(final RightTupleMemory memory) {
        if ( memory instanceof RightTupleList ) {
            checkRightTupleList( (RightTupleList) memory );
        } else if ( memory instanceof RightTupleIndexHashTable ) {
            checkRightTupleIndexHashTable( (RightTupleIndexHashTable) memory );
        } else {
            throw new RuntimeException( memory.getClass() + " should not be here" );
        }
    }

    private void checkRightTupleList(final RightTupleList memory) {
        int count = 0;
        for ( RightTuple rightTuple = memory.getFirst( null ); rightTuple != null; rightTuple = (RightTuple) rightTuple.getNext() ) {
                    count++;
        }

        System.out.println( indent() + "FactHashTable: " + memory.size() + ":" + count );
        if ( memory.size() != count ) {
            System.out.println( indent() + "error" );
        }
    }

    private void checkRightTupleIndexHashTable(final RightTupleIndexHashTable memory) {
        final Entry[] entries = memory.getTable();
        int factCount = 0;
        int bucketCount = 0;
        for ( int i = 0, length = entries.length; i < length; i++ ) {
            if ( entries[i] != null ) {
                RightTupleList rightTupleList = (RightTupleList) entries[i];
                while ( rightTupleList != null ) {
                    if ( rightTupleList.first != null ) {
                        Entry entry = rightTupleList.first;
                        while ( entry != null ) {
                            entry = entry.getNext();
                            factCount++;
                        }
                    } else {
                        System.out.println( "error : fieldIndexHashTable cannot have empty FieldIndexEntry objects" );
                    }
                    rightTupleList = (RightTupleList) rightTupleList.getNext();
                    bucketCount++;
                }
            }
        }

        try {
            final Field field = AbstractHashTable.class.getDeclaredField( "size" );
            field.setAccessible( true );
            System.out.println( indent() + "FieldIndexBuckets: " + ((Integer) field.get( memory )).intValue() + ":" + bucketCount );
            if ( ((Integer) field.get( memory )).intValue() != bucketCount ) {
                System.out.println( indent() + "error" );
            }
        } catch ( final Exception e ) {
            e.printStackTrace();
        }

        System.out.println( indent() + "FieldIndexFacts: " + memory.size() + ":" + factCount );
        if ( memory.size() != factCount ) {
            System.out.println( indent() + "error" );
        }
    }

    private void checkLeftTupleMemory(final LeftTupleMemory memory) {
        // @todo need to implement this correctly, as we now have indexed and none indxed tuple memories.
//        final Entry[] entries = memory.getTable();
//        int count = 0;
//        for ( int i = 0, length = entries.length; i < length; i++ ) {
//            if ( entries[i] != null ) {
//                Entry entry = entries[i];
//                while ( entry != null ) {
//                    count++;
//                    entry = entry.getNext();
//                }
//            }
//        }
//
//        System.out.println( indent() + "TupleMemory: " + memory.size() + ":" + count );
//        if ( memory.size() != count ) {
//            System.out.println( indent() + "error" );
//        }
    }

    private String indent() {
        final StringBuilder buffer = new StringBuilder();
        for ( int i = 0; i < this.indent; i++ ) {
            buffer.append( "  " );
        }
        return buffer.toString();
    }
}
