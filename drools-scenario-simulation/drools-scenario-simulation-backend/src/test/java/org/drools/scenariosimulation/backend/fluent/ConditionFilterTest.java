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