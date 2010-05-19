package org.drools.common;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.drools.FactException;
import org.drools.core.util.ObjectHashMap;
import org.drools.marshalling.impl.MarshallerReaderContext;
import org.drools.marshalling.impl.MarshallerWriteContext;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;

/**
 * The Truth Maintenance System is responsible for tracking two things. Firstly
 * It maintains a Map to track the classes with the same Equality, using the
 * EqualityKey. The EqualityKey has an internal datastructure which references
 * all the handles which are equal. Secondly It maintains another map tracking
 * the  justificiations for logically asserted facts.
 *
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 *
 */
public class TruthMaintenanceSystem {

    private static final long     serialVersionUID = 400L;

    private AbstractWorkingMemory workingMemory;

    private ObjectHashMap         justifiedMap;

    private ObjectHashMap         assertMap;

    public TruthMaintenanceSystem() {
    }

    public TruthMaintenanceSystem(final AbstractWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;

        this.justifiedMap = new ObjectHashMap();
        this.assertMap = new ObjectHashMap();
        this.assertMap.setComparator( EqualityKeyComparator.getInstance() );
    }

    //    public void write(WMSerialisationOutContext context) throws IOException {
    //        ObjectOutputStream stream = context.stream;
    //        
    //        EqualityKey[] keys = new EqualityKey[ this.assertMap.size() ];
    //        org.drools.util.Iterator it = this.assertMap.iterator();
    //        int i = 0;
    //        for ( ObjectEntry entry = ( ObjectEntry ) it.next(); entry != null; entry = ( ObjectEntry ) it.next() ) {
    //            EqualityKey key = ( EqualityKey ) entry.getKey();
    //            keys[i++] = key;
    //        }
    //        
    //        Arrays.sort( keys, EqualityKeySorter.instance );
    //        
    //        // write the assert map of Equality keys
    //        for ( EqualityKey key : keys ) {
    //            stream.writeInt( PersisterEnums.EQUALITY_KEY );
    //            stream.writeInt( key.getStatus() );
    //            InternalFactHandle handle = key.getFactHandle();
    //            stream.writeInt( handle.getId() );
    //            context.out.println( "EqualityKey int:" + key.getStatus() + " int:" + handle.getId() );
    //            if ( key.getOtherFactHandle() != null && !key.getOtherFactHandle().isEmpty() ) {
    //                for ( InternalFactHandle handle2 : key.getOtherFactHandle() ) {
    //                    stream.writeInt( PersisterEnums.FACT_HANDLE );
    //                    stream.writeInt( handle2.getId() );
    //                    context.out.println( "OtherHandle int:" +  handle2.getId() );
    //                }
    //            }
    //            stream.writeInt( PersisterEnums.END );
    //        }
    //        stream.writeInt( PersisterEnums.END );        
    //    }
    //    
    //    public static class EqualityKeySorter implements Comparator<EqualityKey> {
    //        public static final EqualityKeySorter instance = new EqualityKeySorter();
    //        public int compare(EqualityKey key1,
    //                           EqualityKey key2) {
    //            return key1.getFactHandle().getId() - key2.getFactHandle().getId();
    //        }        
    //    }        
    //    
    //    public void read(WMSerialisationInContext context) throws IOException {
    //        ObjectInputStream stream = context.stream;
    //        
    //        while ( stream.readInt() == PersisterEnums.EQUALITY_KEY ) {
    //            int status = stream.readInt();
    //            InternalFactHandle handle = ( InternalFactHandle ) context.handles.get( stream.readInt() );
    //            EqualityKey key = new EqualityKey(handle, status);     
    //            handle.setEqualityKey( key );
    //            while ( stream.readInt() == PersisterEnums.FACT_HANDLE ) {
    //                handle = ( InternalFactHandle ) context.wm.getFactHandle( stream.readInt() );
    //                key.addFactHandle( handle );
    //                handle.setEqualityKey( key );
    //            } 
    //            put( key );
    //        }        
    //    }

    //    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    //        workingMemory   = (AbstractWorkingMemory)in.readObject();
    //        justifiedMap   = (PrimitiveLongMap)in.readObject();
    //        assertMap   = (ObjectHashMap)in.readObject();
    //    }
    //
    //    public void writeExternal(ObjectOutput out) throws IOException {
    //        out.writeObject(workingMemory);
    //        out.writeObject(justifiedMap);
    //        out.writeObject(assertMap);
    //    }

    public ObjectHashMap getJustifiedMap() {
        return this.justifiedMap;
    }

    public ObjectHashMap getAssertMap() {
        return this.assertMap;
    }

    public Object put(final EqualityKey key) {
        return this.assertMap.put( key,
                                   key,
                                   false );
    }

    public EqualityKey get(final EqualityKey key) {
        return (EqualityKey) this.assertMap.get( key );
    }

    public EqualityKey get(final Object object) {
        return (EqualityKey) this.assertMap.get( object );
    }

    public EqualityKey remove(final EqualityKey key) {
        return (EqualityKey) this.assertMap.remove( key );
    }

    /**
     * An Activation is no longer true so it no longer justifies  any  of the logical facts
     * it logically  asserted. It iterates  over the Activation's LinkedList of DependencyNodes
     * it retrieves the justitication  set for each  DependencyNode's FactHandle and  removes
     * itself. If the Set is empty it retracts the FactHandle from the WorkingMemory.
     *
     * @param activation
     * @param context
     * @param rule
     * @throws FactException
     */
    public void removeLogicalDependencies(final Activation activation,
                                          final PropagationContext context,
                                          final Rule rule) throws FactException {
        final org.drools.core.util.LinkedList list = activation.getLogicalDependencies();
        if ( list == null || list.isEmpty() ) {
            return;
        }
        
        for ( LogicalDependency node = (LogicalDependency) list.getFirst(); node != null; node = (LogicalDependency) node.getNext() ) {
            removeLogicalDependency( activation, node, context );
        }
    }
    
    public void removeLogicalDependency(final Activation activation,
                                        final LogicalDependency node,
                                        final PropagationContext context) {
        final InternalFactHandle handle = (InternalFactHandle) node.getFactHandle();
        final Set set = (Set) this.justifiedMap.get( handle.getId() );
        if ( set != null ) {
            set.remove( node );
            WorkingMemoryAction action = new LogicalRetractCallback( this,
                                                                     node,
                                                                     set,
                                                                     handle,
                                                                     context,
                                                                     activation );
            workingMemory.queueWorkingMemoryAction( action );
        }
    }

    public static class LogicalRetractCallback
        implements
        WorkingMemoryAction {
        private TruthMaintenanceSystem tms;
        private LogicalDependency      node;
        private Set                    set;
        private InternalFactHandle     handle;
        private PropagationContext     context;
        private Activation             activation;

        public LogicalRetractCallback() {

        }

        public LogicalRetractCallback(final TruthMaintenanceSystem tms,
                                      final LogicalDependency node,
                                      final Set set,
                                      final InternalFactHandle handle,
                                      final PropagationContext context,
                                      final Activation activation) {
            this.tms = tms;
            this.node = node;
            this.set = set;
            this.handle = handle;
            this.context = context;
        }

        public LogicalRetractCallback(MarshallerReaderContext context) throws IOException {
            this.tms = context.wm.getTruthMaintenanceSystem();            
            
            this.handle = context.handles.get( context.readInt() );
            this.context = context.propagationContexts.get( context.readLong() );
            this.activation = (Activation) context.terminalTupleMap.get( context.readInt() ).getObject();
            
            this.set = ( Set ) this.tms.getJustifiedMap().get( handle.getId() ); 
           
            for ( Iterator it = this.set.iterator(); it.hasNext(); ) {
                LogicalDependency node = ( LogicalDependency ) it.next();
                if ( node.getJustifier() == this.activation ) {
                    this.node = node;
                    break;
                }
            }
        }

        public void write(MarshallerWriteContext context) throws IOException {
            context.writeInt( WorkingMemoryAction.LogicalRetractCallback );
            
            context.writeInt( this.handle.getId() );
            context.writeLong( this.context.getPropagationNumber() );
            context.writeInt( context.terminalTupleMap.get( this.activation.getTuple() ) );
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            tms = (TruthMaintenanceSystem) in.readObject();
            node = (LogicalDependency) in.readObject();
            set = (Set) in.readObject();
            handle = (InternalFactHandle) in.readObject();
            context = (PropagationContext) in.readObject();
            activation = (Activation) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( tms );
            out.writeObject( node );
            out.writeObject( set );
            out.writeObject( handle );
            out.writeObject( context );
            out.writeObject( activation );
        }

        public void execute(InternalWorkingMemory workingMemory) {
            if ( set.isEmpty() ) {
                this.tms.getJustifiedMap().remove( handle.getId() );
                // this needs to be scheduled so we don't upset the current
                // working memory operation
                workingMemory.retract( this.handle,
                                       false,
                                       true,
                                       (Rule) context.getRuleOrigin(),
                                       this.activation );
            }
        }
    }

    /**
     * The FactHandle is being removed from the system so remove any logical dependencies
     * between the  justified FactHandle and its justifiers. Removes the FactHandle key
     * from the justifiedMap. It then iterates over all the LogicalDependency nodes, if any,
     * in the returned Set and removes the LogicalDependency node from the LinkedList maintained
     * by the Activation.
     *
     * @see LogicalDependency
     *
     * @param handle - The FactHandle to be removed
     * @throws FactException
     */
    public void removeLogicalDependencies(final InternalFactHandle handle) throws FactException {
        final Set set = (Set) this.justifiedMap.remove( handle.getId() );
        if ( set != null && !set.isEmpty() ) {
            for ( final Iterator it = set.iterator(); it.hasNext(); ) {
                final LogicalDependency node = (LogicalDependency) it.next();
                node.getJustifier().getLogicalDependencies().remove( node );
            }
        }
    }

    /**
     * Adds a justification for the FactHandle to the justifiedMap.
     *
     * @param handle
     * @param activation
     * @param context
     * @param rule
     * @throws FactException
     */
    public void addLogicalDependency(final InternalFactHandle handle,
                                     final Activation activation,
                                     final PropagationContext context,
                                     final Rule rule) throws FactException {
        final LogicalDependency node = new LogicalDependency( activation,
                                                              handle );
        activation.getRule().setHasLogicalDependency( true );

        activation.addLogicalDependency( node );
        Set set = (Set) this.justifiedMap.get( handle.getId() );
        if ( set == null ) {
            if ( context.getType() == PropagationContext.MODIFICATION ) {
                // if this was a  update, chances  are its trying  to retract a logical assertion
            }
            set = new HashSet();
            this.justifiedMap.put( handle.getId(),
                                   set );
        }
        set.add( node );
    }
    
    public void clear() {
        this.justifiedMap.clear();
        this.assertMap.clear();
    }
}
