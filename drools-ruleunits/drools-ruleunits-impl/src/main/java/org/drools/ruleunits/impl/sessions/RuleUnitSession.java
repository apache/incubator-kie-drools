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
package org.drools.ruleunits.impl.sessions;

import org.drools.core.SessionConfiguration;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.impl.RuleBase;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.util.bitmask.BitMask;
import org.drools.kiesession.consequence.DefaultKnowledgeHelper;
import org.drools.ruleunits.impl.facthandles.RuleUnitDefaultFactHandle;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.ruleunits.api.RuleUnits;

public class RuleUnitSession extends RuleUnitExecutorImpl {

    private final RuleUnits ruleUnits;

    public RuleUnitSession(RuleBase knowledgeBase, RuleUnits ruleUnits) {
        super(knowledgeBase);
        this.ruleUnits = ruleUnits;
    }

    public RuleUnitSession(RuleBase knowledgeBase, SessionConfiguration sessionConfiguration, RuleUnits ruleUnits) {
        super(knowledgeBase, sessionConfiguration);
        this.ruleUnits = ruleUnits;
    }

    public RuleUnits getRuleUnits() {
        return ruleUnits;
    }

    @Override
    public KnowledgeHelper createKnowledgeHelper() {
        return new RuleUnitKnowledgeHelper(this);
    }

    public static class RuleUnitKnowledgeHelper extends DefaultKnowledgeHelper {

        private final RuleUnitSession executor;

        public RuleUnitKnowledgeHelper(RuleUnitSession executor) {
            super(executor);
            this.executor = executor;
        }

        @Override
        public void run(String ruleUnitName) {
            executor.getRuleUnits().getRegisteredInstance(ruleUnitName).fire();
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

            h.getEntryPoint(executor).delete(handle,
                    this.activation.getRule(),
                    this.activation.getTuple().getTupleSink(),
                    fhState);
        }
    }
}
