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

import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DroolsQuery;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.Traitable;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.Activation;
import org.drools.core.spi.ObjectType;
import org.kie.api.definition.type.Role;

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

public class ClassObjectTypeConf
    implements
    ObjectTypeConf,
    Externalizable {

    protected static final Class<?>[]  ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES = new Class[]{PropertyChangeListener.class};

    private static final long          serialVersionUID = 510l;

    private Class< ? >                 cls;
    private transient InternalKnowledgeBase kBase;
    private ObjectTypeNode[]           objectTypeNodes;

    private ObjectType                 objectType;
    private ObjectTypeNode             concreteObjectTypeNode;
    private EntryPointId               entryPoint;

    private TypeDeclaration            typeDecl;
    
    private boolean                    tmsEnabled;
    private boolean                    traitTmsEnabled;
    
    private boolean                    supportsPropertyListeners;

    private boolean                    isEvent;

    private long                       expirationOffset = -1;

    private boolean                    isTrait;

    public ClassObjectTypeConf() {

    }

    public ClassObjectTypeConf(final EntryPointId entryPoint,
                               final Class< ? > clazz,
                               final InternalKnowledgeBase kBase) {
        this.cls = (Activation.class.isAssignableFrom( clazz ) ) ? ClassObjectType.Match_ObjectType.getClassType() : clazz;
        this.kBase = kBase;
        this.entryPoint = entryPoint;

        this.typeDecl = kBase.getTypeDeclaration( clazz );
        isEvent = typeDecl != null && typeDecl.getRole() == Role.Type.EVENT;
        if (isEvent && typeDecl != null) {
            expirationOffset = typeDecl.getExpirationOffset();
        }

        isTrait = determineTraitStatus();

        this.objectType = kBase.getClassFieldAccessorCache().getClassObjectType( new ClassObjectType( clazz, isEvent ), false );

        this.concreteObjectTypeNode = kBase.getRete().getObjectTypeNodes( entryPoint ).get( objectType );

        this.supportsPropertyListeners = checkPropertyListenerSupport( clazz );

        Traitable ttbl = cls.getAnnotation( Traitable.class );
        this.traitTmsEnabled = ttbl != null && ttbl.logical();
    }

    public void readExternal(ObjectInput stream) throws IOException,
                                                ClassNotFoundException {
        kBase = (InternalKnowledgeBase) stream.readObject();
        cls = (Class<?>) stream.readObject();
        objectTypeNodes = (ObjectTypeNode[]) stream.readObject();
        objectType = (ObjectType) stream.readObject();
        concreteObjectTypeNode = (ObjectTypeNode) stream.readObject();
        entryPoint = (EntryPointId) stream.readObject();
        tmsEnabled = stream.readBoolean();
        traitTmsEnabled = stream.readBoolean();
        supportsPropertyListeners = stream.readBoolean();
        isEvent = stream.readBoolean();
        isTrait = stream.readBoolean();
        expirationOffset = stream.readLong();
    }

    public void writeExternal(ObjectOutput stream) throws IOException {
        stream.writeObject( kBase );
        stream.writeObject( cls );
        stream.writeObject( objectTypeNodes );
        stream.writeObject( objectType );
        stream.writeObject( concreteObjectTypeNode );
        stream.writeObject( entryPoint );
        stream.writeBoolean( tmsEnabled );
        stream.writeBoolean( traitTmsEnabled );
        stream.writeBoolean( supportsPropertyListeners );
        stream.writeBoolean( isEvent );
        stream.writeBoolean(isTrait);
        stream.writeLong(expirationOffset);
    }

    public boolean isAssignableFrom(Object object) {
        return this.cls.isAssignableFrom( (Class<?>) object );
    }

    public long getExpirationOffset() {
        return expirationOffset;
    }

    public void setExpirationOffset(long expirationOffset) {
        this.expirationOffset = expirationOffset;
    }

    public ObjectTypeNode getConcreteObjectTypeNode() {
        if (concreteObjectTypeNode == null) {
            concreteObjectTypeNode = kBase.getRete().getObjectTypeNodes( entryPoint ).get( objectType );
        }
        return concreteObjectTypeNode;
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

    public boolean isTraitTMSEnabled() {
        return traitTmsEnabled;
    }

    public void resetCache() {
        this.objectTypeNodes = null;
    }

    public ObjectTypeNode[] getObjectTypeNodes() {
        if ( this.objectTypeNodes == null ) {
            this.objectTypeNodes = getMatchingObjectTypes( this.cls );
        }
        return this.objectTypeNodes;
    }

    private ObjectTypeNode[] getMatchingObjectTypes(final Class<?> clazz) {
        final List<ObjectTypeNode> cache = new ArrayList<ObjectTypeNode>();

        for ( ObjectTypeNode node : kBase.getRete().getObjectTypeNodes( this.entryPoint ).values() ) {
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
        ObjectTypeNode otn = getConcreteObjectTypeNode();
        return otn != null && otn.getSinkPropagator().getSinks().length > 0;
    }

    public boolean isEvent() {
        return this.isEvent;
    }

    public boolean isTrait() {
        return isTrait;
    }

    protected boolean determineTraitStatus() {
        return typeDecl != null
               // if cls implements an interface and cls is never actually used, typeDecl will reference the interface
               // rather than the actual class, but this may not reflect the actual traitability
               && typeDecl.getTypeClass() == cls && (
                typeDecl.getKind() == TypeDeclaration.Kind.TRAIT
                || typeDecl.getTypeClassDef().isTraitable()
                || typeDecl.getTypeClass().getAnnotation( Traitable.class ) != null
        ) || Thing.class.isAssignableFrom( cls )
               || cls.getAnnotation( Traitable.class ) != null
               || TraitableBean.class.isAssignableFrom( cls );
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

    public EntryPointId getEntryPoint() {
        return entryPoint;
    }

    
    public boolean isSupportsPropertyChangeListeners() {
        return supportsPropertyListeners;
    }
    
    public String getClassName() { 
    	return this.cls != null ? this.cls.getName() : "";
    }
    
    public String getTypeName() {
    	return getClassName();
    }
    
}
