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
package org.drools.core.common;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collections;
import java.util.List;

import org.drools.base.base.ClassObjectType;
import org.drools.base.base.ObjectType;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.reteoo.PropertySpecificUtil;
import org.drools.base.rule.EntryPointId;
import org.drools.base.rule.TypeDeclaration;
import org.drools.core.marshalling.MarshallerReaderContext;
import org.drools.core.reteoo.TerminalNode;
import org.drools.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;

import static org.drools.base.reteoo.PropertySpecificUtil.allSetBitMask;
import static org.drools.base.reteoo.PropertySpecificUtil.getEmptyPropertyReactiveMask;
import static org.drools.base.reteoo.PropertySpecificUtil.isAllSetPropertyReactiveMask;
import static org.drools.base.reteoo.PropertySpecificUtil.isPropertySetOnMask;
import static org.drools.base.reteoo.PropertySpecificUtil.setPropertyOnMask;

public class PhreakPropagationContext
        implements
        PropagationContext {

    private static final long               serialVersionUID = 510l;

    private Type                            type;

    private RuleImpl                        rule;

    private TerminalNode                    terminalNodeOrigin;

    private FactHandle                      factHandle;

    private long                            propagationNumber;

    private EntryPointId                    entryPoint;

    private BitMask                         modificationMask = allSetBitMask();

    private BitMask                         originalMask = allSetBitMask();

    private Class<?>                        modifiedClass;

    // this field is only set for propagations happening during
    // the deserialization of a session
    private transient MarshallerReaderContext readerContext;

    public PhreakPropagationContext() {

    }

    public PhreakPropagationContext(final long number,
                                    final Type type,
                                    final RuleImpl rule,
                                    final TerminalNode terminalNode,
                                    final InternalFactHandle factHandle) {
        this( number,
              type,
              rule,
              terminalNode,
              factHandle,
              EntryPointId.DEFAULT,
              allSetBitMask(),
              Object.class,
              null );
    }

    public PhreakPropagationContext(final long number,
                                    final Type type,
                                    final RuleImpl rule,
                                    final TerminalNode terminalNode,
                                    final InternalFactHandle factHandle,
                                    final EntryPointId entryPoint) {
        this( number,
              type,
              rule,
              terminalNode,
              factHandle,
              entryPoint,
              allSetBitMask(),
              Object.class,
              null );
    }

    public PhreakPropagationContext(final long number,
                                    final Type type,
                                    final RuleImpl rule,
                                    final TerminalNode terminalNode,
                                    final InternalFactHandle factHandle,
                                    final EntryPointId entryPoint,
                                    final MarshallerReaderContext readerContext) {
        this( number,
              type,
              rule,
              terminalNode,
              factHandle,
              entryPoint,
              allSetBitMask(),
              Object.class,
              readerContext );
    }

    public PhreakPropagationContext(final long number,
                                    final Type type,
                                    final RuleImpl rule,
                                    final TerminalNode terminalNode,
                                    final InternalFactHandle factHandle,
                                    final EntryPointId entryPoint,
                                    final BitMask modificationMask,
                                    final Class<?> modifiedClass,
                                    final MarshallerReaderContext readerContext) {
        this.type = type;
        this.rule = rule;
        this.terminalNodeOrigin = terminalNode;
        this.factHandle = factHandle;
        this.propagationNumber = number;
        this.entryPoint = entryPoint;
        this.modificationMask = modificationMask;
        this.originalMask = modificationMask;
        this.modifiedClass = modifiedClass;
        this.readerContext = readerContext;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.type = (Type) in.readObject();
        this.propagationNumber = in.readLong();
        this.rule = (RuleImpl) in.readObject();
        this.entryPoint = (EntryPointId) in.readObject();
        this.modificationMask = (BitMask) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( this.type );
        out.writeLong( this.propagationNumber );
        out.writeObject( this.rule );
        out.writeObject( this.entryPoint );
        out.writeObject(this.modificationMask);
    }

    public long getPropagationNumber() {
        return this.propagationNumber;
    }

    public void cleanReaderContext() {
        readerContext = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.reteoo.PropagationContext#getRuleOrigin()
     */
    public RuleImpl getRuleOrigin() {
        return this.rule;
    }

    public TerminalNode getTerminalNodeOrigin() {
        return terminalNodeOrigin;
    }

    public FactHandle getFactHandle() {
        return this.factHandle;
    }
    
    public void setFactHandle(FactHandle factHandle) {
        this.factHandle = factHandle;
    }    

    public Type getType() {
        return this.type;
    }

    /**
     * @return the entryPoint
     */
    public EntryPointId getEntryPoint() {
        return entryPoint;
    }

    /**
     * @param entryPoint the entryPoint to set
     */
    public void setEntryPoint(EntryPointId entryPoint) {
        this.entryPoint = entryPoint;
    }

    public BitMask getModificationMask() {
        return modificationMask;
    }

    public PropagationContext adaptModificationMaskForObjectType(ObjectType type, ReteEvaluator reteEvaluator) {
        if (isAllSetPropertyReactiveMask(originalMask) || originalMask.isSet(PropertySpecificUtil.TRAITABLE_BIT) || !(type instanceof ClassObjectType)) {
            return this;
        }
        ClassObjectType classObjectType = (ClassObjectType)type;
        BitMask cachedMask = classObjectType.getTransformedMask(modifiedClass, originalMask);

        if (cachedMask != null) {
            modificationMask = cachedMask;
            return this;
        }

        modificationMask = originalMask;
        boolean typeBit = modificationMask.isSet(PropertySpecificUtil.TRAITABLE_BIT);
        modificationMask = modificationMask.reset(PropertySpecificUtil.TRAITABLE_BIT);

        Class<?> classType = classObjectType.getClassType();
        String pkgName = classType.getPackage().getName();

        if (classType == modifiedClass || "java.lang".equals(pkgName) || !(classType.isInterface() || modifiedClass.isInterface())) {
            if (typeBit) {
                modificationMask = modificationMask.set(PropertySpecificUtil.TRAITABLE_BIT);
            }
            return this;
        }

        List<String> typeClassProps = getAccessibleProperties( reteEvaluator, classType, pkgName );
        List<String> modifiedClassProps = getAccessibleProperties( reteEvaluator, modifiedClass );
        modificationMask = getEmptyPropertyReactiveMask(typeClassProps.size());

        for (int i = 0; i < modifiedClassProps.size(); i++) {
            if (isPropertySetOnMask(originalMask, i)) {
                int posInType = typeClassProps.indexOf(modifiedClassProps.get(i));
                if (posInType >= 0) {
                    modificationMask = setPropertyOnMask(modificationMask, posInType);
                }
            }
        }

        if (typeBit) {
            modificationMask = modificationMask.set(PropertySpecificUtil.TRAITABLE_BIT);
        }

        classObjectType.storeTransformedMask(modifiedClass, originalMask, modificationMask);

        return this;
    }

    private List<String> getAccessibleProperties( ReteEvaluator reteEvaluator, Class<?> classType ) {
        return getAccessibleProperties( reteEvaluator, classType, classType.getPackage().getName() );
    }

    private List<String> getAccessibleProperties( ReteEvaluator reteEvaluator, Class<?> classType, String pkgName ) {
        if ( pkgName.equals( "java.lang" ) || pkgName.equals( "java.util" ) ) {
            return Collections.EMPTY_LIST;
        }
        InternalKnowledgePackage pkg = reteEvaluator.getKnowledgeBase().getPackage( pkgName );
        TypeDeclaration tdecl =  pkg != null ? pkg.getTypeDeclaration( classType ) : null;
        return tdecl != null ? tdecl.getAccessibleProperties() : Collections.EMPTY_LIST;
    }

    public MarshallerReaderContext getReaderContext() {
        return this.readerContext;
    }

    public static String intEnumToString( PropagationContext pctx ) {
        switch( pctx.getType() ) {
            case INSERTION:
                return "INSERTION";
            case RULE_ADDITION:
                return "RULE_ADDITION";
            case MODIFICATION:
                return "MODIFICATION";
            case RULE_REMOVAL:
                return "RULE_REMOVAL";
            case DELETION:
                return "DELETION";
            case EXPIRATION:
                return "EXPIRATION";
        }
        throw new IllegalStateException( "Int type unknown");
    }

    @Override
    public String toString() {
        return "PhreakPropagationContext [entryPoint=" + entryPoint + ", factHandle=" + factHandle +
                ", propagationNumber=" + propagationNumber + ", rule=" + rule + ", type=" + type + "]";
    }
}
