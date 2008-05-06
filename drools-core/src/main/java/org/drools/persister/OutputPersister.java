package org.drools.persister;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.drools.base.ShadowProxy;
import org.drools.common.AbstractFactHandleFactory;
import org.drools.common.AgendaItem;
import org.drools.common.BinaryHeapQueueAgendaGroup;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.persister.Placeholders.PlaceholderEntry;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.NotNode;
import org.drools.reteoo.ReteooFactHandleFactory;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.RightTupleSink;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.PropagationContext;

public class OutputPersister {
    public WMSerialisationOutContext context;

    public OutputPersister(InternalRuleBase ruleBase,
                           InternalWorkingMemory wm,
                           ObjectOutputStream stream,
                           PlaceholderResolverStrategyFactory resolverStrategyFactory) {
        context = new WMSerialisationOutContext( stream,
                                                 ruleBase,
                                                 wm,
                                                 RuleBaseNodes.getNodeMap( ruleBase ),
                                                 resolverStrategyFactory );
    }

    public void write() throws IOException {
        writeFactHandles( context );
        context.stream.close();
    }

    public static void writeFactHandles(WMSerialisationOutContext context) throws IOException {
        ObjectOutputStream stream = context.stream;
        InternalWorkingMemory wm = context.wm;
        PlaceholderResolverStrategyFactory resolverStrategyFactory = context.resolverStrategyFactory;

        ((AbstractFactHandleFactory) wm.getFactHandleFactory()).writeExternal( stream );

        stream.writeInt( wm.getObjectStore().size() );

        // Write out FactHandles
        for ( Iterator it = wm.getObjectStore().iterateFactHandles(); it.hasNext(); ) {
            //stream.writeInt( PersisterEnums.FACT_HANDLE );
            InternalFactHandle handle = (InternalFactHandle) it.next();
            stream.writeInt( handle.getId() );
            stream.writeLong( handle.getRecency() );

            PlaceholderResolverStrategy strategy = resolverStrategyFactory.get( handle.getObject() );
            //stream.writeInt( strategy.getId() );

            Object object = handle.getObject();
            if ( object instanceof ShadowProxy ) {
                object = ((ShadowProxy) object).getShadowedObject();
            }
            strategy.write( stream,
                            object );

            writeRightTuples( handle,
                              context );
        }

        writeLeftTuples( context );

        writePropagationContexts( context );

        writeActivations( context );

        stream.writeInt( PersisterEnums.END );
    }

    public static void writeRightTuples(InternalFactHandle handle,
                                        WMSerialisationOutContext context) throws IOException {
        ObjectOutputStream stream = context.stream;
        for ( RightTuple rightTuple = handle.getRightTuple(); rightTuple != null; rightTuple = (RightTuple) rightTuple.getHandleNext() ) {
            stream.writeInt( PersisterEnums.RIGHT_TUPLE );
            writeRightTuple( rightTuple,
                             context );
        }
        stream.writeInt( PersisterEnums.END );
    }

    public static void writeRightTuple(RightTuple rightTuple,
                                       WMSerialisationOutContext context) throws IOException {
        ObjectOutputStream stream = context.stream;
        InternalWorkingMemory wm = context.wm;
        stream.writeInt( rightTuple.getRightTupleSink().getId() );
    }

    public static void writeLeftTuples(WMSerialisationOutContext context) throws IOException {
        ObjectOutputStream stream = context.stream;
        InternalWorkingMemory wm = context.wm;

        // Write out LeftTuples
        for ( Iterator it = wm.getObjectStore().iterateFactHandles(); it.hasNext(); ) {
            InternalFactHandle handle = (InternalFactHandle) it.next();

            for ( LeftTuple leftTuple = handle.getLeftTuple(); leftTuple != null; leftTuple = (LeftTuple) leftTuple.getLeftParentNext() ) {
                stream.writeInt( PersisterEnums.LEFT_TUPLE );

                stream.writeInt( leftTuple.getLeftTupleSink().getId() );
                stream.writeInt( leftTuple.getLastHandle().getId() );

                writeLeftTuple( leftTuple,
                                context );
            }
        }
        stream.writeInt( PersisterEnums.END );
    }

    public static void writeLeftTuple(LeftTuple leftTuple,
                                      WMSerialisationOutContext context) throws IOException {
        ObjectOutputStream stream = context.stream;
        InternalRuleBase ruleBase = context.ruleBase;
        InternalWorkingMemory wm = context.wm;

        LeftTupleSink sink = leftTuple.getLeftTupleSink();

        if ( sink instanceof JoinNode ) {
            for ( LeftTuple childLeftTuple = leftTuple.getBetaChildren(); leftTuple != null; childLeftTuple = (LeftTuple) leftTuple.getLeftParentNext() ) {
                stream.writeInt( PersisterEnums.RIGHT_TUPLE );
                stream.writeInt( childLeftTuple.getLeftTupleSink().getId() );
                stream.writeInt( childLeftTuple.getRightParent().getFactHandle().getId() );
                writeLeftTuple( childLeftTuple,
                                context );
            }
            stream.writeInt( PersisterEnums.END );
        } else if ( sink instanceof NotNode ) {
            if ( leftTuple.getBlocker() == null ) {
                // is blocked so has children
                stream.writeInt( PersisterEnums.LEFT_TUPLE_NOT_BLOCKED );

                for ( LeftTuple childLeftTuple = leftTuple.getBetaChildren(); leftTuple != null; childLeftTuple = (LeftTuple) leftTuple.getLeftParentNext() ) {
                    stream.writeInt( PersisterEnums.LEFT_TUPLE );
                    stream.writeInt( childLeftTuple.getLeftTupleSink().getId() );
                    writeLeftTuple( childLeftTuple,
                                    context );
                }
                stream.writeInt( PersisterEnums.END );

            } else {
                stream.writeInt( PersisterEnums.LEFT_TUPLE_BLOCKED );
                stream.writeInt( leftTuple.getBlocker().getFactHandle().getId() );
            }
        } else if ( sink instanceof RuleTerminalNode ) {
            int pos = context.terminalTupleMap.size();
            context.terminalTupleMap.put( leftTuple,
                                          pos );
        }

    }

    public static void writeActivations(WMSerialisationOutContext context) throws IOException {
        ObjectOutputStream stream = context.stream;

        Map<LeftTuple, Integer> tuples = context.terminalTupleMap;
        if ( !tuples.isEmpty() ) {
            for ( LeftTuple leftTuple : tuples.keySet() ) {
                stream.writeInt( PersisterEnums.ACTIVATION );
                writeActivation( context,
                                 leftTuple,
                                 (AgendaItem) leftTuple.getActivation(),
                                 (RuleTerminalNode) leftTuple.getLeftTupleSink() );
            }
        }
        stream.writeInt( PersisterEnums.END );
    }

    public static void writeActivation(WMSerialisationOutContext context,
                                       LeftTuple leftTuple,
                                       AgendaItem agendaItem,
                                       RuleTerminalNode ruleTerminalNode) throws IOException {
        ObjectOutputStream stream = context.stream;

        stream.writeLong( agendaItem.getActivationNumber() );

        stream.writeInt( context.terminalTupleMap.get( leftTuple ) );

        stream.writeInt( agendaItem.getSalience() );

        Rule rule = agendaItem.getRule();
        stream.writeUTF( rule.getPackage() );
        stream.writeUTF( rule.getName() );

        stream.writeLong( agendaItem.getPropagationContext().getPropagationNumber() );

        stream.writeBoolean( agendaItem.isActivated() );
    }

    public static void writePropagationContexts(WMSerialisationOutContext context) throws IOException {
        ObjectOutputStream stream = context.stream;

        Map<LeftTuple, Integer> tuples = context.terminalTupleMap;
        if ( !tuples.isEmpty() ) {
            Map<Long, PropagationContext> pcMap = new HashMap<Long, PropagationContext>();

            for ( LeftTuple leftTuple : tuples.keySet() ) {
                PropagationContext pc = leftTuple.getActivation().getPropagationContext();
                if ( !pcMap.containsKey( pc.getPropagationNumber() ) ) {
                    stream.writeInt( PersisterEnums.PROPAGATION_CONTEXT );
                    writePropagationContext( context,
                                             pc );
                    pcMap.put( pc.getPropagationNumber(),
                               pc );
                }
            }
        }

        stream.writeInt( PersisterEnums.END );
    }

    public static void writePropagationContext(WMSerialisationOutContext context,
                                               PropagationContext pc) throws IOException {
        ObjectOutputStream stream = context.stream;
        Map<LeftTuple, Integer> tuples = context.terminalTupleMap;

        stream.writeInt( pc.getType() );

        Rule ruleOrigin = pc.getRuleOrigin();
        if ( ruleOrigin != null ) {
            stream.writeBoolean( true );
            stream.writeUTF( ruleOrigin.getPackage() );
            stream.writeUTF( ruleOrigin.getName() );
        } else {
            stream.writeBoolean( false );
        }

        LeftTuple tupleOrigin = pc.getLeftTupleOrigin();
        if ( tupleOrigin != null ) {
            stream.writeBoolean( true );
            stream.writeInt( tuples.get( tupleOrigin ) );
        } else {
            stream.writeBoolean( false );
        }

        stream.writeLong( pc.getPropagationNumber() );
        stream.writeInt( pc.getFactHandleOrigin().getId() );

        stream.writeInt( pc.getActiveActivations() );
        stream.writeInt( pc.getDormantActivations() );

        stream.writeUTF( pc.getEntryPoint().getEntryPointId() );
    }
}
