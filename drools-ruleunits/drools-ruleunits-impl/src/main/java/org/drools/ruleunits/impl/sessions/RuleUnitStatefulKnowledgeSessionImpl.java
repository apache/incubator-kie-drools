/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.ruleunits.impl.sessions;

import org.drools.core.SessionConfiguration;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.rule.accessor.FactHandleFactory;
import org.drools.core.rule.consequence.KnowledgeHelper;
import org.drools.core.time.TimerService;
import org.drools.core.util.bitmask.BitMask;
import org.drools.kiesession.consequence.DefaultKnowledgeHelper;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.ruleunits.impl.facthandles.RuleUnitDefaultFactHandle;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.ruleunits.api.RuleUnits;

public class RuleUnitStatefulKnowledgeSessionImpl extends StatefulKnowledgeSessionImpl {

    private RuleUnits ruleUnits;

    public RuleUnitStatefulKnowledgeSessionImpl() {
    }

    public RuleUnitStatefulKnowledgeSessionImpl(long id, InternalKnowledgeBase kBase) {
        super(id, kBase);
    }

    public RuleUnitStatefulKnowledgeSessionImpl(long id, InternalKnowledgeBase kBase, boolean initInitFactHandle, SessionConfiguration config, Environment environment) {
        super(id, kBase, initInitFactHandle, config, environment);
    }

    public RuleUnitStatefulKnowledgeSessionImpl(long id, InternalKnowledgeBase kBase, FactHandleFactory handleFactory, long propagationContext, SessionConfiguration config, InternalAgenda agenda,
                                                Environment environment) {
        super(id, kBase, handleFactory, propagationContext, config, agenda, environment);
    }

    @Override
    protected TimerService createTimerService() {
        return RuleUnitTimerServiceFactory.getTimerService(this.config);
    }

    @Override
    public KnowledgeHelper createKnowledgeHelper() {
        return new RuleUnitKnowledgeHelper(this);
    }

    public RuleUnits getRuleUnits() {
        return ruleUnits;
    }

    public void setRuleUnits(RuleUnits ruleUnits) {
        this.ruleUnits = ruleUnits;
    }

    public static class RuleUnitKnowledgeHelper extends DefaultKnowledgeHelper {

        private final RuleUnitStatefulKnowledgeSessionImpl kogitoSession;

        public RuleUnitKnowledgeHelper(RuleUnitStatefulKnowledgeSessionImpl workingMemory) {
            super(workingMemory);
            this.kogitoSession = workingMemory;
        }

        @Override
        public void run(String ruleUnitName) {
            kogitoSession.getRuleUnits().getRegisteredInstance(ruleUnitName).fire();
        }

        @Override
        public void update(final FactHandle handle, BitMask mask, Class modifiedClass) {
            InternalFactHandle h = (InternalFactHandle) handle;

            if (h instanceof RuleUnitDefaultFactHandle && ((RuleUnitDefaultFactHandle) h).getDataStore() != null) {
                // This handle has been insert from a datasource, so update it
                ((RuleUnitDefaultFactHandle) h).getDataStore().update((RuleUnitDefaultFactHandle) h,
                        h.getObject(),
                        mask,
                        modifiedClass,
                        this.activation);
                return;
            }

            ((InternalWorkingMemoryEntryPoint) h.getEntryPoint(kogitoSession)).update(h,
                    ((InternalFactHandle) handle).getObject(),
                    mask,
                    modifiedClass,
                    this.activation);
            if (h.isTraitOrTraitable()) {
                toStatefulKnowledgeSession().updateTraits(h, mask, modifiedClass, this.activation);
            }
        }

        @Override
        public void delete(FactHandle handle, FactHandle.State fhState) {
            InternalFactHandle h = (InternalFactHandle) handle;

            if (h instanceof RuleUnitDefaultFactHandle && ((RuleUnitDefaultFactHandle) h).getDataStore() != null) {
                // This handle has been insert from a datasource, so remove from it
                ((RuleUnitDefaultFactHandle) h).getDataStore().delete((RuleUnitDefaultFactHandle) h,
                        this.activation.getRule(),
                        this.activation.getTuple().getTupleSink(),
                        fhState);
                return;
            }

            if (h.isTraiting()) {
                delete(((Thing) h.getObject()).getCore());
                return;
            }

            h.getEntryPoint(kogitoSession).delete(handle,
                    this.activation.getRule(),
                    this.activation.getTuple().getTupleSink(),
                    fhState);
        }
    }
}
