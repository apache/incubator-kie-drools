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
package org.drools.scenariosimulation.backend.fluent;

import java.util.function.Function;
import java.util.List;

import org.drools.scenariosimulation.api.model.ExpressionIdentifier;
import org.drools.scenariosimulation.api.model.FactIdentifier;
import org.drools.scenariosimulation.api.model.FactMappingValue;
import org.drools.scenariosimulation.backend.runner.model.ScenarioResult;
import org.drools.scenariosimulation.backend.runner.model.ValueWrapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConditionFilterTest {

	
    @Test
    public void accept() {
        ConditionFilter conditionFilter = createConditionFilter(ValueWrapper::of);

        assertThat(conditionFilter.accept(1)).isFalse();
        assertThat(conditionFilter.accept("String")).isTrue();
    }

    
    @Test
    public void acceptWithFailure() {
        ConditionFilter conditionFilterFail = createConditionFilter(object -> ValueWrapper.errorWithValidValue(null, null));
        
        assertThat(conditionFilterFail.accept("String")).isFalse();
    }

	private ConditionFilter createConditionFilter(Function<Object, ValueWrapper> matchFunction) {
		FactMappingValue factMappingValue = new FactMappingValue(FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION, "Test");
        ScenarioResult scenarioResult = new ScenarioResult(factMappingValue);
        return new ConditionFilter(List.of(new FactCheckerHandle(String.class, matchFunction, scenarioResult)));
	}
}