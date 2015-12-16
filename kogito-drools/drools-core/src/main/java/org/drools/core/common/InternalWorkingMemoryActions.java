/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.common;

import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.spi.Activation;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitableBean;
import org.kie.internal.runtime.beliefs.Mode;

import java.util.Collection;

public interface InternalWorkingMemoryActions
        extends
        InternalWorkingMemory,
        InternalWorkingMemoryEntryPoint {
    public void update(FactHandle handle,
                       Object object,
                       BitMask mask,
                       Class<?> modifiedClass,
                       Activation activation);

    public void delete(FactHandle handle,
                       RuleImpl rule,
                        Activation activation);

    FactHandle insert(Object object,
                                 Object value,
                                 boolean dynamic,
                                 boolean logical,
                                 RuleImpl rule,
                                 Activation activation);

    void updateTraits( InternalFactHandle h, BitMask mask, Class<?> modifiedClass, Activation activation );

    <T, K, X extends TraitableBean> Thing<K> shed( Activation activation, TraitableBean<K,X> core, Class<T> trait );

    <T, K> T don( Activation activation, K core, Collection<Class<? extends Thing>> traits, boolean b, Mode[] modes );

    <T, K> T don( Activation activation, K core, Class<T> trait, boolean b, Mode[] modes );
}
