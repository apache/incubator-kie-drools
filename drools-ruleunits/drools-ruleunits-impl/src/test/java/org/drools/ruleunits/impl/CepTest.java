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
package org.drools.ruleunits.impl;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.impl.domain.StockTick;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.time.SessionPseudoClock;

import static org.assertj.core.api.Assertions.assertThat;

public class CepTest {

    @Test
    public void cep_pseudoClock() {
        StockTickUnit unit = new StockTickUnit();
        try (RuleUnitInstance<StockTickUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit)) {
            unit.getStockTicks().append(new StockTick("DROO"));

            SessionPseudoClock sessionClock = unitInstance.getClock();
            sessionClock.advanceTime(6L, TimeUnit.SECONDS);

            unit.getStockTicks().append(new StockTick("ACME"));

            unitInstance.fire();

            assertThat(unit.getResults()).hasSize(1);
            assertThat(unit.getResults().get(0).getCompany()).isEqualTo("ACME");

            Collection<FactHandle> factHandles = getFactHandlesInEntryPoint(unitInstance, "stockTicks");
            assertThat(factHandles).hasSize(2);

            sessionClock.advanceTime(5L, TimeUnit.SECONDS);
            unitInstance.fire();

            // DROO is expired
            factHandles = getFactHandlesInEntryPoint(unitInstance, "stockTicks");
            assertThat(factHandles).hasSize(1);
            assertThat(((StockTick) ((InternalFactHandle) factHandles.iterator().next()).getObject()).getCompany()).isEqualTo("ACME");
        }
    }

    private Collection<FactHandle> getFactHandlesInEntryPoint(RuleUnitInstance<StockTickUnit> unitInstance, String entryPointName) {
        ReteEvaluator evaluator = (ReteEvaluator) ((AbstractRuleUnitInstance) unitInstance).getEvaluator();
        WorkingMemoryEntryPoint entryPoint = evaluator.getEntryPoint(entryPointName);
        return entryPoint.getFactHandles();
    }
}
