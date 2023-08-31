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

import java.util.Collection;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.factmodel.traits.Thing;
import org.drools.base.factmodel.traits.TraitableBean;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.base.beliefsystem.Mode;

public interface InternalWorkingMemoryActions
        extends
        InternalWorkingMemory,
        WorkingMemoryEntryPoint {

    void update(FactHandle handle,
                Object object,
                BitMask mask,
                Class<?> modifiedClass,
                InternalMatch internalMatch);

    FactHandle insert(Object object,
                      boolean dynamic,
                      RuleImpl rule,
                      TerminalNode terminalNode);

    FactHandle insertAsync(Object object);

    void updateTraits( InternalFactHandle h, BitMask mask, Class<?> modifiedClass, InternalMatch internalMatch);

    <T, K, X extends TraitableBean> Thing<K> shed(InternalMatch internalMatch, TraitableBean<K,X> core, Class<T> trait);

    <T, K> T don(InternalMatch internalMatch, K core, Collection<Class<? extends Thing>> traits, boolean b, Mode[] modes);

    <T, K> T don(InternalMatch internalMatch, K core, Class<T> trait, boolean b, Mode[] modes);
}
