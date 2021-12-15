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
package org.kie.kogito.drools.core.impl;

import org.drools.core.SessionConfiguration;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.spi.AbstractProcessContext;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.time.TimerService;
import org.drools.core.util.bitmask.BitMask;
import org.drools.kiesession.consequence.DefaultKnowledgeHelper;
import org.drools.kiesession.consequence.StatefulKnowledgeSessionForRHS;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.kogito.Application;
import org.kie.kogito.drools.core.KogitoWorkingMemory;
import org.kie.kogito.drools.core.factory.KogitoDefaultFactHandle;
import org.kie.kogito.drools.core.spi.KogitoProcessContextImpl;
import org.kie.kogito.drools.core.time.KogitoTimerServiceFactory;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.rules.RuleUnits;

public class KogitoStatefulKnowledgeSessionImpl extends StatefulKnowledgeSessionImpl implements KogitoWorkingMemory, KogitoProcessRuntime.Provider {

    private Application application;

    public KogitoStatefulKnowledgeSessionImpl() {
    }

    public KogitoStatefulKnowledgeSessionImpl(long id, InternalKnowledgeBase kBase) {
        super(id, kBase);
    }

    public KogitoStatefulKnowledgeSessionImpl(long id, InternalKnowledgeBase kBase, boolean initInitFactHandle, SessionConfiguration config, Environment environment) {
        super(id, kBase, initInitFactHandle, config, environment);
    }

    public KogitoStatefulKnowledgeSessionImpl(long id, InternalKnowledgeBase kBase, FactHandleFactory handleFactory, long propagationContext, SessionConfiguration config, InternalAgenda agenda,
            Environment environment) {
        super(id, kBase, handleFactory, propagationContext, config, agenda, environment);
    }

    @Override
    public KogitoProcessRuntime getKogitoProcessRuntime() {
        return ((KogitoProcessRuntime.Provider) getProcessRuntime()).getKogitoProcessRuntime();
    }

    @Override
    protected TimerService createTimerService() {
        return KogitoTimerServiceFactory.getTimerService(this.config);
    }

    @Override
    public ProcessInstance getProcessInstance(Object processInstanceId) {
        return getProcessInstance((String) processInstanceId);
    }

    @Override
    public KogitoProcessInstance getProcessInstance(String processInstanceId) {
        return getKogitoProcessRuntime().getProcessInstance(processInstanceId);
    }

    @Override
    public KogitoProcessInstance getProcessInstance(String processInstanceId, boolean readonly) {
        throw new UnsupportedOperationException();
    }

    @Override
    public KnowledgeHelper createKnowledgeHelper() {
        return new RuleUnitKnowledgeHelper(this);
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public static class RuleUnitKnowledgeHelper extends DefaultKnowledgeHelper {

        private final KogitoStatefulKnowledgeSessionImpl kogitoSession;

        public RuleUnitKnowledgeHelper(KogitoStatefulKnowledgeSessionImpl workingMemory) {
            super(workingMemory);
            this.kogitoSession = workingMemory;
        }

        @Override
        public void run(String ruleUnitName) {
            kogitoSession.getApplication().get(RuleUnits.class).getRegisteredInstance(ruleUnitName).fire();
        }

        @Override
        public void update(final FactHandle handle, BitMask mask, Class modifiedClass) {
            InternalFactHandle h = (InternalFactHandle) handle;

            if (h instanceof KogitoDefaultFactHandle && ((KogitoDefaultFactHandle) h).getDataStore() != null) {
                // This handle has been insert from a datasource, so update it
                ((KogitoDefaultFactHandle) h).getDataStore().update((KogitoDefaultFactHandle) h,
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

            if (h instanceof KogitoDefaultFactHandle && ((KogitoDefaultFactHandle) h).getDataStore() != null) {
                // This handle has been insert from a datasource, so remove from it
                ((KogitoDefaultFactHandle) h).getDataStore().delete((KogitoDefaultFactHandle) h,
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

        @Override
        protected boolean sameNodeInstance(NodeInstance subNodeInstance, String nodeInstanceId) {
            return ((KogitoNodeInstance) subNodeInstance).getStringId().equals(nodeInstanceId);
        }

        @Override
        protected AbstractProcessContext createProcessContext() {
            return new KogitoProcessContextImpl(toStatefulKnowledgeSession());
        }

        @Override
        protected KogitoReteEvaluatorForRHS toStatefulKnowledgeSession() {
            return new KogitoReteEvaluatorForRHS((KogitoStatefulKnowledgeSessionImpl) reteEvaluator);
        }
    }

    public static class KogitoReteEvaluatorForRHS extends StatefulKnowledgeSessionForRHS implements KogitoProcessRuntime.Provider {

        public KogitoReteEvaluatorForRHS(KogitoStatefulKnowledgeSessionImpl delegate) {
            super(delegate);
        }

        @Override
        public ProcessInstance getProcessInstance(long id) {
            throw new UnsupportedOperationException();
        }

        public ProcessInstance getProcessInstance(String id) {
            return ((KogitoStatefulKnowledgeSessionImpl) delegate).getProcessInstance(id);
        }

        @Override
        public KogitoProcessRuntime getKogitoProcessRuntime() {
            return ((KogitoProcessRuntime.Provider) delegate).getKogitoProcessRuntime();
        }
    }
}
