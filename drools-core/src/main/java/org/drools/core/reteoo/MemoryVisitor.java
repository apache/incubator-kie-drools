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

package org.drools.core.reteoo;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.spi.Tuple;
import org.drools.core.util.AbstractHashTable;
import org.drools.core.util.Entry;
import org.drools.core.util.FastIterator;
import org.drools.core.util.LinkedList;
import org.drools.core.util.ReflectiveVisitor;
import org.drools.core.util.index.TupleIndexHashTable;
import org.drools.core.util.index.TupleList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;

public class MemoryVisitor extends ReflectiveVisitor
    implements
    Externalizable {

    protected static final transient Logger logger = LoggerFactory.getLogger(MemoryVisitor.class);

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
    public void visitReteooRuleBase(InternalKnowledgeBase kBase) {
        visit( kBase.getRete() );
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
        logger.info( indent() + node );

        this.indent++;
        try {
            final Field field = ObjectSource.class.getDeclaredField( "sink" );
            field.setAccessible( true );
            final ObjectSinkPropagator sink = (ObjectSinkPropagator) field.get( node );
            final ObjectSink[] sinks = sink.getSinks();
            for ( ObjectSink sink1 : sinks ) {
                visit( sink1 );
            }
        } catch ( final Exception e ) {
            e.printStackTrace();
        }
        this.indent--;
    }

    public void visitAlphaNode(final AlphaNode node) {
        logger.info( indent() + node );

        this.indent++;
        try {
            final Field field = ObjectSource.class.getDeclaredField( "sink" );
            field.setAccessible( true );
            final ObjectSinkPropagator sink = (ObjectSinkPropagator) field.get( node );
            final ObjectSink[] sinks = sink.getSinks();
            for ( ObjectSink sink1 : sinks ) {
                visit( sink1 );
            }
        } catch ( final Exception e ) {
            e.printStackTrace();
        }
        this.indent--;
    }

    public void visitLeftInputAdapterNode(final LeftInputAdapterNode node) {
        logger.info( indent() + node );

        this.indent++;
        try {
            final Field field = LeftTupleSource.class.getDeclaredField( "sink" );
            field.setAccessible( true );
            final LeftTupleSinkPropagator sink = (LeftTupleSinkPropagator) field.get( node );
            final LeftTupleSink[] sinks = sink.getSinks();
            for ( LeftTupleSink sink1 : sinks ) {
                visit( sink1 );
            }
        } catch ( final Exception e ) {
            e.printStackTrace();
        }
        this.indent--;
    }

    public void visitJoinNode(final JoinNode node) {
        logger.info( indent() + node );

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
            for ( LeftTupleSink sink1 : sinks ) {
                visit( sink1 );
            }
        } catch ( final Exception e ) {
            e.printStackTrace();
        }
        this.indent--;
    }

    public void visitNotNode(final NotNode node) {
        logger.info( indent() + node );
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
            for ( LeftTupleSink sink1 : sinks ) {
                visit( sink1 );
            }
        } catch ( final Exception e ) {
            e.printStackTrace();
        }
        this.indent--;
    }

    public void visitRuleTerminalNode(final RuleTerminalNode node) {
        logger.info( indent() + node );
    }

    private void checkObjectHashTable(final TupleMemory memory) {
        if ( memory instanceof TupleList ) {
            checkRightTupleList( (TupleList) memory );
        } else if ( memory instanceof TupleIndexHashTable ) {
            checkRightTupleIndexHashTable( (TupleIndexHashTable) memory );
        } else {
            throw new RuntimeException( memory.getClass() + " should not be here" );
        }
    }

    private void checkRightTupleList(final TupleList memory) {
        int count = 0;
        FastIterator rightIt = memory.fastIterator();
        for ( Tuple rightTuple = memory.getFirst( ); rightTuple != null; rightTuple = (Tuple) rightIt.next( rightTuple ) ) {
                    count++;
        }

        logger.info( indent() + "FactHashTable: " + memory.size() + ":" + count );
        if ( memory.size() != count ) {
            logger.info( indent() + "error" );
        }
    }

    private void checkRightTupleIndexHashTable(final TupleIndexHashTable memory) {
        final Entry[] entries = memory.getTable();
        int factCount = 0;
        int bucketCount = 0;
        FastIterator it = LinkedList.fastIterator;
        for ( Entry entry1 : entries ) {
            if ( entry1 != null ) {
                TupleList rightTupleList = (TupleList) entry1;
                while ( rightTupleList != null ) {
                    if ( rightTupleList.getFirst() != null ) {
                        Entry entry = rightTupleList.getFirst();
                        while ( entry != null ) {
                            entry = it.next( entry );
                            factCount++;
                        }
                    } else {
                        logger.info( "error : fieldIndexHashTable cannot have empty FieldIndexEntry objects" );
                    }
                    rightTupleList = rightTupleList.getNext();
                    bucketCount++;
                }
            }
        }

        try {
            final Field field = AbstractHashTable.class.getDeclaredField( "size" );
            field.setAccessible( true );
            logger.info( indent() + "FieldIndexBuckets: " + field.get( memory ) + ":" + bucketCount );
            if ( (Integer) field.get( memory ) != bucketCount ) {
                logger.info( indent() + "error" );
            }
        } catch ( final Exception e ) {
            e.printStackTrace();
        }

        logger.info( indent() + "FieldIndexFacts: " + memory.size() + ":" + factCount );
        if ( memory.size() != factCount ) {
            logger.info( indent() + "error" );
        }
    }

    private void checkLeftTupleMemory(final TupleMemory memory) {
        // @todo need to implement this correctly, as we now have indexed and none indxed tuple memories.
//        final Entry[] entries = memory.getTable();
//        int count = 0;
//        for ( int i = 0, length = entries.length; i < length; i++ ) {
//            if ( entries[i] != null ) {
//                Entry entry = entries[i];
//                while ( entry != null ) {
//                    count++;
//                    entry = entry.remove();
//                }
//            }
//        }
//
//        logger.info( indent() + "TupleMemory: " + memory.size() + ":" + count );
//        if ( memory.size() != count ) {
//            logger.info( indent() + "error" );
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
