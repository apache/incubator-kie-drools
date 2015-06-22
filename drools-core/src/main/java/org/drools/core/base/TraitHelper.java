package org.drools.core.base;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemoryActions;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.ObjectTypeConfigurationRegistry;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.traits.CoreWrapper;
import org.drools.core.factmodel.traits.LogicalTypeInconsistencyException;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.TraitFieldTMS;
import org.drools.core.factmodel.traits.TraitProxy;
import org.drools.core.factmodel.traits.TraitRegistry;
import org.drools.core.factmodel.traits.TraitType;
import org.drools.core.factmodel.traits.TraitTypeMap;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.metadata.Metadatable;
import org.drools.core.metadata.Modify;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.HierarchyEncoder;
import org.drools.core.util.HierarchyEncoderImpl;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.runtime.beliefs.Mode;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import static org.drools.core.reteoo.PropertySpecificUtil.onlyTraitBitSetMask;

public class TraitHelper implements Externalizable {


    private InternalWorkingMemoryActions              workingMemory;
    private NamedEntryPoint                           entryPoint;


    public TraitHelper( InternalWorkingMemoryActions workingMemory, NamedEntryPoint nep ) {
        this.workingMemory = workingMemory;
        this.entryPoint = nep;
    }

    public <T, K> T don( Activation activation, K core, Collection<Class<? extends Thing>> traits, boolean logical, Mode... modes ) {
        if ( core instanceof Thing && ( (Thing) core ).getCore() != core ) {
            return don( activation, ((Thing) core).getCore(), traits, logical, modes );
        }
        if ( traits.isEmpty() ) {
            return (T) don( activation, core, Thing.class, logical );
        }
        try {
            T thing = applyManyTraits( activation, core, traits, null, logical, modes );
            return thing;
        } catch ( LogicalTypeInconsistencyException ltie ) {
            ltie.printStackTrace();
            return null;
        }
    }

    public <T, K> T don( Activation activation, K core, Class<T> trait, boolean logical, Mode... modes ) {
        return don( activation, core, trait, null, logical, modes );
    }

    public <T, K> T don( Activation activation, K core, Class<T> trait, Modify initArgs, boolean logical, Mode... modes ) {
        if ( core instanceof Thing && ( (Thing) core ).getCore() != core ) {
            return don( activation, ((Thing) core).getCore(), trait, initArgs, logical, modes );
        }
        try {
            T thing = applyTrait( activation, core, trait, initArgs, logical, modes );
            return thing;
        } catch ( LogicalTypeInconsistencyException ltie ) {
            ltie.printStackTrace();
            return null;
        }
    }

    protected <T> T doInsertTrait( Activation activation, T thing, Object core, boolean logical, Mode... modes ) {
        if ( thing == core ) {
            return thing;
        }

        if ( logical ) {
            insertLogical( activation, thing, modes );
        } else {
            insert( thing, activation );
        }
        return thing;
    }

    private void updateTraits( Object object, BitMask mask, Thing originator, Class<?> modifiedClass, Collection<Thing> traits, Activation activation ) {
        updateManyTraits( object, mask, Arrays.asList( originator ), modifiedClass, traits, activation );
    }

    private void updateManyTraits( Object object, BitMask mask, Collection<Thing> originators, Class<?> modifiedClass, Collection<Thing> traits, Activation activation ) {

        for ( Thing t : traits ) {
            if ( ! originators.contains( t ) ) {
                InternalFactHandle h = (InternalFactHandle) lookupFactHandle( t );
                if ( h != null ) {
                    NamedEntryPoint nep = (NamedEntryPoint) h.getEntryPoint();
                    PropagationContext propagationContext = nep.getPctxFactory().createPropagationContext( nep.getInternalWorkingMemory().getNextPropagationIdCounter(),
                                                                                                           PropagationContext.MODIFICATION,
                                                                                                           activation != null ? activation.getRule() : null,
                                                                                                           activation != null ? activation.getTuple() : null,
                                                                                                           h,
                                                                                                           nep.getEntryPoint(),
                                                                                                           mask,
                                                                                                           modifiedClass,
                                                                                                           null );
                    nep.update( h,
                                t,
                                t,
                                nep.getObjectTypeConfigurationRegistry().getObjectTypeConf( nep.getEntryPoint(), t ),
                                activation != null ? activation.getRule() : null,
                                propagationContext );
                }
            }
        }
    }

    public void updateTraits( final InternalFactHandle handle, BitMask mask, Class<?> modifiedClass, Activation activation ) {

        if (  handle.isTraitable() ) {
            // this is a traitable core object, so its traits must be updated as well
            if ( ((TraitableBean) handle.getObject()).hasTraits() ) {
                updateTraits( handle.getObject(), mask, null, modifiedClass, ((TraitableBean) handle.getObject())._getTraitMap().values(), activation );
            }
        } else if ( handle.isTraiting() ) {
            Thing x = (Thing) handle.getObject();
            // in case this is a proxy
            if ( x != x.getCore() ) {
                Object core = x.getCore();
                InternalFactHandle coreHandle = (InternalFactHandle) getFactHandle( core );
                ((NamedEntryPoint) coreHandle.getEntryPoint()).update(
                        coreHandle,
                        core,
                        mask,
                        modifiedClass,
                        activation );
                updateTraits( core, mask, x, modifiedClass, ((TraitableBean) core)._getTraitMap().values(), activation );
            }
        }
    }


    private <T,K> void refresh( T thing, K core, TraitableBean inner, Class<T> trait, Collection<Thing> mostSpecificTraits, boolean logical, Activation activation ) {
        if ( mostSpecificTraits != null ) {
            updateCore( inner, core, trait, logical, activation );
            if ( ! mostSpecificTraits.isEmpty() ) {
                updateTraits( inner, onlyTraitBitSetMask(), (Thing) thing, trait, mostSpecificTraits, activation );
            }
        } else if ( Thing.class == trait ) {
            updateCore( inner, core, trait, logical,activation );
        }
    }

    protected <T, K> T applyManyTraits( Activation activation, K core, Collection<Class<? extends Thing>> traits, Object value, boolean logical, Mode... modes ) throws LogicalTypeInconsistencyException {
        // Precondition : traits is not empty, checked by don

        TraitFactory builder = TraitFactory.getTraitBuilderForKnowledgeBase( entryPoint.getKnowledgeBase() );

        TraitableBean inner = makeTraitable( core, builder, logical, activation );

        Collection<Thing> mostSpecificTraits = inner.getMostSpecificTraits();
        boolean newTraitsAdded = false;
        T firstThing = null;
        List<Thing> things = new ArrayList<Thing>( traits.size() );

        checkStaticTypeCode( inner );

        for ( Class<?> trait : traits ) {
            boolean needsProxy = trait.isAssignableFrom( inner.getClass() );
            boolean hasTrait = inner.hasTrait( trait.getName() );
            boolean needsUpdate = needsProxy || core != inner;

            if ( ! hasTrait ) {
                T thing = (T) asTrait( core, inner, trait, needsProxy, hasTrait, needsUpdate, builder, logical, activation );

                configureTrait( thing, value );

                things.add( (Thing) thing );

                if ( ! newTraitsAdded && trait != Thing.class ) {
                    firstThing = thing;
                    newTraitsAdded = true;
                }
            }
        }

        for ( Thing t : things ) {
            doInsertTrait( activation, t, core, logical, modes );
        }

        if ( newTraitsAdded ) {
            if ( mostSpecificTraits != null ) {
                updateCore( inner, core, null, logical, activation );
                if ( ! mostSpecificTraits.isEmpty() ) {
                    updateManyTraits( inner, onlyTraitBitSetMask(), things, core.getClass(), mostSpecificTraits, activation );
                }
            }
        }
        return firstThing;
    }

    private void checkStaticTypeCode( TraitableBean inner ) {
        if ( ! inner.hasTraits() ) {
            TraitTypeMap ttm = (TraitTypeMap) inner._getTraitMap();
            if ( ttm != null && ttm.getStaticTypeCode() == null ) {
                TraitRegistry registry = this.workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getTraitRegistry();
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

    protected <T, K> T applyTrait( Activation activation, K core, Class<T> trait, Object value, boolean logical, Mode... modes ) throws LogicalTypeInconsistencyException {
        TraitFactory builder = TraitFactory.getTraitBuilderForKnowledgeBase( entryPoint.getKnowledgeBase() );

        TraitableBean inner = makeTraitable( core, builder, logical, activation );

        boolean needsProxy = trait.isAssignableFrom( inner.getClass() );
        boolean hasTrait = inner.hasTrait( trait.getName() );
        boolean needsUpdate = needsProxy || core != inner;

        checkStaticTypeCode( inner );

        BitSet boundary = inner.getCurrentTypeCode() != null ? (BitSet) inner.getCurrentTypeCode().clone() : null;

        Collection<Thing> mostSpecificTraits = getTraitBoundary( inner, needsProxy, hasTrait, trait );

        T thing = asTrait( core, inner, trait, needsProxy, hasTrait, needsUpdate, builder, logical, activation );

        configureTrait( thing, value );

        thing = doInsertTrait( activation, thing, core, logical, modes );

        refresh( thing, core, inner, trait, mostSpecificTraits, logical, activation );

        if ( trait != Thing.class && inner._getFieldTMS() != null ) {
            inner._getFieldTMS().resetModificationMask();
        }
        return thing;
    }

    private <T> void updateCore( TraitableBean inner, Object core, Class<T> trait, boolean logical, Activation activation ) {
        FactHandle handle = lookupFactHandle( inner );
        InternalFactHandle h = (InternalFactHandle) handle;
        if ( handle != null ) {
            TraitFieldTMS fieldTMS = inner._getFieldTMS();
            BitMask mask = fieldTMS == null ? onlyTraitBitSetMask() : fieldTMS.getModificationMask();

            Object o = h.getObject();
            NamedEntryPoint nep = (NamedEntryPoint) h.getEntryPoint();
            PropagationContext propagationContext = nep.getPctxFactory().createPropagationContext( nep.getInternalWorkingMemory().getNextPropagationIdCounter(),
                                                                                                   PropagationContext.MODIFICATION,
                                                                                                   activation.getRule(),
                                                                                                   activation.getTuple(),
                                                                                                   h,
                                                                                                   nep.getEntryPoint(),
                                                                                                   mask,
                                                                                                   core.getClass(),
                                                                                                   null );
            nep.update( h,
                        o,
                        o,
                        nep.getObjectTypeConfigurationRegistry().getObjectTypeConf( nep.getEntryPoint(), o ),
                        activation.getRule(),
                        propagationContext );
        } else {
            handle = this.workingMemory.insert( inner,
                                                null,
                                                false,
                                                logical,
                                                activation.getRule(),
                                                activation );
        }

    }

    public <T,K,X extends TraitableBean> Thing<K> shed( TraitableBean<K,X> core, Class<T> trait, Activation activation ) {
        if ( trait.isAssignableFrom( core.getClass() ) ) {
            Collection<Thing<K>> removedTypes = core.removeTrait( trait.getName() );
            if ( ! removedTypes.isEmpty() ) {
                reassignNodes( core, removedTypes );
                update( getFactHandle( core ), onlyTraitBitSetMask(), core.getClass(), activation );
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

                removedTypes = new ArrayList<Thing<K>>( core._getTraitMap().values() );
                for ( Thing t : removedTypes ) {
                    if ( ! ((TraitType) t)._isVirtual() ) {
                        delete( getFactHandle( t ), activation );
                    }
                }

                core._getTraitMap().clear();
                core._setTraitMap( null );
                return thing;
            } else if ( core.hasTrait( trait.getName() ) ) {
                removedTypes = core.removeTrait( trait.getName() );
            } else {
                HierarchyEncoder hier = this.workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getTraitRegistry().getHierarchy();
                BitSet code = hier.getCode( trait.getName() );
                removedTypes = core.removeTrait( code );
            }

            removedTypes = new ArrayList<Thing<K>>( removedTypes );
            reassignNodes( core, removedTypes );
            for ( Thing t : removedTypes ) {
                if ( ! ((TraitType) t)._isVirtual() ) {
                    InternalFactHandle handle = (InternalFactHandle) getFactHandle( t );
                    if ( handle.getEqualityKey() != null && handle.getEqualityKey().getLogicalFactHandle() == handle ) {
                        entryPoint.getTruthMaintenanceSystem().delete( handle );
                    } else {
                        delete( getFactHandle( t ), activation );
                    }
                }
            }

            if ( ! core.hasTraits() ) {
                don( activation, core, Thing.class, false );
            } else if ( ! removedTypes.isEmpty() ) {
                update( getFactHandle( core ), onlyTraitBitSetMask(), core.getClass(), activation );
                //updateTraits( core, Long.MIN_VALUE, null, core.getClass(), null, ((TraitableBean) core).getMostSpecificTraits()  );
            }
            return thing;
        }
    }

    private <K, X extends TraitableBean> void reassignNodes( TraitableBean<K, X> core, Collection<Thing<K>> removedTraits ) {
        if ( ! core.hasTraits() ) {
            return;
        }
        Collection<Thing<K>> mst = ( (TraitTypeMap) core._getTraitMap() ).getMostSpecificTraits();
        for ( Thing<K> shedded : removedTraits ) {
            for ( BitSet bs : ( (TraitProxy) shedded ).listAssignedOtnTypeCodes() ) {
                boolean found = false;
                for ( Thing<K> tp : mst ) {
                    TraitProxy candidate = (TraitProxy) tp;
                    if ( HierarchyEncoderImpl.supersetOrEqualset( candidate._getTypeCode(), bs ) ) {
                        candidate.assignOtn( bs );
                        found = true;
                        break;
                    }
                }
                if ( found ) {
                    continue;
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

        Collection<Thing> ts = new ArrayList<Thing>();
        for ( Thing t : inner._getTraitMap().values() )     {
            if ( t instanceof TraitProxy ) {
                if ( ( (TraitProxy) t ).hasOtns() ) {
                    ts.add( t );
                }
            }
        }
        return ts;
    }

    private <T, K> T asTrait( K core, TraitableBean inner, Class<T> trait, boolean needsProxy, boolean hasTrait, boolean needsUpdate, TraitFactory builder, boolean logical, Activation activation ) throws LogicalTypeInconsistencyException {
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
                h = (InternalFactHandle) this.workingMemory.insert( core,
                                                                    null,
                                                                    false,
                                                                    logical,
                                                                    activation.getRule(),
                                                                    activation );
            }
            if ( ! h.isTraitOrTraitable() ) {
                throw new IllegalStateException( "A traited working memory element is being used with a default fact handle. " +
                                                 "Please verify that its class was declared as @Traitable : " + core.getClass().getName() );
            }
            this.update( h, inner, activation );
        }

        return thing;
    }

    private <K> TraitableBean makeTraitable( K core, TraitFactory builder, boolean logical, Activation activation ) {
        boolean needsWrapping = ! ( core instanceof TraitableBean );

        ClassDefinition coreDef = lookupClassDefinition( core );
        TraitableBean<K,? extends TraitableBean> inner = needsWrapping ? builder.asTraitable( core, coreDef ) : (TraitableBean<K,? extends TraitableBean>) core;
        if ( needsWrapping ) {
            InternalFactHandle h = (InternalFactHandle) lookupFactHandle( core );
            InternalWorkingMemoryEntryPoint ep = h != null ? (InternalWorkingMemoryEntryPoint) h.getEntryPoint() : (InternalWorkingMemoryEntryPoint) ((StatefulKnowledgeSessionImpl)workingMemory).getEntryPoint("DEFAULT");
            ObjectTypeConfigurationRegistry reg = ep.getObjectTypeConfigurationRegistry();

            ObjectTypeConf coreConf = reg.getObjectTypeConf( ep.getEntryPoint(), core );

            ObjectTypeConf innerConf = reg.getObjectTypeConf( ep.getEntryPoint(), inner );
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
                    handle = this.workingMemory.insert( inner,
                                                        null,
                                                        false,
                                                        logical,
                                                        activation.getRule(),
                                                        activation );
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
            ObjectStore store = ((InternalWorkingMemoryEntryPoint) ep).getObjectStore();
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
        FactHandle handle = null;

        if ( handle != null ) {
            return handle;
        }

        handle = getFactHandleFromWM( object );
        return handle;
    }


    protected <T> void configureTrait( T thing, Object value ) {
        if ( value instanceof Modify && thing instanceof Metadatable ) {
            Modify modify = (Modify) value;
            modify.call( (Metadatable) thing );
        }
    }

    private FactHandle getFactHandleFromWM(final Object object) {
        FactHandle handle = null;
        // entry point null means it is a generated fact, not a regular inserted fact
        // NOTE: it would probably be a good idea to create a specific attribute for that
            handle = (FactHandle) entryPoint.getFactHandle( object );
        if ( handle != null ) {
        }
        return handle;
    }

    public FactHandle getFactHandle(Object object) {
        FactHandle handle = null;

        if ( handle != null ) {
            return handle;
        }

        handle = getFactHandleFromWM( object );

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
                       final Activation activation ){
        InternalFactHandle h = (InternalFactHandle) handle;
        ((InternalWorkingMemoryEntryPoint) h.getEntryPoint()).update( h,
                                                                      newObject,
                                                                      onlyTraitBitSetMask(),
                                                                      newObject.getClass(),
                                                                      activation );
    }

    public void update( final FactHandle handle,
                        BitMask mask,
                        Class<?> modifiedClass,
                        Activation activation ) {
        InternalFactHandle h = (InternalFactHandle) handle;
        ((NamedEntryPoint) h.getEntryPoint()).update( h,
                                                      ((InternalFactHandle)handle).getObject(),
                                                      mask,
                                                      modifiedClass,
                                                      activation );
        if ( h.isTraitOrTraitable() ) {
            workingMemory.updateTraits( h, mask, modifiedClass, activation );
        }
    }

    public void delete( final FactHandle handle, Activation activation ) {
        Object o = ((InternalFactHandle) handle).getObject();
        ((InternalWorkingMemoryEntryPoint) ((InternalFactHandle) handle).getEntryPoint()).delete( handle,
                                                                                                  activation.getRule(),
                                                                                                  activation );
    }

    public FactHandle insert(final Object object,
                             final Activation activation) {
        FactHandle handle = this.workingMemory.insert( object,
                                                       null,
                                                       false,
                                                       false,
                                                       activation.getRule(),
                                                       activation );
        return handle;
    }

    public void insertLogical(final Activation activation,
                              final Object object,
                              final Mode... modes ) {

        if ( !activation.isMatched() ) {
            // Activation is already unmatched, can't do logical insertions against it
            return;
        }
        // iterate to find previous equal logical insertion
        FactHandle handle = workingMemory.getTruthMaintenanceSystem().insert( object,
                                                                              modes,
                                                                              activation.getRule(),
                                                                              activation );

    }

    public void deleteWMAssertedTraitProxies( InternalFactHandle handle, RuleImpl rule, Activation activation ) {
        TraitableBean traitableBean = (TraitableBean) handle.getObject();
        if( traitableBean.hasTraits() ){
            PriorityQueue<TraitProxy> removedTypes =
                    new PriorityQueue<TraitProxy>( traitableBean._getTraitMap().values().size() );
            removedTypes.addAll( traitableBean._getTraitMap().values() );

            while ( ! removedTypes.isEmpty() ) {
                TraitProxy proxy = removedTypes.poll();
                if ( ! proxy._isVirtual() ) {
                    InternalFactHandle proxyHandle = (InternalFactHandle) getFactHandle( proxy );
                    if ( proxyHandle.getEqualityKey() == null || proxyHandle.getEqualityKey().getLogicalFactHandle() != proxyHandle ) {
                        entryPoint.delete( proxyHandle,
                                           rule,
                                           activation );
                    }
                }
            }
        }
    }

    public static <K> K extractTrait( InternalFactHandle handle, Class<K> klass ) {
        TraitableBean tb;
        if ( handle.isTraitable() ) {
            tb = (TraitableBean) handle.getObject();
        } else if ( handle.isTraiting() ) {
            tb = ((TraitProxy) handle.getObject()).getObject();
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

    public void replaceCore( InternalFactHandle handle, Object object, Object originalObject, BitMask modificationMask, Class<? extends Object> aClass, Activation activation ) {
        TraitableBean src = (TraitableBean) originalObject;
        TraitableBean tgt = (TraitableBean) object;
        tgt._setTraitMap( src._getTraitMap() );
        tgt._setDynamicProperties( src._getDynamicProperties() );
        tgt._setFieldTMS( src._getFieldTMS() );

        updateTraits( handle, modificationMask, object.getClass(), activation );
    }
}
