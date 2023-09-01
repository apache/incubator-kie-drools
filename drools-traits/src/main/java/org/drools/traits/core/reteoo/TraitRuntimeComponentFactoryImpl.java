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
package org.drools.traits.core.reteoo;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.drools.core.common.EntryPointFactory;
import org.drools.kiesession.rulebase.SessionsAwareKnowledgeBase;
import org.drools.base.RuleBase;
import org.drools.core.rule.accessor.FactHandleFactory;
import org.drools.kiesession.factory.RuntimeComponentFactoryImpl;
import org.drools.traits.core.common.TraitEntryPointFactory;
import org.drools.traits.core.factmodel.TraitFactoryImpl;
import org.drools.traits.core.factmodel.TraitRegistry;

public class TraitRuntimeComponentFactoryImpl extends RuntimeComponentFactoryImpl implements TraitRuntimeComponentFactory {

    private final TraitFactHandleFactory traitFactHandleFactory = new TraitFactHandleFactory();

    private final Map<RuleBase, TraitFactoryImpl> traitFactoryCache = new WeakHashMap<>(new IdentityHashMap<>());

    @Override
    public TraitFactoryImpl getTraitFactory(RuleBase knowledgeBase) {
        if (knowledgeBase instanceof SessionsAwareKnowledgeBase) {
            knowledgeBase = ((SessionsAwareKnowledgeBase) knowledgeBase).getDelegate();
        }
        return traitFactoryCache.computeIfAbsent(knowledgeBase, TraitFactoryImpl::new);
    }

    @Override
    public TraitRegistry getTraitRegistry(RuleBase knowledgeBase) {
        return getTraitFactory(knowledgeBase).getTraitRegistry();
    }

    @Override
    public FactHandleFactory getFactHandleFactoryService() {
        return traitFactHandleFactory;
    }

    @Override
    public EntryPointFactory getEntryPointFactory() {
        return new TraitEntryPointFactory();
    }

    @Override
    public int servicePriority() {
        return 1;
    }
}
