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

import java.util.concurrent.locks.ReentrantLock;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.TraitHelper;
import org.drools.core.common.ClassAwareObjectStore;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemoryActions;
import org.drools.kiesession.entrypoints.NamedEntryPoint;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.traits.core.factmodel.TraitProxy;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.EntryPointId;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.traits.core.base.TraitHelperImpl;

public class TraitNamedEntryPoint extends NamedEntryPoint {

    protected TraitHelper traitHelper;

    public TraitNamedEntryPoint(EntryPointId entryPoint,
                                EntryPointNode entryPointNode,
                                ReteEvaluator reteEvaluator) {
        this(entryPoint,
             entryPointNode,
             reteEvaluator,
             new ReentrantLock());
    }

    public TraitNamedEntryPoint(EntryPointId entryPoint,
                                EntryPointNode entryPointNode,
                                ReteEvaluator reteEvaluator,
                                ReentrantLock lock) {
        this.entryPoint = entryPoint;
        this.entryPointNode = entryPointNode;
        this.reteEvaluator = reteEvaluator;
        this.ruleBase = this.reteEvaluator.getKnowledgeBase();
        this.lock = lock;
        this.handleFactory = this.reteEvaluator.getFactHandleFactory();
        this.pctxFactory = RuntimeComponentFactory.get().getPropagationContextFactory();
        boolean isEqualityBehaviour = RuleBaseConfiguration.AssertBehaviour.EQUALITY.equals(this.ruleBase.getConfiguration().getAssertBehaviour());
        this.objectStore = new ClassAwareObjectStore(isEqualityBehaviour, this.lock);
        this.traitHelper = new TraitHelperImpl((InternalWorkingMemoryActions) reteEvaluator, this);
    }

    @Override
    protected void beforeUpdate(InternalFactHandle handle, Object object, Activation activation, Object originalObject, PropagationContext propagationContext) {
        if (handle.isTraitable() && object != originalObject
                && object instanceof TraitableBean && originalObject instanceof TraitableBean) {
            this.traitHelper.replaceCore(handle, object, originalObject, propagationContext.getModificationMask(), object.getClass(), activation);
        }
    }

    @Override
    protected void afterRetract(InternalFactHandle handle, RuleImpl rule, TerminalNode terminalNode) {
        if (handle.isTraiting() && handle.getObject() instanceof TraitProxy) {
            (((TraitProxy) handle.getObject()).getObject()).removeTrait(((TraitProxy) handle.getObject())._getTypeCode());
        } else if (handle.isTraitable()) {
            traitHelper.deleteWMAssertedTraitProxies(handle, rule, terminalNode);
        }
    }

    @Override
    protected void beforeDestroy(RuleImpl rule, TerminalNode terminalNode, InternalFactHandle handle) {
        if (handle.isTraitable()) {
            traitHelper.deleteWMAssertedTraitProxies(handle, rule, terminalNode);
        }
    }

    @Override
    public TraitHelper getTraitHelper() {
        return traitHelper;
    }
}
