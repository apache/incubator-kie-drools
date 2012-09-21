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

package org.drools.reteoo;

import java.beans.PropertyChangeListener;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.drools.FactException;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.ShadowProxy;
import org.drools.common.AbstractRuleBase;
import org.drools.common.InternalRuleBase;
import org.drools.reteoo.builder.BuildContext;
import org.drools.reteoo.builder.PatternBuilder;
import org.drools.rule.EntryPoint;
import org.drools.rule.TypeDeclaration;
import org.drools.spi.Activation;
import org.drools.spi.ObjectType;

public class ClassObjectTypeConf
    implements
    ObjectTypeConf,
    Externalizable {

    protected static final Class<?>[]  ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES = new Class[]{PropertyChangeListener.class};

    private static final long          serialVersionUID = 510l;

    private Class< ? >                 cls;
    private transient InternalRuleBase ruleBase;
    private ObjectTypeNode[]           objectTypeNodes;

    protected boolean                  shadowEnabled;

    private ObjectTypeNode             concreteObjectTypeNode;
    private EntryPoint                 entryPoint;

    private TypeDeclaration            typeDecl;
    
    private boolean                    tmsEnabled;
    
    private boolean                    supportsPropertyListeners;

    private boolean                    isEvent;


    public ClassObjectTypeConf() {

    }

    public ClassObjectTypeConf(final EntryPoint entryPoint,
                               final Class< ? > clazz,
                               final InternalRuleBase ruleBase) {        
        this.cls = (Activation.class.isAssignableFrom( clazz ) ) ? ClassObjectType.Activation_ObjectType.getClassType() : clazz;
        this.ruleBase = ruleBase;
        this.entryPoint = entryPoint;
        this.typeDecl = ruleBase.getTypeDeclaration( clazz );
        isEvent = typeDecl != null && typeDecl.getRole() == TypeDeclaration.Role.EVENT;

        ObjectType objectType = ((AbstractRuleBase) ruleBase).getClassFieldAccessorCache().getClassObjectType( new ClassObjectType( clazz,
                                                                                                                                    isEvent ) );

        this.concreteObjectTypeNode = ruleBase.getRete().getObjectTypeNodes( entryPoint ).get( objectType );
        if ( this.concreteObjectTypeNode == null ) {
            BuildContext context = new BuildContext( ruleBase,
                                                     ruleBase.getRete().getRuleBase().getReteooBuilder().getIdGenerator() );
            context.setCurrentEntryPoint( entryPoint );
            if ( DroolsQuery.class == clazz ) {
                context.setTupleMemoryEnabled( false );
                context.setObjectTypeNodeMemoryEnabled( false );
            } else if ( context.getRuleBase().getConfiguration().isSequential() ) {
                // We are in sequential mode, so no nodes should have memory
                context.setTupleMemoryEnabled( false );
                context.setObjectTypeNodeMemoryEnabled( false );
            } else {
                context.setTupleMemoryEnabled( true );
                context.setObjectTypeNodeMemoryEnabled( true );
            }
            // there must exist an ObjectTypeNode for this concrete class
            this.concreteObjectTypeNode = PatternBuilder.attachObjectTypeNode( context,
                                                                               objectType );
        }

        defineShadowProxyData( clazz );
        this.supportsPropertyListeners = checkPropertyListenerSupport( clazz );
    }

    public void readExternal(ObjectInput stream) throws IOException,
                                                ClassNotFoundException {
        ruleBase = (InternalRuleBase) stream.readObject();
        cls = (Class<?>) stream.readObject();
        objectTypeNodes = (ObjectTypeNode[]) stream.readObject();
        shadowEnabled = stream.readBoolean();
        concreteObjectTypeNode = (ObjectTypeNode) stream.readObject();
        entryPoint = (EntryPoint) stream.readObject();
        tmsEnabled = stream.readBoolean();
        supportsPropertyListeners = stream.readBoolean();
        isEvent = stream.readBoolean();
        defineShadowProxyData( cls );
    }

    public void writeExternal(ObjectOutput stream) throws IOException {
        stream.writeObject( ruleBase );
        stream.writeObject( cls );
        stream.writeObject( objectTypeNodes );
        stream.writeBoolean( shadowEnabled );
        stream.writeObject( concreteObjectTypeNode );
        stream.writeObject( entryPoint );
        stream.writeBoolean( tmsEnabled );
        stream.writeBoolean( supportsPropertyListeners );
        stream.writeBoolean( isEvent );
    }

    public boolean isAssignableFrom(Object object) {
        return this.cls.isAssignableFrom( (Class<?>) object );
    }

    public ObjectTypeNode getConcreteObjectTypeNode() {
        return this.concreteObjectTypeNode;
    }

    private void defineShadowProxyData(Class<?> clazz) {
        if ( ShadowProxy.class.isAssignableFrom( cls ) ) {
            this.shadowEnabled = true;
        }
    }
    
    private boolean checkPropertyListenerSupport( Class<?> clazz ) {
        Method method = null;
        try {
            method = clazz.getMethod( "addPropertyChangeListener",
                                      ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES );
        } catch (Exception e) {
            // intentionally left empty
        }
        return method != null;
    }

    /**
     * This will return the package name - if the package is null, it will
     * work it out from the class name (this is in cases where funky classloading is used).
     */
    public static String getPackageName(Class< ? > clazz,
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

    private ObjectTypeNode[] getMatchingObjectTypes(final Class<?> clazz) throws FactException {
        final List<ObjectTypeNode> cache = new ArrayList<ObjectTypeNode>();

        for ( ObjectTypeNode node : ruleBase.getRete().getObjectTypeNodes( this.entryPoint ).values() ) {
            if ( clazz == DroolsQuery.class ) {
                // for query objects only add direct matches
                if ( ((ClassObjectType)node.getObjectType()).getClassType() == clazz ) {
                    cache.add( node );    
                }
            } else if ( node.isAssignableFrom( new ClassObjectType( clazz ) ) ) {
                cache.add( node );
            }
        }

        Collections.sort(cache, OBJECT_TYPE_NODE_COMPARATOR);
        return cache.toArray( new ObjectTypeNode[cache.size()] );
    }

    private static final ObjectTypeNodeComparator OBJECT_TYPE_NODE_COMPARATOR = new ObjectTypeNodeComparator();
    private static final class ObjectTypeNodeComparator implements Comparator<ObjectTypeNode> {
        public int compare(ObjectTypeNode o1, ObjectTypeNode o2) {
            return o1.getId() - o2.getId();
        }
    }

    public boolean isActive() {
        return getConcreteObjectTypeNode().getSinkPropagator().getSinks().length > 0;
    }

    public boolean isEvent() {
//        return this.concreteObjectTypeNode.getObjectType().isEvent();
        return this.isEvent;
    }

    public TypeDeclaration getTypeDeclaration() {
        return typeDecl;
    }
    
    public boolean isDynamic() {
        return typeDecl != null && typeDecl.isDynamic();
    }

    public boolean isTMSEnabled() {
        return this.tmsEnabled;
    }

    public void enableTMS() {
        this.tmsEnabled = true;
    }

    public EntryPoint getEntryPoint() {
        return entryPoint;
    }

    
    public boolean isSupportsPropertyChangeListeners() {
        return supportsPropertyListeners;
    }
    
}
