/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.base.TraitHelper;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.rule.FactHandle;

public interface InternalWorkingMemoryEntryPoint extends WorkingMemoryEntryPoint {

    TraitHelper getTraitHelper();

    PropagationContextFactory getPctxFactory();

    FactHandle insert( Object object,
                       boolean dynamic,
                       RuleImpl rule,
                       Activation activation );

    void insert(InternalFactHandle handle,
                Object object,
                RuleImpl rule,
                Activation activation,
                ObjectTypeConf typeConf );

    void insert(InternalFactHandle handle,
                Object object,
                RuleImpl rule,
                Activation activation,
                ObjectTypeConf typeConf,
                PropagationContext pctx );

    FactHandle insertAsync(Object object);

    InternalFactHandle update(InternalFactHandle handle,
                              Object object,
                              BitMask mask,
                              Class<?> modifiedClass,
                              Activation activation);

    void update(InternalFactHandle handle,
                Object object,
                Object originalObject,
                ObjectTypeConf typeConf,
                RuleImpl rule,
                PropagationContext propagationContext);

    PropagationContext delete(InternalFactHandle handle,
                              Object object,
                              ObjectTypeConf typeConf,
                              RuleImpl rule,
                              Activation activation);

    void removeFromObjectStore(InternalFactHandle handle);
}
