/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.traits.core.common;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.factmodel.traits.CoreWrapper;
import org.drools.core.factmodel.traits.TraitTypeEnum;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.traits.core.base.TraitHelperImpl;
import org.drools.traits.core.factmodel.traits.TraitProxyImpl;

public class TraitDefaultFactHandle extends DefaultFactHandle {

    public TraitDefaultFactHandle(long id, Object initialFact, long recency, WorkingMemoryEntryPoint wmEntryPoint) {
        this( id, determineIdentityHashCode( initialFact ), initialFact, recency, wmEntryPoint, false );
    }

    public TraitDefaultFactHandle(final long id,
                             final Object object,
                             final long recency,
                             final WorkingMemoryEntryPoint wmEntryPoint,
                             final boolean isTraitOrTraitable ) {
        this( id, determineIdentityHashCode( object ), object, recency, wmEntryPoint, isTraitOrTraitable );
    }

    public TraitDefaultFactHandle(final long id,
                             final int identityHashCode,
                             final Object object,
                             final long recency,
                             final WorkingMemoryEntryPoint wmEntryPoint,
                             final boolean isTraitOrTraitable ) {
        this.id = id;
        this.entryPointId = wmEntryPoint == null ? null : wmEntryPoint.getEntryPoint();
        this.wmEntryPoint = wmEntryPoint;
        this.recency = recency;
        setObject(object);
        this.identityHashCode = identityHashCode;
        this.traitType = determineTraitType(object, isTraitOrTraitable);
        if (wmEntryPoint != null) {
            setLinkedTuples( wmEntryPoint.getKnowledgeBase() );
            this.wmEntryPoint = wmEntryPoint;
        } else {
            this.linkedTuples = new SingleLinkedTuples();
        }
    }

    @Override
    public <K> K as( Class<K> klass ) throws ClassCastException {
        if ( klass.isAssignableFrom( object.getClass() ) ) {
            return (K) object;
        } else if ( this.isTraitOrTraitable() ) {
            TraitHelperImpl traitHelper = new TraitHelperImpl();
            K k = traitHelper.extractTrait(this, klass);
            if (k != null) {
                return k;
            } else {
                throw new RuntimeException(String.format("Cannot trait to %s", klass));
            }
        }
        throw new ClassCastException( "The Handle's Object can't be cast to " + klass );
    }

    @Override
    protected TraitTypeEnum determineTraitType(Object object, boolean isTraitOrTraitable) {
        if (isTraitOrTraitable) {
            return determineTraitType(object);
        } else {
            return TraitTypeEnum.NON_TRAIT;
        }
    }

    public TraitTypeEnum determineTraitType(Object object ) {
        if ( object instanceof TraitProxyImpl) {
            return TraitTypeEnum.TRAIT;
        } else if ( object instanceof CoreWrapper) {
            return TraitTypeEnum.WRAPPED_TRAITABLE;
        } else if ( object instanceof TraitableBean) {
            return TraitTypeEnum.TRAITABLE;
        } else {
            return TraitTypeEnum.LEGACY_TRAITABLE;
        }
    }
}
