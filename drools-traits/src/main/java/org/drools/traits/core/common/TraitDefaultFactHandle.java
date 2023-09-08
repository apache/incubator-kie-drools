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
package org.drools.traits.core.common;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.DefaultFactHandle;
import org.drools.base.factmodel.traits.CoreWrapper;
import org.drools.base.factmodel.traits.TraitTypeEnum;
import org.drools.base.factmodel.traits.Traitable;
import org.drools.base.factmodel.traits.TraitableBean;
import org.drools.base.rule.TypeDeclaration;
import org.drools.traits.core.base.TraitHelperImpl;
import org.drools.traits.core.factmodel.TraitProxyImpl;

public class TraitDefaultFactHandle extends DefaultFactHandle {

    protected TraitTypeEnum traitType = TraitTypeEnum.NON_TRAIT;

    public TraitDefaultFactHandle(final long id,
                                  final Object object,
                                  final long recency,
                                  final WorkingMemoryEntryPoint wmEntryPoint) {
        this(id, determineIdentityHashCode(object), object, recency, wmEntryPoint);
    }

    public TraitDefaultFactHandle(final long id,
                                  final int identityHashCode,
                                  final Object object,
                                  final long recency,
                                  final WorkingMemoryEntryPoint wmEntryPoint) {
        this.id = id;
        this.entryPointId = wmEntryPoint == null ? null : wmEntryPoint.getEntryPoint();
        this.wmEntryPoint = wmEntryPoint;
        this.recency = recency;
        this.traitType = determineTraitType(object);
        setObject(object);
        this.identityHashCode = identityHashCode;
        if (wmEntryPoint != null) {
            setLinkedTuples(wmEntryPoint.getKnowledgeBase());
            this.wmEntryPoint = wmEntryPoint;
        } else {
            this.linkedTuples = new SingleLinkedTuples();
        }
    }

    @Override
    public Object as(Class klass) throws ClassCastException {
        if (klass.isAssignableFrom(object.getClass())) {
            return object;
        } else if (this.isTraitOrTraitable()) {
            TraitHelperImpl traitHelper = new TraitHelperImpl();
            Object k = traitHelper.extractTrait(this, klass);
            if (k != null) {
                return k;
            } else {
                throw new RuntimeException(String.format("Cannot trait to %s", klass));
            }
        }
        throw new ClassCastException("The Handle's Object can't be cast to " + klass);
    }

    private TraitTypeEnum determineTraitType(Object object) {
        if (object == null) {
            return TraitTypeEnum.NON_TRAIT;
        }
        if (object instanceof TraitProxyImpl) {
            return TraitTypeEnum.TRAIT;
        }
        if (object instanceof CoreWrapper) {
            return TraitTypeEnum.WRAPPED_TRAITABLE;
        }
        if (object instanceof TraitableBean) {
            return TraitTypeEnum.TRAITABLE;
        }
        if (object.getClass().getAnnotation(Traitable.class) != null) {
            return TraitTypeEnum.LEGACY_TRAITABLE;
        }
        TypeDeclaration typeDeclaration = wmEntryPoint.getKnowledgeBase().getTypeDeclaration(object.getClass());
        if (typeDeclaration != null && typeDeclaration.getTypeClassDef().getAnnotation(Traitable.class) != null) {
            return TraitTypeEnum.LEGACY_TRAITABLE;
        }
        return TraitTypeEnum.NON_TRAIT;
    }

    @Override
    public TraitTypeEnum getTraitType() {
        return traitType;
    }

    @Override
    protected void setTraitType(TraitTypeEnum traitType) {
        this.traitType = traitType;
    }

    @Override
    public boolean isTraitOrTraitable() {
        return traitType != TraitTypeEnum.NON_TRAIT;
    }

    @Override
    public boolean isTraitable() {
        return traitType == TraitTypeEnum.TRAITABLE || traitType == TraitTypeEnum.WRAPPED_TRAITABLE;
    }

    @Override
    public boolean isTraiting() {
        return traitType == TraitTypeEnum.TRAIT;
    }

    @Override
    public void setObject(final Object object) {
        this.object = object;
        this.objectClassName = null;
        this.objectHashCode = 0;

        if (isTraitOrTraitable()) {
            TraitTypeEnum newType = determineTraitType(object);
            if (!(this.traitType == TraitTypeEnum.LEGACY_TRAITABLE && newType != TraitTypeEnum.LEGACY_TRAITABLE)) {
                this.identityHashCode = determineIdentityHashCode(object);
            } else {
                // we are replacing a non-traitable object with its proxy, so we need to preserve the identity hashcode
            }
            this.traitType = newType;
        } else {
            this.identityHashCode = 0;
        }
    }
}
