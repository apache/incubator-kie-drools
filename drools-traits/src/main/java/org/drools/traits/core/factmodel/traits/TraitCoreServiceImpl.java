/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.traits.core.factmodel.traits;

import org.drools.core.base.TraitHelper;
import org.drools.core.factmodel.traits.TraitCoreService;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.TraitRegistry;
import org.drools.traits.core.base.TraitHelperImpl;
import org.drools.core.common.InternalWorkingMemoryActions;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.factmodel.ClassBuilder;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.traits.core.reteoo.TraitObjectTypeNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.ObjectType;

public class TraitCoreServiceImpl implements TraitCoreService {

    @Override
    public TraitRegistry createRegistry() {
        return new TraitRegistryImpl();
    }

    @Override
    public TraitFactory createTraitFactory() {
        return new TraitFactoryImpl();
    }

    @Override
    public ClassBuilder createTraitProxyClassBuilder() {
        return new TraitMapProxyClassBuilderImpl();
    }

    @Override
    public ClassBuilder createPropertyWrapperBuilder() {
        return new TraitMapPropertyWrapperClassBuilderImpl();
    }

    @Override
    public TraitHelper createTraitHelper() {
        return new TraitHelperImpl();
    }

    @Override
    public TraitHelper createTraitHelper(InternalWorkingMemoryActions workingMemory, InternalWorkingMemoryEntryPoint nep) {
        return new TraitHelperImpl(workingMemory, nep);
    }

    @Override
    public Class<?> baseTraitProxyClass() {
        return TraitProxyImpl.class;
    }

    @Override
    public ObjectTypeNode createTraitObjectTypeNode(int id, EntryPointNode source, ObjectType objectType, BuildContext context) {
        return new TraitObjectTypeNode(id, source, objectType, context );
    }
}
