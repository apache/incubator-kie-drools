package org.drools.persister;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.drools.base.ClassObjectType;
import org.drools.common.AgendaItem;
import org.drools.common.BaseNode;
import org.drools.common.BinaryHeapQueueAgendaGroup;
import org.drools.common.DefaultAgenda;
import org.drools.common.DefaultFactHandle;
import org.drools.common.InternalAgendaGroup;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.JoinNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.NotNode;
import org.drools.reteoo.ObjectSink;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ReteooFactHandleFactory;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.RightTupleSink;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.RuleTerminalNode.TerminalNodeMemory;
import org.drools.rule.EntryPoint;
import org.drools.rule.GroupElement;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaGroup;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;
import org.drools.util.BinaryHeapQueue;
import org.drools.util.ObjectHashMap;
import org.drools.util.ObjectHashSet;

public class InputPersister {
    public WMSerialisationInContext context;

    public InputPersister(InternalRuleBase ruleBase,
                          ObjectInputStream stream,
                          PlaceholderResolverStrategyFactory resolverStrategyFactory) {
        context = new WMSerialisationInContext( stream,
                                                ruleBase,
                                                RuleBaseNodes.getNodeMap( ruleBase ),
                                                resolverStrategyFactory );
    }

    public InternalWorkingMemory read() throws IOException,
                                       ClassNotFoundException {
        readFactHandles( context );
        context.stream.close();
        return context.wm;
    }

    public static void readFactHandles(WMSerialisationInContext context) throws IOException,
                                                                        ClassNotFoundException {
        ObjectInputStream stream = context.stream;
        InternalRuleBase ruleBase = context.ruleBase;

        PlaceholderResolverStrategyFactory resolverStrategyFactory = context.resolverStrategyFactory;

        ReteooFactHandleFactory factHandleFactory = new ReteooFactHandleFactory();
        factHandleFactory.readExternal( stream );
        context.wm = new ReteooWorkingMemory( 0,
                                              ruleBase,
                                              factHandleFactory );

        int size = stream.readInt();

        if ( size == 0 ) {
            return;
        }

        // load the handles
        InternalFactHandle[] handles = new InternalFactHandle[size];
        for ( int i = 0; i < size; i++ ) {
            int id = stream.readInt();
            long recency = stream.readLong();
            PlaceholderResolverStrategy strategy = resolverStrategyFactory.get( null );
            ObjectPlaceholder placeHolder = strategy.read( stream );

            Object object = placeHolder.resolveObject();

            InternalFactHandle handle = new DefaultFactHandle( id,
                                                               object,
                                                               recency );
            context.handles.put( id,
                                 handle );
            handles[i] = handle;

            context.wm.getObjectStore().addHandle( handle,
                                                   object );

            int type = stream.readInt();
            if ( type == PersisterEnums.RIGHT_TUPLE ) {
                type = PersisterEnums.REPEAT;
                while ( type == PersisterEnums.REPEAT ) {
                    readRightTuple( context,
                                    handle );
                    type = stream.readInt();
                }
            }
        }

        EntryPointNode node = ruleBase.getRete().getEntryPointNode( EntryPoint.DEFAULT );
        Map<ObjectType, ObjectTypeNode> objectTypeNodes = node.getObjectTypeNodes();

        // add handles to object type nodes
        for ( InternalFactHandle handle : handles ) {
            Object object = handle.getObject();
            ClassObjectType objectType = new ClassObjectType( object.getClass() );
            ObjectTypeNode objectTypeNode = objectTypeNodes.get( objectType );
            ObjectHashSet set = (ObjectHashSet) context.wm.getNodeMemory( objectTypeNode );
            set.add( handle,
                     false );
        }

        readLeftTuples( context );

        readPropagationContexts( context );

        readActivations( context );
    }

    public static void readRightTuple(WMSerialisationInContext context,
                                      InternalFactHandle factHandle) throws IOException {
        ObjectInputStream stream = context.stream;

        RightTupleSink sink = (RightTupleSink) context.sinks.get( stream.readInt() );

        BetaMemory memory = (BetaMemory) context.wm.getNodeMemory( (BetaNode) sink );

        RightTuple rightTuple = new RightTuple( factHandle,
                                                sink );
        context.rightTuples.put( new RightTupleKey( factHandle.getId(),
                                                    sink ),
                                 rightTuple );

        memory.getRightTupleMemory().add( rightTuple );
    }
    
    public static void readLeftTuples(WMSerialisationInContext context)  throws IOException {
        ObjectInputStream stream = context.stream;
        
        while ( stream.readInt() == PersisterEnums.LEFT_TUPLE ) {
            LeftTupleSink sink = (LeftTupleSink) context.sinks.get( stream.readInt() );
            int factHandleId = stream.readInt();
            LeftTuple leftTuple = new LeftTuple( context.handles.get( factHandleId ),
                                                 sink,
                                                 true );
            readLeftTuple( leftTuple,
                           context );
        }
    }

    public static void readLeftTuple(LeftTuple parentLeftTuple,
                                     WMSerialisationInContext context) throws IOException {
        ObjectInputStream stream = context.stream;
        InternalRuleBase ruleBase = context.ruleBase;
        InternalWorkingMemory wm = context.wm;
        Map<Integer, BaseNode> sinks = context.sinks;

        LeftTupleSink sink = parentLeftTuple.getLeftTupleSink();

        if ( sink instanceof JoinNode ) {
            BetaMemory memory = (BetaMemory) context.wm.getNodeMemory( (BetaNode) sink );
            memory.getLeftTupleMemory().add( parentLeftTuple );

            while ( stream.readInt() == PersisterEnums.RIGHT_TUPLE ) {
                LeftTupleSink childSink = (LeftTupleSink) sinks.get( stream.readInt() );
                int factHandleId = stream.readInt();
                RightTupleKey key = new RightTupleKey( factHandleId,
                                                       sink );
                RightTuple rightTuple = context.rightTuples.get( key );
                LeftTuple childLeftTuple = new LeftTuple( parentLeftTuple,
                                                          rightTuple,
                                                          childSink,
                                                          true );
                readLeftTuple( childLeftTuple,
                               context );
            }

        } else if ( sink instanceof NotNode ) {
            BetaMemory memory = (BetaMemory) context.wm.getNodeMemory( (BetaNode) sink );
            int type = stream.readInt();
            if ( type == PersisterEnums.LEFT_TUPLE_NOT_BLOCKED ) {
                memory.getLeftTupleMemory().add( parentLeftTuple );

                while ( stream.readInt() == PersisterEnums.LEFT_TUPLE ) {
                    LeftTupleSink childSink = (LeftTupleSink) sinks.get( stream.readInt() );
                    LeftTuple childLeftTuple = new LeftTuple( parentLeftTuple,
                                                              childSink,
                                                              true );
                    readLeftTuple( childLeftTuple,
                                   context );
                }

            } else {
                int factHandleId = stream.readInt();
                RightTupleKey key = new RightTupleKey( factHandleId,
                                                       sink );
                RightTuple rightTuple = context.rightTuples.get( key );

                LeftTuple blockedPrevious = rightTuple.getBlocked();
                if ( blockedPrevious != null ) {
                    parentLeftTuple.setBlockedNext( blockedPrevious );
                    blockedPrevious.setBlockedPrevious( parentLeftTuple );
                }
                rightTuple.setBlocked( parentLeftTuple );
            }
        } else if ( sink instanceof RuleTerminalNode ) {
            RuleTerminalNode ruleTerminalNode = (RuleTerminalNode) sink;
            TerminalNodeMemory memory = (TerminalNodeMemory) wm.getNodeMemory( ruleTerminalNode );
            memory.getTupleMemory().add( parentLeftTuple );

            int pos = context.terminalTupleMap.size();
            context.terminalTupleMap.put( pos,
                                          parentLeftTuple );
        }
    }

    public static void readActivations(WMSerialisationInContext context) throws IOException {
        ObjectInputStream stream = context.stream;

        while ( stream.readInt() == PersisterEnums.ACTIVATION ) {
            readActivation( context );
        }
    }

    public static Activation readActivation(WMSerialisationInContext context) throws IOException {
        ObjectInputStream stream = context.stream;
        InternalRuleBase ruleBase = context.ruleBase;
        InternalWorkingMemory wm = context.wm;

        long activationNumber = stream.readLong();

        LeftTuple leftTuple = context.terminalTupleMap.get( stream.readInt() );

        int salience = stream.readInt();

        //PropagationContext context,
        String pkgName = stream.readUTF();
        String ruleName = stream.readUTF();
        Package pkg = ruleBase.getPackage( pkgName );
        Rule rule = pkg.getRule( ruleName );

        RuleTerminalNode ruleTerminalNode = (RuleTerminalNode) leftTuple.getLeftTupleSink();
        GroupElement subRule = ruleTerminalNode.getSubRule();

        PropagationContext pc = context.propagationContexts.get( stream.readLong() );

        AgendaItem activation = new AgendaItem( activationNumber,
                                                leftTuple,
                                                salience,
                                                pc,
                                                rule,
                                                subRule );

        boolean activated = stream.readBoolean();
        activation.setActivated( activated );
        if ( activated ) {
            InternalAgendaGroup agendaGroup;
            if ( rule.getAgendaGroup() == null || rule.getAgendaGroup().equals( "" ) || rule.getAgendaGroup().equals( AgendaGroup.MAIN ) ) {
                // Is the Rule AgendaGroup undefined? If it is use MAIN,
                // which is added to the Agenda by default
                agendaGroup = (InternalAgendaGroup) wm.getAgenda().getAgendaGroup( AgendaGroup.MAIN );
            } else {
                // AgendaGroup is defined, so try and get the AgendaGroup
                // from the Agenda
                agendaGroup = (InternalAgendaGroup) wm.getAgenda().getAgendaGroup( rule.getAgendaGroup() );
            }

            agendaGroup.add( activation );
        }

        return activation;
    }

    public static void readPropagationContexts(WMSerialisationInContext context) throws IOException {
        ObjectInputStream stream = context.stream;

        while ( stream.readInt() == PersisterEnums.PROPAGATION_CONTEXT ) {
            readPropagationContext( context );
        }

    }

    public static void readPropagationContext(WMSerialisationInContext context) throws IOException {
        ObjectInputStream stream = context.stream;
        InternalRuleBase ruleBase = context.ruleBase;
        InternalWorkingMemory wm = context.wm;

        int type = stream.readInt();

        Rule rule = null;
        if ( stream.readBoolean() ) {
            String pkgName = stream.readUTF();
            String ruleName = stream.readUTF();
            Package pkg = ruleBase.getPackage( pkgName );
            rule = pkg.getRule( ruleName );
        }

        LeftTuple leftTuple = null;
        if ( stream.readBoolean() ) {
            int tuplePos = stream.readInt();
            leftTuple = (LeftTuple) context.terminalTupleMap.get( tuplePos );
        }

        int factHandleId = stream.readInt();
        InternalFactHandle factHandle = context.handles.get( factHandleId );

        long propagationNumber = stream.readLong();
        int activeActivations = stream.readInt();
        int dormantActivations = stream.readInt();
        String entryPointId = stream.readUTF();

        EntryPoint entryPoint = context.entryPoints.get( entryPointId );
        if ( entryPoint == null ) {
            entryPoint = new EntryPoint( entryPointId );
            context.entryPoints.put( entryPointId,
                                     entryPoint );
        }

        PropagationContext pc = new PropagationContextImpl( propagationNumber,
                                                            type,
                                                            rule,
                                                            leftTuple,
                                                            factHandle,
                                                            activeActivations,
                                                            dormantActivations,
                                                            entryPoint );
        context.propagationContexts.put( propagationNumber,
                                         pc );
    }
}
