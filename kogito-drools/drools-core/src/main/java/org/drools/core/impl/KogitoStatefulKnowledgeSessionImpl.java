/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.impl;

import org.drools.core.SessionConfiguration;
import org.drools.core.WorkingMemory;
import org.drools.core.base.DefaultKnowledgeHelper;
import org.drools.core.base.WrappedStatefulKnowledgeSessionForRHS;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.kogito.factory.KogitoDefaultFactHandle;
import org.drools.core.spi.AbstractProcessContext;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.KogitoProcessContext;
import org.drools.core.time.KogitoTimerServiceFactory;
import org.drools.core.time.TimerService;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.kogito.Application;
import org.kie.kogito.rules.RuleUnits;

public class KogitoStatefulKnowledgeSessionImpl extends StatefulKnowledgeSessionImpl {

    private Application application;

    public KogitoStatefulKnowledgeSessionImpl() {
    }

    public KogitoStatefulKnowledgeSessionImpl(long id, InternalKnowledgeBase kBase) {
        super(id, kBase);
    }

    public KogitoStatefulKnowledgeSessionImpl(long id, InternalKnowledgeBase kBase, boolean initInitFactHandle, SessionConfiguration config, Environment environment) {
        super(id, kBase, initInitFactHandle, config, environment);
    }

    public KogitoStatefulKnowledgeSessionImpl(long id, InternalKnowledgeBase kBase, FactHandleFactory handleFactory, long propagationContext, SessionConfiguration config, InternalAgenda agenda, Environment environment) {
        super(id, kBase, handleFactory, propagationContext, config, agenda, environment);
    }

    @Override
    protected TimerService createTimerService() {
        return KogitoTimerServiceFactory.getTimerService( this.config );
    }

    @Override
    public ProcessInstance getProcessInstance(Object processInstanceId) {
        return getProcessInstance( (String) processInstanceId );
    }

    @Override
    public ProcessInstance getProcessInstance(String processInstanceId) {
        return getProcessRuntime().getProcessInstance( processInstanceId );
    }

    @Override
    public ProcessInstance startProcessInstance(String processInstanceId) {
        return getProcessRuntime().startProcessInstance( processInstanceId );
    }

    @Override
    public void abortProcessInstance(String processInstanceId) {
        getProcessRuntime().abortProcessInstance( processInstanceId );
    }

    @Override
    public void signalEvent(String type, Object event, String processInstanceId) {
        getProcessRuntime().signalEvent( type, event, processInstanceId );
    }

    @Override
    public KnowledgeHelper createKnowledgeHelper() {
        return new RuleUnitKnowledgeHelper( this );
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication( Application application ) {
        this.application = application;
    }

    public static class RuleUnitKnowledgeHelper extends DefaultKnowledgeHelper {

        private final KogitoStatefulKnowledgeSessionImpl kogitoSession;

        public RuleUnitKnowledgeHelper( KogitoStatefulKnowledgeSessionImpl workingMemory ) {
            super(workingMemory);
            this.kogitoSession = workingMemory;
        }

        @Override
        public void run(String ruleUnitName) {
            kogitoSession.getApplication().get(RuleUnits.class).getRegisteredInstance( ruleUnitName ).fire();
        }

        @Override
        public void update( final FactHandle handle, BitMask mask, Class modifiedClass ) {
            InternalFactHandle h = (InternalFactHandle) handle;

            if (h instanceof KogitoDefaultFactHandle && (( KogitoDefaultFactHandle ) h).getDataStore() != null) {
                // This handle has been insert from a datasource, so update it
                (( KogitoDefaultFactHandle ) h).getDataStore().update( (KogitoDefaultFactHandle) h,
                        h.getObject(),
                        mask,
                        modifiedClass,
                        this.activation );
                return;
            }

            (( InternalWorkingMemoryEntryPoint ) h.getEntryPoint(kogitoSession)).update( h,
                    ((InternalFactHandle)handle).getObject(),
                    mask,
                    modifiedClass,
                    this.activation );
            if ( h.isTraitOrTraitable() ) {
                workingMemory.updateTraits( h, mask, modifiedClass, this.activation );
            }
        }

        @Override
        public void delete(FactHandle handle, FactHandle.State fhState ) {
            InternalFactHandle h = (InternalFactHandle) handle;

            if (h instanceof KogitoDefaultFactHandle && (( KogitoDefaultFactHandle ) h).getDataStore() != null) {
                // This handle has been insert from a datasource, so remove from it
                (( KogitoDefaultFactHandle ) h).getDataStore().delete( (KogitoDefaultFactHandle) h,
                        this.activation.getRule(),
                        this.activation.getTuple().getTupleSink(),
                        fhState);
                return;
            }

            if ( h.isTraiting() ) {
                delete( (( Thing ) h.getObject()).getCore() );
                return;
            }

            h.getEntryPoint(kogitoSession).delete(handle,
                    this.activation.getRule(),
                    this.activation.getTuple().getTupleSink(),
                    fhState);
        }

        @Override
        protected boolean sameNodeInstance( NodeInstance subNodeInstance, String nodeInstanceId ) {
            return subNodeInstance.getId().equals( nodeInstanceId );
        }

        @Override
        protected AbstractProcessContext createProcessContext() {
            return new KogitoProcessContext(workingMemory.getKnowledgeRuntime());
        }

        @Override
        protected WrappedStatefulKnowledgeSessionForRHS createWrappedSession( WorkingMemory workingMemory ) {
            return new KogitoWrappedStatefulKnowledgeSessionForRHS( (KogitoStatefulKnowledgeSessionImpl) workingMemory );
        }
    }

    public static class KogitoWrappedStatefulKnowledgeSessionForRHS extends WrappedStatefulKnowledgeSessionForRHS {

        public KogitoWrappedStatefulKnowledgeSessionForRHS( KogitoStatefulKnowledgeSessionImpl delegate ) {
            super(delegate);
        }

        @Override
        public ProcessInstance getProcessInstance(String id) {
            return ((KogitoStatefulKnowledgeSessionImpl)delegate).getProcessInstance(id);
        }
    }
}
