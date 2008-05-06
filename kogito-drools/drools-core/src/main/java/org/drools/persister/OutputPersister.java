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

            // Write out RightTuples for FactHandle
            if ( handle.getRightTuple() != null ) {
                stream.writeInt( PersisterEnums.RIGHT_TUPLE );
                int i = 0;
                for ( RightTuple rightTuple = handle.getRightTuple(); rightTuple != null; rightTuple = (RightTuple) rightTuple.getHandleNext() ) {
                    if ( i != 0 ) {
                        stream.writeInt( PersisterEnums.REPEAT );
                    }
                    writeRightTuple( rightTuple,
                                     context );
                    i++;
                }
            }
            stream.writeInt( PersisterEnums.END );
        }

        // Write out LeftTuples
        for ( Iterator it = wm.getObjectStore().iterateFactHandles(); it.hasNext(); ) {
            InternalFactHandle handle = (InternalFactHandle) it.next();

            if ( handle.getLeftTuple() != null ) {
                stream.writeInt( PersisterEnums.LEFT_TUPLE );
                int i = 0;
                for ( LeftTuple leftTuple = handle.getLeftTuple(); leftTuple != null; leftTuple = (LeftTuple) leftTuple.getLeftParentNext() ) {
                    if ( i != 0 ) {
                        stream.writeInt( PersisterEnums.REPEAT );
                    }

                    stream.writeInt( leftTuple.getLeftTupleSink().getId() );
                    stream.writeInt( leftTuple.getLastHandle().getId() );

                    writeLeftTuple( leftTuple,
                                    context );
                }
                stream.writeInt( PersisterEnums.END );
            }
        }

        writePropagationContexts( context );

        writeActivations( context );

        stream.writeInt( PersisterEnums.END );
    }

    public static void writeRightTuple(RightTuple rightTuple,
                                       WMSerialisationOutContext context) throws IOException {
        ObjectOutputStream stream = context.stream;
        InternalWorkingMemory wm = context.wm;
        stream.writeInt( rightTuple.getRightTupleSink().getId() );
    }

    public static void writeLeftTuple(LeftTuple leftTuple,
                                      WMSerialisationOutContext context) throws IOException {
        ObjectOutputStream stream = context.stream;
        InternalRuleBase ruleBase = context.ruleBase;
        InternalWorkingMemory wm = context.wm;

        LeftTupleSink sink = leftTuple.getLeftTupleSink();
        stream.writeInt( sink.getId() );

        if ( sink instanceof JoinNode ) {
            if ( leftTuple.getBetaChildren() != null ) {
                stream.writeInt( PersisterEnums.RIGHT_TUPLE );
            }
            int i = 0;
            for ( LeftTuple childLeftTuple = leftTuple.getBetaChildren(); leftTuple != null; childLeftTuple = (LeftTuple) leftTuple.getLeftParentNext() ) {
                if ( i != 0 ) {
                    stream.writeInt( PersisterEnums.REPEAT );
                }
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

                stream.writeInt( PersisterEnums.LEFT_TUPLE );
                int i = 0;
                for ( LeftTuple childLeftTuple = leftTuple.getBetaChildren(); leftTuple != null; childLeftTuple = (LeftTuple) leftTuple.getLeftParentNext() ) {
                    if ( i != 0 ) {
                        stream.writeInt( PersisterEnums.REPEAT );
                    }
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
            stream.writeInt( PersisterEnums.ACTIVATION );
        }

        int i = 0;
        for ( LeftTuple leftTuple : tuples.keySet() ) {
            if ( i != 0 ) {
                stream.writeInt( PersisterEnums.REPEAT );
            }
            writeActivation( context,
                             leftTuple,
                             (AgendaItem) leftTuple.getActivation(),
                             (RuleTerminalNode) leftTuple.getLeftTupleSink() );
            i++;
        }
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
        stream.writeChars( rule.getPackage() );
        stream.writeChars( rule.getName() );

        stream.writeLong( agendaItem.getPropagationContext().getPropagationNumber() );

        stream.writeInt( ruleTerminalNode.getId() );

        stream.writeBoolean( agendaItem.isActivated() );
    }

    public static void writePropagationContexts(WMSerialisationOutContext context) throws IOException {
        ObjectOutputStream stream = context.stream;

        Map<LeftTuple, Integer> tuples = context.terminalTupleMap;
        if ( !tuples.isEmpty() ) {
            stream.writeInt( PersisterEnums.PROPAGATION_CONTEXT );
        }

        Map<Long, PropagationContext> pcMap = new HashMap<Long, PropagationContext>();

        int i = 0;
        for ( LeftTuple leftTuple : tuples.keySet() ) {
            if ( i != 0 ) {
                stream.writeInt( PersisterEnums.REPEAT );
            }
            PropagationContext pc = leftTuple.getActivation().getPropagationContext();
            if ( !pcMap.containsKey( pc.getPropagationNumber() ) ) {
                writePropagationContext( context,
                                         pc );
                pcMap.put( pc.getPropagationNumber(),
                           pc );
            }
            i++;
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

        stream.writeInt( pc.getFactHandleOrigin().getId() );

        stream.writeInt( pc.getActiveActivations() );
        stream.writeInt( pc.getDormantActivations() );

        stream.writeUTF( pc.getEntryPoint().getEntryPointId() );
    }

    //    public void writeParentActivations(Activation[] activations) throws IOException {
    //        //        // we do these first as we know the parent Activation is null
    //        //        // first create placeholders for rules
    //        //        
    //        //        for ( int i = 0, length = activations.length; i < length; i++ ){                
    //        //            AgendaItem item = ( AgendaItem ) activations[i];
    //        //            // this is a parent activation that has had it's resources released, so write first
    //        //            if ( item.getPropagationContext().getActivationOrigin() == null ) {
    //        //                writeActivation( item );
    //        //            }
    //        //            PlaceholderEntry placeHolder = this.placeholders.assignPlaceholder( item.getRule(), RulePersisterKey.getInstace() );               
    //        //            RulePersisterKey.getInstace().writeExternal( item.getRule(), placeHolder, this.stream );
    //        //            
    //        //            // writeout Activation on PropagationContext
    //        //            item = ( AgendaItem ) item.getPropagationContext().getActivationOrigin();
    //        //            Rule rule = item.getPropagationContext().getRuleOrigin();
    //        //        }           
    //    }
    //
    ////    public void writeActivations(InternalWorkingMemory wm) throws IOException {
    ////        //        BinaryHeapQueueAgendaGroup[] groups = ( BinaryHeapQueueAgendaGroup[] ) wm.getAgenda().getAgendaGroups();
    ////        //        for ( int i = 0, iLength = groups.length; i < iLength; i++ ) {
    ////        //            BinaryHeapQueueAgendaGroup group = groups[i];
    ////        //            this.stream.writeInt( group.size() );
    ////        //            this.stream.writeChars( group.getName() );      
    ////        //            
    ////        //            writeActivations( groups[i].getActivations() );
    ////        //        }
    ////    }
    //
    //    public void writeActivation(Activation[] activations) throws IOException {
    //        //        // first create placeholders for rules, need to count rules first
    //        //        int count =  0;
    //        //        RulePersisterKey ruleKey = RulePersisterKey.getInstace() ;
    //        //        for ( int i = 0, length = activations.length; i < length; i++ ){                
    //        //            AgendaItem item = ( AgendaItem ) activations[i];    
    //        //            
    //        //            Rule rule = item.getRule();
    //        //            if ( this.placeholders.lookupPlaceholder(  rule, ruleKey) == null ) {
    //        //                this.placeholders.assignPlaceholder( rule, ruleKey );
    //        //                count++;
    //        //            }
    //        //            
    //        //            Rule rule = item.getPropagationContext().getRuleOrigin()
    //        //            
    //        //            // writeout Activation on PropagationContext
    //        //            item = ( AgendaItem ) item.getPropagationContext().getActivationOrigin();
    //        //            Rule rule = item.getPropagationContext().getRuleOrigin();
    //        //        }   
    //        //        
    //        //        // write our activations
    //        //        for ( int j = 0, jLength = group.size(); j < jLength; j++ ){                
    //        //            AgendaItem item = ( AgendaItem ) activations[i];                
    //        //            writeTuple( ( ReteTuple ) item.getTuple() );
    //        //            this.stream.writeInt( this.placeholders.assignPlaceholder( item.getRule(), RulePersisterKey.getInstace() ).id );
    //        //            this.stream.write(  item.getSalience()  );
    //        //        }          
    //    }
    //
    //    public void writeTuple(LeftTuple tuple) throws IOException {
    //        //        tuple.writeExternal( this.stream );
    //        //        LeftTuple leftParent = tuple;
    //        //        LeftTuple child = tuple.getBetaChildren();
    //        //        RightTuple rightParent = child.getRightParent();
    //
    //        //        int size = 0;        
    //        //        LeftTuple entry = tuple;
    //        //        while ( entry != null ) {
    //        //            size++;
    //        //            entry = entry.getParent();
    //        //        }
    //        //        
    //        //        this.stream.writeInt( size );
    //        //        while ( entry != null ) {
    //        //            this.stream.writeLong( entry.getLastHandle().getId() );
    //        //            entry = entry.getParent();
    //        //        }        
    //    }

    //    public OutputPersister(WorkingMemory workingMemory) {
    //        BinaryHeapQueueAgendaGroup[] groups = ( BinaryHeapQueueAgendaGroup[] ) workingMemory.getAgenda().getAgendaGroups();
    //        
    //    }
}
