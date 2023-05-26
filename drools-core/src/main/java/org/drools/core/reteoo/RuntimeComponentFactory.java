/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

import org.drools.core.SessionConfiguration;
import org.drools.core.common.AgendaFactory;
import org.drools.core.common.AgendaGroupFactory;
import org.drools.core.common.EntryPointFactory;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.management.DroolsManagementAgent;
import org.drools.core.management.GenericKieSessionMonitoringImpl;
import org.drools.core.marshalling.SerializablePlaceholderResolverStrategy;
import org.drools.core.rule.accessor.FactHandleFactory;
import org.drools.core.rule.accessor.GlobalResolver;
import org.drools.core.rule.consequence.KnowledgeHelper;
import org.drools.core.time.TimerService;
import org.kie.api.definition.rule.RuleBase;
import org.kie.api.internal.utils.KieService;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyAcceptor;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.KieSessionsPool;
import org.kie.api.runtime.StatelessKieSession;

public interface RuntimeComponentFactory extends KieService {

    String NO_RUNTIME = "Missing runtime. Please add the module org.drools:drools-kiesession to your classpath.";

    AgendaFactory getAgendaFactory(SessionConfiguration config);

    AgendaGroupFactory getAgendaGroupFactory();

    PropagationContextFactory getPropagationContextFactory();

    EntryPointFactory getEntryPointFactory();

    FactHandleFactory getFactHandleFactoryService();

    TraitFactory getTraitFactory(RuleBase knowledgeBase);

    KnowledgeHelper createKnowledgeHelper(ReteEvaluator reteEvaluator);

    InternalWorkingMemory createStatefulSession(RuleBase ruleBase, Environment environment, SessionConfiguration sessionConfig, boolean fromPool);

    StatelessKieSession createStatelessSession(RuleBase ruleBase, KieSessionConfiguration conf);

    KieSessionsPool createSessionsPool(RuleBase ruleBase, int initialSize);

    GenericKieSessionMonitoringImpl createStatefulSessionMonitor(DroolsManagementAgent.CBSKey cbsKey);

    GenericKieSessionMonitoringImpl createStatelessSessionMonitor(DroolsManagementAgent.CBSKey cbsKey);

    GlobalResolver createGlobalResolver(ReteEvaluator reteEvaluator, Environment environment);

    TimerService createTimerService(ReteEvaluator reteEvaluator);

    class Holder {
        private static final RuntimeComponentFactory INSTANCE = createInstance();

        static RuntimeComponentFactory createInstance() {
            RuntimeComponentFactory factory = KieService.load( RuntimeComponentFactory.class );
            if (factory == null) {
                throwExceptionForMissingRuntime();
                return null;
            }
            return factory;
        }
    }

    static RuntimeComponentFactory get() {
        return RuntimeComponentFactory.Holder.INSTANCE;
    }

    static <T> T throwExceptionForMissingRuntime() {
        throw new RuntimeException(NO_RUNTIME);
    }

    default ObjectMarshallingStrategy createDefaultObjectMarshallingStrategy(ObjectMarshallingStrategyAcceptor acceptor) {
        return new SerializablePlaceholderResolverStrategy(acceptor);
    }
}
