/*
 * Copyright 2007 JBoss Inc
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
 *
 * Created on Nov 26, 2007
 */
package org.drools.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.FactException;
import org.drools.RuntimeDroolsException;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.ShadowProxy;
import org.drools.base.ShadowProxyFactory;
import org.drools.common.InternalRuleBase;
import org.drools.objenesis.instantiator.ObjectInstantiator;
import org.drools.reteoo.builder.BuildContext;
import org.drools.reteoo.builder.PatternBuilder;
import org.drools.rule.EntryPoint;
import org.drools.rule.TypeDeclaration;
import org.drools.spi.ObjectType;

public class ClassObjectTypeConf
    implements
    ObjectTypeConf,
    Externalizable {

    private static final long serialVersionUID = 8218802585428841926L;
    
    private Class<?>                       cls;
    private transient InternalRuleBase     ruleBase;
    private ObjectTypeNode[]               objectTypeNodes;

    protected boolean                      shadowEnabled;
    protected Class<ShadowProxy>           shadowClass;
    protected transient ObjectInstantiator instantiator;

    private ObjectTypeNode                 concreteObjectTypeNode;
    private EntryPoint                     entryPoint;

    public ClassObjectTypeConf() {

    }

    public ClassObjectTypeConf(final EntryPoint entryPoint,
                               final Class<?> clazz,
                               final InternalRuleBase ruleBase) {
        this.cls = clazz;
        this.ruleBase = ruleBase;
        this.entryPoint = entryPoint;
        TypeDeclaration type = ruleBase.getTypeDeclaration( clazz );
        final boolean isEvent = type != null && type.getRole() == TypeDeclaration.Role.EVENT; 

        ObjectType objectType = new ClassObjectType( clazz,
                                                     isEvent );
        this.concreteObjectTypeNode = (ObjectTypeNode) ruleBase.getRete().getObjectTypeNodes( entryPoint ).get( objectType );
        if ( this.concreteObjectTypeNode == null ) {
            BuildContext context = new BuildContext( ruleBase,
                                                     ((ReteooRuleBase) ruleBase.getRete().getRuleBase()).getReteooBuilder().getIdGenerator() );
            context.setCurrentEntryPoint( entryPoint );
            if ( DroolsQuery.class == clazz ) {
                context.setTupleMemoryEnabled( false );
                context.setObjectTypeNodeMemoryEnabled( false );
                context.setTerminalNodeMemoryEnabled( false );
            } else if ( context.getRuleBase().getConfiguration().isSequential() ) {
                // We are in sequential mode, so no nodes should have memory
                context.setTupleMemoryEnabled( false );
                context.setObjectTypeNodeMemoryEnabled( false );
                context.setTerminalNodeMemoryEnabled( false );
            } else {
                context.setTupleMemoryEnabled( true );
                context.setObjectTypeNodeMemoryEnabled( true );
                context.setTerminalNodeMemoryEnabled( true );
            }
            // there must exist an ObjectTypeNode for this concrete class
            this.concreteObjectTypeNode = PatternBuilder.attachObjectTypeNode( context,
                                                                               objectType );
        }

        defineShadowProxyData( clazz );
    }

    public void readExternal(ObjectInput stream) throws IOException,
                                                     ClassNotFoundException {
        ruleBase = (InternalRuleBase)stream.readObject();
        cls = (Class)stream.readObject();
        objectTypeNodes = (ObjectTypeNode[])stream.readObject();
        shadowEnabled = stream.readBoolean();
        shadowClass = (Class)stream.readObject();
        concreteObjectTypeNode = (ObjectTypeNode)stream.readObject();
        entryPoint = (EntryPoint)stream.readObject();
        defineShadowProxyData(cls);
    }

    public void writeExternal(ObjectOutput stream) throws IOException {
        stream.writeObject(ruleBase);
        stream.writeObject(cls);
        stream.writeObject(objectTypeNodes);
        stream.writeBoolean(shadowEnabled);
        stream.writeObject(shadowClass);
        stream.writeObject(concreteObjectTypeNode);
        stream.writeObject(entryPoint);
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
    public static String getPackageName(Class<?> clazz,
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
                isOk = nodes[i].getSinkPropagator().size() == 0 || nodes[i].isAssignableFrom( ret );
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
                    isOk = nodes[j].getSinkPropagator().size() == 0 || nodes[j].isAssignableFrom( ret );
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

    /**
     *
     */
    private void setInstantiator() {
        this.instantiator = this.ruleBase.getObjenesis().getInstantiatorOf( this.shadowClass );
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
            	System.out.println( "shadow: " +proxy.getClass() + ":" + fact.getClass() );
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
        final List<ObjectTypeNode> cache = new ArrayList<ObjectTypeNode>();

        for( ObjectTypeNode node : ruleBase.getRete().getObjectTypeNodes( this.entryPoint ).values() ) {
            if ( node.isAssignableFrom( clazz ) ) {
                cache.add( node );
            }
        }

        return (ObjectTypeNode[]) cache.toArray( new ObjectTypeNode[cache.size()] );
    }

    public boolean isActive() {
        return getConcreteObjectTypeNode().getSinkPropagator().getSinks().length > 0;
    }

    public boolean isEvent() {
        return this.concreteObjectTypeNode.getObjectType().isEvent();
    }
}