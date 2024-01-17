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
package org.drools.traits.core.base;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.base.TraitHelper;
import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemoryActions;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.SuperCacheFixer;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.kiesession.entrypoints.NamedEntryPoint;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.ObjectTypeConfigurationRegistry;
import org.drools.core.common.TruthMaintenanceSystemFactory;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.factmodel.ClassDefinition;
import org.drools.base.factmodel.traits.CoreWrapper;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.traits.core.factmodel.LogicalTypeInconsistencyException;
import org.drools.base.factmodel.traits.Thing;
import org.drools.traits.core.factmodel.TraitFactoryImpl;
import org.drools.base.factmodel.traits.TraitFieldTMS;
import org.drools.traits.core.factmodel.TraitProxyImpl;
import org.drools.traits.core.factmodel.TraitRegistryImpl;
import org.drools.base.factmodel.traits.TraitType;
import org.drools.traits.core.factmodel.TraitTypeMapImpl;
import org.drools.base.factmodel.traits.TraitableBean;
import org.drools.traits.core.metadata.Metadatable;
import org.drools.traits.core.metadata.Modify;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.TerminalNode;
import org.drools.base.rule.TypeDeclaration;
import org.drools.core.common.PropagationContext;
import org.drools.traits.core.factmodel.HierarchyEncoder;
import org.drools.util.bitmask.BitMask;
import org.drools.base.beliefsystem.Mode;
import org.drools.traits.core.reteoo.TraitRuntimeComponentFactory;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.traits.core.base.TraitUtils.supersetOrEqualset;
import static org.drools.base.reteoo.PropertySpecificUtil.onlyTraitBitSetMask;

public class TraitHelperImpl implements Externalizable,
                                        TraitHelper {


    private static final Logger LOG = LoggerFactory.getLogger(TraitHelperImpl.class);


    private InternalWorkingMemoryActions              workingMemory;
    private InternalWorkingMemoryEntryPoint           entryPoint;


    public TraitHelperImpl(InternalWorkingMemoryActions workingMemory, InternalWorkingMemoryEntryPoint nep ) {
        this.workingMemory = workingMemory;
        this.entryPoint = nep;
    }

    public TraitHelperImpl() {
    }

    public <T, K> T don(InternalMatch internalMatch, K core, Collection<Class<? extends Thing>> traits, boolean logical, Mode... modes) {
        if ( core instanceof Thing && ( (Thing) core ).getCore() != core ) {
            return don(internalMatch, ((Thing) core).getCore(), traits, logical, modes);
        }
        if ( traits.isEmpty() ) {
            return (T) don(internalMatch, core, Thing.class, logical);
        }
        try {
            T thing = applyManyTraits(internalMatch, core, traits, null, logical, modes);
            return thing;
        } catch ( LogicalTypeInconsistencyException ltie ) {
            LOG.error("Exception", ltie);
            return null;
        }
    }

    public <T, K> T don(InternalMatch internalMatch, K core, Class<T> trait, boolean logical, Mode... modes) {
        return don(internalMatch, core, trait, null, logical, modes);
    }

    public <T, K> T don(InternalMatch internalMatch, K core, Class<T> trait, Modify initArgs, boolean logical, Mode... modes) {
        if ( core instanceof Thing && ( (Thing) core ).getCore() != core ) {
            return don(internalMatch, ((Thing) core).getCore(), trait, initArgs, logical, modes);
        }
        try {
            T thing = applyTrait(internalMatch, core, trait, initArgs, logical, modes);
            return thing;
        } catch ( LogicalTypeInconsistencyException ltie ) {
            LOG.error("Exception", ltie);
            return null;
        }
    }

    protected <T> T doInsertTrait(InternalMatch internalMatch, T thing, Object core, boolean logical, Mode... modes) {
        if ( thing == core ) {
            return thing;
        }

        if ( logical ) {
            insertLogical(internalMatch, thing, modes);
        } else {
            insert(thing, internalMatch);
        }
        return thing;
    }

    private void updateTraits( Object object, BitMask mask, Thing originator, Class<?> modifiedClass, Collection<Thing> traits, InternalMatch internalMatch) {
        updateManyTraits(object, mask, Collections.singletonList(originator), modifiedClass, traits, internalMatch);
    }

    private void updateManyTraits( Object object, BitMask mask, Collection<Thing> originators, Class<?> modifiedClass, Collection<Thing> traits, InternalMatch internalMatch) {

        for ( Thing t : traits ) {
            if ( ! originators.contains( t ) ) {
                InternalFactHandle h = (InternalFactHandle) lookupFactHandle( t );
                if ( h != null ) {
                    NamedEntryPoint nep = (NamedEntryPoint) h.getEntryPoint(workingMemory);
                    PropagationContext propagationContext = nep.getPctxFactory().createPropagationContext(nep.getReteEvaluator().getNextPropagationIdCounter(),
                                                                                                          PropagationContext.Type.MODIFICATION,
                                                                                                          internalMatch != null ? internalMatch.getRule() : null,
                                                                                                          internalMatch != null ? SuperCacheFixer.asTerminalNode(internalMatch.getTuple()) : null,
                                                                                                          h,
                                                                                                          nep.getEntryPoint(),
                                                                                                          mask,
                                                                                                          modifiedClass,
                                                                                                          null );
                    nep.update( h,
                                t,
                                t,
                                nep.getObjectTypeConfigurationRegistry().getObjectTypeConf( t ),
                                propagationContext );
                }
            }
        }
    }

    public void updateTraits( final InternalFactHandle handle, BitMask mask, Class<?> modifiedClass, InternalMatch internalMatch) {

        if (  handle.isTraitable() ) {
            // this is a traitable core object, so its traits must be updated as well
            if ( ((TraitableBean) handle.getObject()).hasTraits() ) {
                updateTraits(handle.getObject(), mask, null, modifiedClass, ((TraitableBean) handle.getObject())._getTraitMap().values(), internalMatch);
            }
        } else if ( handle.isTraiting() ) {
            Thing x = (Thing) handle.getObject();
            // in case this is a proxy
            if ( x != x.getCore() ) {
                Object core = x.getCore();
                InternalFactHandle coreHandle = (InternalFactHandle) getFactHandle( core );
                ((NamedEntryPoint) coreHandle.getEntryPoint(workingMemory)).update(
                        coreHandle,
                        core,
                        mask,
                        modifiedClass,
                        internalMatch);
                updateTraits(core, mask, x, modifiedClass, ((TraitableBean) core)._getTraitMap().values(), internalMatch);
            }
        }
    }


    private <T,K> void refresh( T thing, K core, TraitableBean inner, Class<T> trait, Collection<Thing> mostSpecificTraits, boolean logical, InternalMatch internalMatch) {
        if ( mostSpecificTraits != null ) {
            updateCore(inner, core, trait, logical, internalMatch);
            if ( ! mostSpecificTraits.isEmpty() ) {
                updateTraits(inner, onlyTraitBitSetMask(), (Thing) thing, trait, mostSpecificTraits, internalMatch);
            }
        } else if ( Thing.class == trait ) {
            updateCore(inner, core, trait, logical, internalMatch);
        }
    }

    protected <T, K> T applyManyTraits(InternalMatch internalMatch, K core, Collection<Class<? extends Thing>> traits, Object value, boolean logical, Mode... modes) throws LogicalTypeInconsistencyException {
        // Precondition : traits is not empty, checked by don

        TraitFactoryImpl builder = TraitFactoryImpl.getTraitBuilderForKnowledgeBase(entryPoint.getKnowledgeBase() );

        TraitableBean inner = makeTraitable(core, builder, logical, internalMatch);

        Collection<Thing> mostSpecificTraits = inner.getMostSpecificTraits();
        boolean newTraitsAdded = false;
        T firstThing = null;
        List<Thing> things = new ArrayList<>( traits.size() );

        checkStaticTypeCode( inner );

        for ( Class<?> trait : traits ) {
            boolean needsProxy = trait.isAssignableFrom( inner.getClass() );
            boolean hasTrait = inner.hasTrait( trait.getName() );
            boolean needsUpdate = needsProxy || core != inner;

            if ( ! hasTrait ) {
                T thing = (T) asTrait(core, inner, trait, needsProxy, hasTrait, needsUpdate, builder, logical, internalMatch);

                configureTrait( thing, value );

                things.add( (Thing) thing );

                if ( ! newTraitsAdded && trait != Thing.class ) {
                    firstThing = thing;
                    newTraitsAdded = true;
                }
            }
        }

        for ( Thing t : things ) {
            doInsertTrait(internalMatch, t, core, logical, modes);
        }

        if ( newTraitsAdded ) {
            if ( mostSpecificTraits != null ) {
                updateCore(inner, core, null, logical, internalMatch);
                if ( ! mostSpecificTraits.isEmpty() ) {
                    updateManyTraits(inner, onlyTraitBitSetMask(), things, core.getClass(), mostSpecificTraits, internalMatch);
                }
            }
        }
        return firstThing;
    }

    private void checkStaticTypeCode( TraitableBean inner ) {
        if ( ! inner.hasTraits() ) {
            TraitTypeMapImpl ttm = (TraitTypeMapImpl) inner._getTraitMap();
            if ( ttm != null && ttm.getStaticTypeCode() == null ) {
                TraitRegistryImpl registry = (TraitRegistryImpl) ((TraitRuntimeComponentFactory) RuntimeComponentFactory.get()).getTraitRegistry(this.workingMemory.getKnowledgeBase());
                // code that summarizes ALL the static types
                BitSet staticCode = registry.getStaticTypeCode( inner.getClass().getName() );
                ttm.setStaticTypeCode( staticCode );
                if ( staticCode != null ) {
                    for ( String staticTrait : registry.getStaticTypes( inner.getClass().getName() ) ) {
                        ttm.addStaticTrait( staticTrait, registry.getHierarchy().getCode( staticTrait ) );
                    }
                }
            }
        }
    }

    protected <T, K> T applyTrait(InternalMatch internalMatch, K core, Class<T> trait, Object value, boolean logical, Mode... modes) throws LogicalTypeInconsistencyException {
        TraitFactoryImpl builder = TraitFactoryImpl.getTraitBuilderForKnowledgeBase(entryPoint.getKnowledgeBase() );

        TraitableBean inner = makeTraitable(core, builder, logical, internalMatch);

        boolean needsProxy = trait.isAssignableFrom( inner.getClass() );
        boolean hasTrait = inner.hasTrait( trait.getName() );
        boolean needsUpdate = needsProxy || core != inner;

        checkStaticTypeCode( inner );

        Collection<Thing> mostSpecificTraits = getTraitBoundary( inner, needsProxy, hasTrait, trait );

        T thing = asTrait(core, inner, trait, needsProxy, hasTrait, needsUpdate, builder, logical, internalMatch);

        configureTrait( thing, value );

        thing = doInsertTrait(internalMatch, thing, core, logical, modes);

        refresh(thing, core, inner, trait, mostSpecificTraits, logical, internalMatch);

        if ( trait != Thing.class && inner._getFieldTMS() != null ) {
            inner._getFieldTMS().resetModificationMask();
        }
        return thing;
    }

    private <T> void updateCore( TraitableBean inner, Object core, Class<T> trait, boolean logical, InternalMatch internalMatch) {
        FactHandle handle = lookupFactHandle( inner );
        InternalFactHandle h = (InternalFactHandle) handle;
        if ( handle != null ) {
            TraitFieldTMS fieldTMS = inner._getFieldTMS();
            BitMask mask = fieldTMS == null ? onlyTraitBitSetMask() : fieldTMS.getModificationMask();

            Object o = h.getObject();
            NamedEntryPoint nep = (NamedEntryPoint) h.getEntryPoint(workingMemory);
            PropagationContext propagationContext = nep.getPctxFactory().createPropagationContext(nep.getReteEvaluator().getNextPropagationIdCounter(),
                                                                                                  PropagationContext.Type.MODIFICATION,
                                                                                                  internalMatch.getRule(),
                                                                                                  SuperCacheFixer.asTerminalNode(internalMatch.getTuple()),
                                                                                                  h,
                                                                                                  nep.getEntryPoint(),
                                                                                                  mask,
                                                                                                  core.getClass(),
                                                                                                  null );
            nep.update( h,
                        o,
                        o,
                        nep.getObjectTypeConfigurationRegistry().getObjectTypeConf( o ),
                        propagationContext );
        } else {
            handle = this.workingMemory.insert(inner,
                                               false,
                                               internalMatch.getRule(),
                                               SuperCacheFixer.asTerminalNode(internalMatch.getTuple()));
        }

    }

    public <T,K,X extends TraitableBean> Thing<K> shed( TraitableBean<K,X> core, Class<T> trait, InternalMatch internalMatch) {
        if ( trait.isAssignableFrom( core.getClass() ) ) {
            Collection<Thing<K>> removedTypes = core.removeTrait( trait.getName() );
            if ( ! removedTypes.isEmpty() ) {
                reassignNodes( core, removedTypes );
                FactHandle factHandle = getFactHandle(core);
                update(factHandle, onlyTraitBitSetMask(), core.getClass(), internalMatch);
                //updateTraits( core, Long.MIN_VALUE, null, core.getClass(), null, ((TraitableBean) core).getMostSpecificTraits()  );
            }
            if ( core instanceof Thing ) {
                return (Thing<K>) core;
            } else {
                return null;
            }
        } else {
            Collection<Thing<K>> removedTypes;
            Thing<K> thing = core.getTrait( Thing.class.getName() );
            if ( trait == Thing.class ) {

                removedTypes = new ArrayList<>( core._getTraitMap().values() );
                for ( Thing t : removedTypes ) {
                    if ( ! ((TraitType) t)._isVirtual() ) {
                        delete(getFactHandle( t ), internalMatch);
                    }
                }

                core._getTraitMap().clear();
                core._setTraitMap( null );
                return thing;
            } else if ( core.hasTrait( trait.getName() ) ) {
                removedTypes = core.removeTrait( trait.getName() );
            } else {
                HierarchyEncoder hier = ((TraitRuntimeComponentFactory) RuntimeComponentFactory.get()).getTraitRegistry(this.workingMemory.getKnowledgeBase()).getHierarchy();
                BitSet code = hier.getCode( trait.getName() );
                removedTypes = core.removeTrait( code );
            }

            removedTypes = new ArrayList<>( removedTypes );
            reassignNodes( core, removedTypes );
            for ( Thing t : removedTypes ) {
                if ( ! ((TraitType) t)._isVirtual() ) {
                    InternalFactHandle handle = (InternalFactHandle) getFactHandle( t );
                    if ( handle.getEqualityKey() != null && handle.getEqualityKey().getLogicalFactHandle() == handle ) {
                        TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem(entryPoint).delete( handle );
                    } else {
                        delete(getFactHandle( t ), internalMatch);
                    }
                }
            }

            if ( ! core.hasTraits() ) {
                don(internalMatch, core, Thing.class, false);
            } else if ( ! removedTypes.isEmpty() ) {
                update(getFactHandle( core ), onlyTraitBitSetMask(), core.getClass(), internalMatch);
                //updateTraits( core, Long.MIN_VALUE, null, core.getClass(), null, ((TraitableBean) core).getMostSpecificTraits()  );
            }
            return thing;
        }
    }

    private <K, X extends TraitableBean> void reassignNodes( TraitableBean<K, X> core, Collection<Thing<K>> removedTraits ) {
        if ( ! core.hasTraits() ) {
            return;
        }
        Collection<Thing<K>> mst = ( (TraitTypeMapImpl) core._getTraitMap() ).getMostSpecificTraits();
        for ( Thing<K> shedded : removedTraits ) {
            for ( BitSet bs : ( (TraitProxyImpl) shedded ).listAssignedOtnTypeCodes() ) {
                boolean found = false;
                for ( Thing<K> tp : mst ) {
                    TraitProxyImpl candidate = (TraitProxyImpl) tp;
                    if ( supersetOrEqualset( candidate._getTypeCode(), bs ) ) {
                        candidate.assignOtn( bs );
                        found = true;
                        break;
                    }
                }
                if ( found ) {
                }
            }
        }
    }


    protected <K> Collection<Thing> getTraitBoundary( TraitableBean<K,?> inner, boolean needsProxy, boolean hasTrait, Class trait ) {
        boolean refresh = ! needsProxy && ! hasTrait && Thing.class != trait;
        if ( ! refresh ) {
            return null;
        }

        if ( inner._getTraitMap() == null || inner instanceof Thing ) return Collections.EMPTY_LIST;
        if ( inner._getTraitMap().isEmpty() ) return null;

        Collection<Thing> ts = new ArrayList<>();
        for ( Thing t : inner._getTraitMap().values() )     {
            if ( t instanceof TraitProxyImpl) {
                if ( ( (TraitProxyImpl) t ).hasOtns() ) {
                    ts.add( t );
                }
            }
        }
        return ts;
    }

    private <T, K> T asTrait(K core, TraitableBean inner, Class<T> trait, boolean needsProxy, boolean hasTrait, boolean needsUpdate, TraitFactoryImpl builder, boolean logical, InternalMatch internalMatch) throws LogicalTypeInconsistencyException {
        T thing;
        if ( needsProxy ) {
            thing = (T) inner;
            inner.addTrait( trait.getName(), (Thing<K>) core );
        } else if ( hasTrait ) {
            thing = (T) inner.getTrait( trait.getName() );
        } else {
            thing = (T) builder.getProxy( inner, trait, logical );
        }

        if ( needsUpdate ) {
            InternalFactHandle h = (InternalFactHandle) lookupFactHandle( core );
            if ( h == null ) {
                h = lookupHandleForWrapper( core );
            }
            if ( h == null ) {
                h = (InternalFactHandle) this.workingMemory.insert(core,
                                                                   false,
                                                                   internalMatch.getRule(),
                                                                   SuperCacheFixer.asTerminalNode(internalMatch.getTuple()));
            }
            if ( ! h.isTraitOrTraitable() ) {
                throw new IllegalStateException( "A traited working memory element is being used with a default fact handle. " +
                                                 "Please verify that its class was declared as @Traitable : " + core.getClass().getName() );
            }
            this.update(h, inner, internalMatch);
        }

        return thing;
    }

    private <K> TraitableBean makeTraitable(K core, TraitFactoryImpl builder, boolean logical, InternalMatch internalMatch) {
        boolean needsWrapping = ! ( core instanceof TraitableBean );

        ClassDefinition coreDef = lookupClassDefinition( core );
        TraitableBean<K,? extends TraitableBean> inner = needsWrapping ? builder.asTraitable( core, coreDef ) : (TraitableBean<K,? extends TraitableBean>) core;
        if ( needsWrapping ) {
            InternalFactHandle h = (InternalFactHandle) lookupFactHandle( core );
            WorkingMemoryEntryPoint ep = h != null ? h.getEntryPoint(workingMemory) : workingMemory.getEntryPoint("DEFAULT");
            ObjectTypeConfigurationRegistry reg = ep.getObjectTypeConfigurationRegistry();

            ObjectTypeConf coreConf = reg.getOrCreateObjectTypeConf( ep.getEntryPoint(), core );

            ObjectTypeConf innerConf = reg.getOrCreateObjectTypeConf( ep.getEntryPoint(), inner );
            if ( coreConf.isTMSEnabled() ) {
                innerConf.enableTMS();
            }
            if ( inner._getFieldTMS() != null && inner._getFieldTMS().needsInit() ) {
                inner._getFieldTMS().init( workingMemory );
            }
        } else {
            TraitFieldTMS ftms = inner._getFieldTMS();
            if ( ftms != null ) {
                FactHandle handle = lookupFactHandle( inner );
                if ( handle == null ) {
                    handle = this.workingMemory.insert(inner,
                                                       false,
                                                       internalMatch.getRule(),
                                                       SuperCacheFixer.asTerminalNode(internalMatch.getTuple()));
                }
                if ( ftms.needsInit() ) {
                    ftms.init( workingMemory );
                }
            }
        }
        return inner;
    }

    protected  <K> ClassDefinition lookupClassDefinition( K core ) {
        InternalKnowledgePackage pack = workingMemory.getKnowledgeBase().getPackage( core.getClass().getPackage().getName() );
        if ( pack != null ) {
            TypeDeclaration decl = pack.getTypeDeclaration( core.getClass() );
            if ( decl != null ) {
                return decl.getTypeClassDef();
            }
        }
        return null;
    }

    private <K> InternalFactHandle lookupHandleForWrapper( K core ) {
        for ( EntryPoint ep : workingMemory.getEntryPoints() ) {
            ObjectStore store = ((WorkingMemoryEntryPoint) ep).getObjectStore();
            Iterator<InternalFactHandle> iter = store.iterateFactHandles();
            while ( iter.hasNext() ) {
                InternalFactHandle handle = iter.next();
                if ( handle.isTraitable() && handle.getObject() instanceof CoreWrapper && ( (CoreWrapper) handle.getObject() ).getCore() == core ) {
                    return handle;
                }
            }
        }
        return null;
    }

    public FactHandle lookupFactHandle(Object object) {
        FactHandle handle = getFactHandleFromWM( object );
        if ( handle == null && object instanceof CoreWrapper ) {
        	handle = getFactHandleFromWM( ((CoreWrapper) object).getCore() );
        }
        return handle;
    }


    protected <T> void configureTrait( T thing, Object value ) {
        if ( value instanceof Modify && thing instanceof Metadatable) {
            Modify modify = (Modify) value;
            modify.call(thing);
        }
    }

    private FactHandle getFactHandleFromWM(final Object object) {
        FactHandle handle = null;
        // entry point null means it is a generated fact, not a regular inserted fact
        // NOTE: it would probably be a good idea to create a specific attribute for that
            handle = entryPoint.getFactHandle(object);
        if ( handle != null ) {
        }
        return handle;
    }

    public FactHandle getFactHandle(Object object) {
        FactHandle handle = getFactHandleFromWM( object );
        if ( handle == null ) {
            if ( object instanceof CoreWrapper ) {
                handle = getFactHandleFromWM( ((CoreWrapper) object).getCore() );
            }
            if ( handle == null ) {
                throw new RuntimeException( "Update error: handle not found for object: " + object + ". Is it in the working memory?" );
            }
        }
        return handle;
    }


    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        workingMemory = (InternalWorkingMemoryActions) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( workingMemory );
    }


    public void update(final FactHandle handle,
                       final Object newObject,
                       final InternalMatch internalMatch){
        InternalFactHandle h = (InternalFactHandle) handle;
        h.getEntryPoint(workingMemory).update( h,
                                  newObject,
                                  onlyTraitBitSetMask(),
                                  newObject.getClass(),
                                               internalMatch);
    }

    public void update( final FactHandle handle,
                        BitMask mask,
                        Class<?> modifiedClass,
                        InternalMatch internalMatch) {
        InternalFactHandle h = (InternalFactHandle) handle;
        ((NamedEntryPoint) h.getEntryPoint(workingMemory)).update( h,
                                                      ((InternalFactHandle)handle).getObject(),
                                                      mask,
                                                      modifiedClass,
                                                                   internalMatch);
        if ( h.isTraitOrTraitable() ) {
            workingMemory.updateTraits(h, mask, modifiedClass, internalMatch);
        }
    }

    public void delete( final FactHandle handle, InternalMatch internalMatch) {
        ((InternalFactHandle) handle).getEntryPoint(workingMemory).delete(handle,
                                                                          internalMatch.getRule(),
                                                                          SuperCacheFixer.asTerminalNode(internalMatch.getTuple()));
    }

    public FactHandle insert(final Object object,
                             final InternalMatch internalMatch) {
        FactHandle handle = this.workingMemory.insert(object,
                                                      false,
                                                      internalMatch.getRule(),
                                                      SuperCacheFixer.asTerminalNode(internalMatch.getTuple()));
        return handle;
    }

    public void insertLogical(final InternalMatch internalMatch,
                              final Object object,
                              final Mode... modes ) {

        if ( !internalMatch.isMatched() ) {
            // Activation is already unmatched, can't do logical insertions against it
            return;
        }
        // iterate to find previous equal logical insertion
        TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem(workingMemory)
                .insert(object, modes, internalMatch);

    }

    public void deleteWMAssertedTraitProxies( InternalFactHandle handle, RuleImpl rule, TerminalNode terminalNode ) {
        TraitableBean traitableBean = (TraitableBean) handle.getObject();
        if( traitableBean.hasTraits() ){
            PriorityQueue<TraitProxyImpl> removedTypes =
                    new PriorityQueue<>(traitableBean._getTraitMap().values().size() );
            removedTypes.addAll( traitableBean._getTraitMap().values() );

            while ( ! removedTypes.isEmpty() ) {
                TraitProxyImpl proxy = removedTypes.poll();
                if ( ! proxy._isVirtual() ) {
                    InternalFactHandle proxyHandle = (InternalFactHandle) getFactHandle( proxy );
                    if ( proxyHandle.getEqualityKey() == null || proxyHandle.getEqualityKey().getLogicalFactHandle() != proxyHandle ) {
                        entryPoint.delete( proxyHandle,
                                           rule,
                                           terminalNode );
                    }
                }
            }
        }
    }

    @Override
    public <K> K extractTrait( InternalFactHandle handle, Class<K> klass ) {
        TraitableBean tb;
        if ( handle.isTraitable() ) {
            tb = (TraitableBean) handle.getObject();
        } else if ( handle.isTraiting() ) {
            tb = ((TraitProxyImpl) handle.getObject()).getObject();
        } else {
            return null;
        }
        K k = (K) tb.getTrait( klass.getCanonicalName() );
        if ( k != null ) {
            return k;
        }
        for ( Object t : tb.getMostSpecificTraits() ) {
            if ( klass.isAssignableFrom( t.getClass() ) ) {
                return (K) t;
            }
        }
        return null;
    }

    public void replaceCore( InternalFactHandle handle, Object object, Object originalObject, BitMask modificationMask, Class<? extends Object> aClass, InternalMatch internalMatch) {
        TraitableBean src = (TraitableBean) originalObject;
        TraitableBean tgt = (TraitableBean) object;
        tgt._setTraitMap( src._getTraitMap() );
        tgt._setDynamicProperties( src._getDynamicProperties() );
        tgt._setFieldTMS( src._getFieldTMS() );

        updateTraits(handle, modificationMask, object.getClass(), internalMatch);
    }
}
