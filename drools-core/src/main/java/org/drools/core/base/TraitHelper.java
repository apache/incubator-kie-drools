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
package org.drools.core.base;

import java.util.Collection;

import org.drools.base.beliefsystem.Mode;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.factmodel.traits.Thing;
import org.drools.base.factmodel.traits.TraitableBean;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.util.bitmask.BitMask;

public interface TraitHelper {

    <K> K extractTrait(InternalFactHandle defaultFactHandle, Class<K> klass);

    <T, K> T don(InternalMatch internalMatch, K core, Collection<Class<? extends Thing>> traits, boolean logical, Mode... modes);

    <T, K> T don(InternalMatch internalMatch, K core, Class<T> trait, boolean logical, Mode... modes);

    <T, K, X extends TraitableBean> Thing<K> shed(TraitableBean<K, X> core, Class<T> trait, InternalMatch internalMatch);

    void replaceCore(InternalFactHandle handle, Object object, Object originalObject, BitMask modificationMask, Class<? extends Object> aClass, InternalMatch internalMatch);

    void deleteWMAssertedTraitProxies(InternalFactHandle handle, RuleImpl rule, TerminalNode terminalNode);

    void updateTraits(final InternalFactHandle handle, BitMask mask, Class<?> modifiedClass, InternalMatch internalMatch);
}
