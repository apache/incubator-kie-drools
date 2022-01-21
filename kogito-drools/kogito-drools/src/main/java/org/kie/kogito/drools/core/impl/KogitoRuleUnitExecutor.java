/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.impl.RuleBase;
import org.drools.core.impl.RuleUnitExecutorImpl;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.util.bitmask.BitMask;
import org.drools.kiesession.consequence.DefaultKnowledgeHelper;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.kogito.Application;
import org.kie.kogito.drools.core.factory.KogitoDefaultFactHandle;
import org.kie.kogito.rules.RuleUnits;

public class KogitoRuleUnitExecutor extends RuleUnitExecutorImpl {

    private final Application application;

    public KogitoRuleUnitExecutor(RuleBase knowledgeBase, Application application) {
        super(knowledgeBase);
        this.application = application;
    }

    public KogitoRuleUnitExecutor(RuleBase knowledgeBase, SessionConfiguration sessionConfiguration, Application application) {
        super(knowledgeBase, sessionConfiguration);
        this.application = application;
    }

    public Application getApplication() {
        return application;
    }

    @Override
    public KnowledgeHelper createKnowledgeHelper() {
        return new RuleUnitKnowledgeHelper(this);
    }

    public static class RuleUnitKnowledgeHelper extends DefaultKnowledgeHelper {

        private final KogitoRuleUnitExecutor executor;

        public RuleUnitKnowledgeHelper(KogitoRuleUnitExecutor executor) {
            super(executor);
            this.executor = executor;
        }

        @Override
        public void run(String ruleUnitName) {
            executor.getApplication().get(RuleUnits.class).getRegisteredInstance(ruleUnitName).fire();
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

            ((InternalWorkingMemoryEntryPoint) h.getEntryPoint(executor)).update(h,
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

            h.getEntryPoint(executor).delete(handle,
                    this.activation.getRule(),
                    this.activation.getTuple().getTupleSink(),
                    fhState);
        }
    }
}
