package org.drools.reteoo;

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
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.FactException;
import org.drools.RuleBaseConfiguration;
import org.drools.RuntimeDroolsException;
import org.drools.base.ClassObjectType;
import org.drools.base.ShadowProxy;
import org.drools.base.ShadowProxyFactory;
import org.drools.common.BaseNode;
import org.drools.common.DroolsObjectInputStream;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.facttemplates.Fact;
import org.drools.facttemplates.FactImpl;
import org.drools.facttemplates.FactTemplate;
import org.drools.facttemplates.FactTemplateObjectType;
import org.drools.objenesis.Objenesis;
import org.drools.objenesis.ObjenesisStd;
import org.drools.objenesis.instantiator.ObjectInstantiator;
import org.drools.reteoo.builder.PatternBuilder;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;
import org.drools.util.FactEntry;
import org.drools.util.FactHashTable;
import org.drools.util.Iterator;
import org.drools.util.ObjectHashMap;
import org.drools.util.ObjectHashMap.ObjectEntry;

/**
 * The Rete-OO network.
 *
 * The Rete class is the root <code>Object</code>. All objects are asserted into
 * the Rete node where it propagates to all matching ObjectTypeNodes.
 *
 * The first time an  instance of a Class type is asserted it does a full
 * iteration of all ObjectTyppeNodes looking for matches, any matches are
 * then cached in a HashMap which is used for future assertions.
 *
 * While Rete  extends ObjectSource nad implements ObjectSink it nulls the
 * methods attach(), remove() and  updateNewNode() as this is the root node
 * they are no applicable
 *
 * @see ObjectTypeNode
 *
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
public class Rete extends ObjectSource
    implements
    Serializable,
    ObjectSink,
    NodeMemory {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
     *
     */
    private static final long          serialVersionUID = 400L;
    /** The <code>Map</code> of <code>ObjectTypeNodes</code>. */
    private final ObjectHashMap        objectTypeNodes;

    private transient InternalRuleBase ruleBase;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    public Rete(InternalRuleBase ruleBase) {
        super( 0 );
        this.objectTypeNodes = new ObjectHashMap();
        this.ruleBase = ruleBase;
    }

    private void readObject(ObjectInputStream stream) throws IOException,
                                                     ClassNotFoundException {
        stream.defaultReadObject();
        this.ruleBase = ((DroolsObjectInputStream) stream).getRuleBase();
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * This is the entry point into the network for all asserted Facts. Iterates a cache
     * of matching <code>ObjectTypdeNode</code>s asserting the Fact. If the cache does not
     * exist it first iteraes and builds the cache.
     *
     * @param handle
     *            The FactHandle of the fact to assert
     * @param context
     *            The <code>PropagationContext</code> of the <code>WorkingMemory</code> action
     * @param workingMemory
     *            The working memory session.
     */
    public void assertObject(final InternalFactHandle handle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        final ObjectHashMap memory = (ObjectHashMap) workingMemory.getNodeMemory( this );

        Object object = handle.getObject();

        ObjectTypeConf ojectTypeConf;
        if ( object instanceof FactImpl ) {
            String key = ((Fact) object).getFactTemplate().getName();
            ojectTypeConf = (ObjectTypeConf) memory.get( key );
            if ( ojectTypeConf == null ) {
                ojectTypeConf = new FactTemplateTypeConf( ((Fact) object).getFactTemplate(),
                                                                 this.ruleBase );
                memory.put( key,
                            ojectTypeConf,
                            false );
            }
            object = key;
        } else {
            Class cls = null;
            if ( object instanceof ShadowProxy ) {
                cls = ((ShadowProxy) object).getShadowedObject().getClass();
            } else {
                cls = object.getClass();
            }

            ojectTypeConf = (ObjectTypeConf) memory.get( cls );
            if ( ojectTypeConf == null ) {
                ojectTypeConf = new ClassObjectTypeConf( cls,
                                                         this.ruleBase );
                memory.put( cls,
                            ojectTypeConf,
                            false );
            }

            // checks if shadow is enabled
            if ( ojectTypeConf.isShadowEnabled() ) {
                // need to improve this
                if ( !(handle.getObject() instanceof ShadowProxy) ) {
                    // replaces the actual object by its shadow before propagating
                    handle.setObject( ojectTypeConf.getShadow( handle.getObject() ) );
                    handle.setShadowFact( true );
                } else {
                    ((ShadowProxy) handle.getObject()).updateProxy();
                }
            }
        }

        ObjectTypeNode[] cachedNodes = ojectTypeConf.getObjectTypeNodes();

        for ( int i = 0, length = cachedNodes.length; i < length; i++ ) {
            cachedNodes[i].assertObject( handle,
                                         context,
                                         workingMemory );
        }
    }

    /**
     * Retract a fact object from this <code>RuleBase</code> and the specified
     * <code>WorkingMemory</code>.
     *
     * @param handle
     *            The handle of the fact to retract.
     * @param workingMemory
     *            The working memory session.
     */
    public void retractObject(final InternalFactHandle handle,
                              final PropagationContext context,
                              final InternalWorkingMemory workingMemory) {
        final ObjectHashMap memory = (ObjectHashMap) workingMemory.getNodeMemory( this );

        final Object object = handle.getObject();

        ObjectTypeConf objectTypeConf;
        if ( object instanceof ShadowProxy ) {
            objectTypeConf = (ObjectTypeConf) memory.get( ((ShadowProxy) object).getShadowedObject().getClass() );
        } else {
            objectTypeConf = (ObjectTypeConf) memory.get( object.getClass() );
        }

        ObjectTypeNode[] cachedNodes = objectTypeConf.getObjectTypeNodes( );

        if ( cachedNodes == null ) {
            // it is  possible that there are no ObjectTypeNodes for an  object being retracted
            return;
        }

        for ( int i = 0; i < cachedNodes.length; i++ ) {
            cachedNodes[i].retractObject( handle,
                                          context,
                                          workingMemory );
        }
    }

    /**
     * Adds the <code>TupleSink</code> so that it may receive
     * <code>Tuples</code> propagated from this <code>TupleSource</code>.
     *
     * @param tupleSink
     *            The <code>TupleSink</code> to receive propagated
     *            <code>Tuples</code>.
     */
    protected void addObjectSink(final ObjectSink objectSink) {
        final ObjectTypeNode node = (ObjectTypeNode) objectSink;
        this.objectTypeNodes.put( node.getObjectType(),
                                  node,
                                  true );
    }

    protected void removeObjectSink(final ObjectSink objectSink) {
        final ObjectTypeNode node = (ObjectTypeNode) objectSink;        
        this.objectTypeNodes.remove( node.getObjectType() );
    }

    public void attach() {
        throw new UnsupportedOperationException( "cannot call attach() from the root Rete node" );
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        throw new UnsupportedOperationException( "cannot call attach() from the root Rete node" );
    }

    public void remove(final BaseNode node,
                       final InternalWorkingMemory[] workingMemories) {
        final ObjectTypeNode objectTypeNode = (ObjectTypeNode) node;
        removeObjectSink( objectTypeNode );
        for ( int i = 0; i < workingMemories.length; i++ ) {
            // clear the node memory for each working memory.
            workingMemories[i].clearNodeMemory( (NodeMemory) node );
        }
    }

    public ObjectHashMap getObjectTypeNodes() {
        return this.objectTypeNodes;
    }

    public Object createMemory(final RuleBaseConfiguration config) {
        return new ObjectHashMap();
    }

    public InternalRuleBase getRuleBase() {
        return this.ruleBase;
    }

    public int hashCode() {
        return this.objectTypeNodes.hashCode();
    }

    public boolean equals(final Object object) {
        if ( object == this ) {
            return true;
        }

        if ( object == null || !(object instanceof Rete) ) {
            return false;
        }

        final Rete other = (Rete) object;
        return this.objectTypeNodes.equals( other.objectTypeNodes );
    }

    public void updateSink(final ObjectSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        // JBRULES-612: the cache MUST be invalidated when a new node type is added to the network, so iterate and reset all caches.
        final ObjectHashMap memory = (ObjectHashMap) workingMemory.getNodeMemory( this );
        Iterator it = memory.iterator();
        final ObjectTypeNode node = (ObjectTypeNode) sink;
        
        ObjectType newObjectType = node.getObjectType();

        for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
            ObjectTypeConf objectTypeConf = (ObjectTypeConf) entry.getValue();
            if ( newObjectType.isAssignableFrom( objectTypeConf.getConcreteObjectTypeNode().getObjectType() ) ) {                
                objectTypeConf.resetCache();
                ObjectTypeNode sourceNode = objectTypeConf.getConcreteObjectTypeNode();
                FactHashTable table = (FactHashTable) workingMemory.getNodeMemory( sourceNode );
                Iterator factIter = table.iterator();
                for ( FactEntry factEntry = (FactEntry) factIter.next(); factEntry != null; factEntry = (FactEntry) factIter.next() ) {
                    sink.assertObject( factEntry.getFactHandle(),
                                       context,
                                       workingMemory );
                }
            }
        }

        //        ObjectType
        //        this.c

        //        final ObjectTypeNode node = (ObjectTypeNode) sink;
        //        it = workingMemory.getFactHandleMap().iterator();
        //        for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
        //            final InternalFactHandle handle = (InternalFactHandle) entry.getValue();
        //            if ( node.matches( handle.getObject() ) ) {
        //                node.assertObject( handle,
        //                                   context,
        //                                   workingMemory );
        //            }
        //        }
    }

    public static interface ObjectTypeConf {
        public ObjectTypeNode[] getObjectTypeNodes();

        public boolean isShadowEnabled();

        public Object getShadow(final Object fact) throws RuntimeDroolsException;

        public ObjectTypeNode getConcreteObjectTypeNode();

        public void resetCache();

        public boolean isAssignableFrom(Object object);
    }

    public static class FactTemplateTypeConf
        implements
        ObjectTypeConf,
        Serializable {
        private InternalRuleBase               ruleBase;
        private FactTemplate                   factTemplate;
        private ObjectTypeNode                 concreteObjectTypeNode;
        private ObjectTypeNode[]               cache;
        
        public FactTemplateTypeConf(FactTemplate  factTemplate,
                                    InternalRuleBase ruleBase) {
            this.ruleBase = ruleBase;
            this.factTemplate = factTemplate;
            ObjectType objectType = new FactTemplateObjectType(factTemplate);
            this.concreteObjectTypeNode = (ObjectTypeNode) ruleBase.getRete().getObjectTypeNodes().get( objectType );
            if ( this.concreteObjectTypeNode == null ) {
                // there must exist an ObjectTypeNode for this concrete class                
                this.concreteObjectTypeNode = PatternBuilder.attachObjectTypeNode( ruleBase.getRete(),
                                                                                   objectType );
            }           
            this.cache = new ObjectTypeNode[] { this.concreteObjectTypeNode };
        }

        public ObjectTypeNode getConcreteObjectTypeNode() {
            return this.concreteObjectTypeNode;
        }

        public ObjectTypeNode[] getObjectTypeNodes() {
            if ( this.cache == null ) {
                this.cache = new ObjectTypeNode[] { this.concreteObjectTypeNode };
            }
            return this.cache;
        }

        public Object getShadow(Object fact) throws RuntimeDroolsException {
            return null;
        }

        public boolean isShadowEnabled() {
            return false;
        }        

        public boolean isAssignableFrom(Object object) {
            return this.factTemplate.equals( object );
        }

        public void resetCache() {
            this.cache = null;
        }

    }

    public static class ClassObjectTypeConf
        implements
        ObjectTypeConf,
        Serializable {
        // Objenesis instance without cache (false)
        private static final Objenesis         OBJENESIS = new ObjenesisStd( false );

        private final Class                    cls;
        private transient InternalRuleBase     ruleBase;
        private ObjectTypeNode[]               objectTypeNodes;

        protected boolean                      shadowEnabled;
        protected Class                        shadowClass;
        protected transient ObjectInstantiator instantiator;

        private ObjectTypeNode                 concreteObjectTypeNode;

        public ClassObjectTypeConf(Class clazz,
                                   InternalRuleBase ruleBase) {
            this.cls = clazz;
            this.ruleBase = ruleBase;

            ObjectType objectType =  new ClassObjectType( clazz );
            this.concreteObjectTypeNode = (ObjectTypeNode) ruleBase.getRete().getObjectTypeNodes().get( objectType );
            if ( this.concreteObjectTypeNode == null ) {
                // there must exist an ObjectTypeNode for this concrete class
                this.concreteObjectTypeNode = PatternBuilder.attachObjectTypeNode( ruleBase.getRete(),
                                                                                   objectType );
            }

            defineShadowProxyData( clazz );
        }

        public boolean isAssignableFrom(Object object) {
            return this.cls.isAssignableFrom( (Class) object );
        }       

        public ObjectTypeNode getConcreteObjectTypeNode() {
            return this.concreteObjectTypeNode;
        }

        private void defineShadowProxyData(Class clazz) {
            Rete rete = this.ruleBase.getRete();

            if ( !ruleBase.getConfiguration().isShadowProxy() || clazz == null || !ruleBase.getConfiguration().isShadowed( clazz.getName() ) ) {
                this.shadowEnabled = false;
                this.shadowClass = null;
                this.instantiator = null;
                return;
            }

            //String pkgName = (pkg != null) ? pkg.getName() : "";
            String pkgName = getPackageName( clazz,
                                             clazz.getPackage() );
            if ( "org.drools.reteoo".equals( pkgName ) || "org.drools.base".equals( pkgName ) ) {
                // We don't shadow internal classes
                this.shadowEnabled = false;
                this.shadowClass = null;
                this.instantiator = null;
                return;
            }

            // try to generate proxy for the actual class
            Class shadowClass = loadOrGenerateProxy( clazz,
                                                     rete );

            if ( shadowClass == null ) {
                // if it failed, try to find a parent class
                ObjectTypeNode[] nodes = this.getMatchingObjectTypes( clazz );
                Class shadowClassRoot = clazz;
                while ( shadowClass == null && (shadowClassRoot = this.findAFeasibleSuperclassOrInterface( nodes,
                                                                                                           shadowClassRoot )) != null ) {
                    shadowClass = loadOrGenerateProxy( shadowClassRoot,
                                                       rete );
                }
            }

            if ( shadowClass != null ) {
                this.shadowClass = shadowClass;
                this.shadowEnabled = true;
                setInstantiator();
            }
        }

        /**
         * This will return the package name - if the package is null, it will
         * work it out from the class name (this is in cases where funky classloading is used).
         */
        public static String getPackageName(Class clazz,
                                             Package pkg) {
            String pkgName = "";
            if ( pkg == null ) {
                int index = clazz.getName().lastIndexOf( '.' );
                if ( index != -1 ) pkgName = clazz.getName().substring( 0,
                                                                        index );
            } else {
                pkgName = pkg.getName();
            }
            return pkgName;

        }

        private Class loadOrGenerateProxy(Class clazz,
                                          Rete rete) {
            Class shadowClass = null;
            final String shadowProxyName = ShadowProxyFactory.getProxyClassNameForClass( clazz );
            try {
                // if already loaded
                shadowClass = rete.getRuleBase().getMapBackedClassLoader().loadClass( shadowProxyName );
            } catch ( final ClassNotFoundException cnfe ) {
                // otherwise, create and load
                final byte[] proxyBytes = ShadowProxyFactory.getProxyBytes( clazz );
                if ( proxyBytes != null ) {
                    rete.getRuleBase().getMapBackedClassLoader().addClass( shadowProxyName,
                                                                           proxyBytes );
                    try {
                        shadowClass = rete.getRuleBase().getMapBackedClassLoader().loadClass( shadowProxyName );
                    } catch ( ClassNotFoundException e ) {
                        throw new RuntimeException( "Unable to find or generate the ShadowProxy implementation for '" + clazz + "'" );
                    }
                }

            }
            return shadowClass;
        }

        private Class findAFeasibleSuperclassOrInterface(ObjectTypeNode[] nodes,
                                                         Class clazz) {

            // check direct superclass
            Class ret = clazz.getSuperclass();
            boolean isOk = ret != null && ret != Object.class; // we don't want to shadow java.lang.Object
            if ( isOk ) {
                for ( int i = 0; isOk && ret != null && i < nodes.length; i++ ) {
                    isOk = nodes[i].isAssignableFrom( ret );
                }
            }

            if ( !isOk ) {
                // try the interfaces now...
                Class[] interfaces = clazz.getInterfaces();
                boolean notFound = true;
                isOk = interfaces.length > 0;
                for ( int i = 0; notFound && i < interfaces.length; i++ ) {
                    ret = interfaces[i];
                    isOk = interfaces[i] != Serializable.class && interfaces[i] != Cloneable.class && interfaces[i] != Comparable.class;
                    for ( int j = 0; isOk && j < nodes.length; j++ ) {
                        isOk = nodes[j].isAssignableFrom( ret );
                    }
                    notFound = !isOk;
                }
                if ( notFound ) {
                    ret = null;
                }
            }

            // ret now contains a superclass/interface that can be shadowed or null if none
            return ret;
        }

        private void readObject(ObjectInputStream stream) throws IOException,
                                                         ClassNotFoundException {
            stream.defaultReadObject();
            this.ruleBase = ((DroolsObjectInputStream) stream).getRuleBase();
        }

        /**
         *
         */
        private void setInstantiator() {
            this.instantiator = OBJENESIS.getInstantiatorOf( this.shadowClass );
        }

        public Object getShadow(final Object fact) throws RuntimeDroolsException {
            ShadowProxy proxy = null;
            if ( isShadowEnabled() ) {
                try {
                    if ( Collection.class.isAssignableFrom( this.shadowClass ) || Map.class.isAssignableFrom( this.shadowClass ) ) {
                        // if it is a collection, try to instantiate using constructor
                        try {
                            proxy = (ShadowProxy) this.shadowClass.getConstructor( new Class[]{cls} ).newInstance( new Object[]{fact} );
                        } catch ( Exception e ) {
                            // not possible to instantiate using constructor
                        }
                    }
                    if ( proxy == null ) {
                        if ( this.instantiator == null ) {
                            this.setInstantiator();
                        }
                        proxy = (ShadowProxy) this.instantiator.newInstance();
                    }
                    proxy.setShadowedObject( fact );
                } catch ( final Exception e ) {
                    throw new RuntimeDroolsException( "Error creating shadow fact for object: " + fact,
                                                      e );
                }
            }
            return proxy;
        }

        public boolean isShadowEnabled() {
            return this.shadowEnabled;
        }

        public void resetCache() {
            this.objectTypeNodes = null;
            defineShadowProxyData( cls );
        }

        public ObjectTypeNode[] getObjectTypeNodes() {
            if ( this.objectTypeNodes == null ) {
                this.objectTypeNodes = getMatchingObjectTypes( this.cls );
            }
            return this.objectTypeNodes;
        }

        private ObjectTypeNode[] getMatchingObjectTypes(final Class clazz) throws FactException {
            final List cache = new ArrayList();

            final Iterator it = ruleBase.getRete().getObjectTypeNodes().newIterator();
            for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
                final ObjectTypeNode node = (ObjectTypeNode) entry.getValue();
                if ( node.isAssignableFrom( clazz ) ) {
                    cache.add( node );
                }
            }

            return (ObjectTypeNode[]) cache.toArray( new ObjectTypeNode[cache.size()] );
        }
    }

}
