package org.drools.scenariosimulation.api.model;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ScenarioTest {

    private ScesimModelDescriptor scesimModelDescriptor;
    private Scenario scenario;
    private FactIdentifier factIdentifier;
    private ExpressionIdentifier expressionIdentifier;
    private Simulation simulation;

    @Before
    public void init() {
        simulation = new Simulation();
        scesimModelDescriptor = simulation.getScesimModelDescriptor();
        scenario = simulation.addData();
        factIdentifier = FactIdentifier.create("test fact", String.class.getCanonicalName());
        expressionIdentifier = ExpressionIdentifier.create("test expression", FactMappingType.EXPECT);
    }
    
    @Test
    public void addFactMappingValue() {
        FactMappingValue factMappingValue = scenario.addMappingValue(factIdentifier, expressionIdentifier, "test value");
        
        Optional<FactMappingValue> retrieved = scenario.getFactMappingValue(factIdentifier, expressionIdentifier);
        assertThat(retrieved).isPresent();
        assertThat(factMappingValue.getRawValue()).isEqualTo("test value");
    }

    @Test
    public void removeFactMappingValueByIdentifiers() {
        scenario.addMappingValue(factIdentifier, expressionIdentifier, "test value");

        scenario.removeFactMappingValueByIdentifiers(factIdentifier, expressionIdentifier);

        assertThat(scenario.getFactMappingValue(factIdentifier, expressionIdentifier)).isNotPresent();
    }

    @Test
    public void removeFactMappingValue() {
        scenario.addMappingValue(factIdentifier, expressionIdentifier, "test value");
        Optional<FactMappingValue> retrieved = scenario.getFactMappingValue(factIdentifier, expressionIdentifier);

        scenario.removeFactMappingValue(retrieved.get());

        assertThat(scenario.getFactMappingValue(factIdentifier, expressionIdentifier)).isNotPresent();
    }

    @Test(expected = IllegalArgumentException.class)
    public void addMappingValueTest() {
        scenario.addMappingValue(factIdentifier, expressionIdentifier, "test value");
        // Should fail
        scenario.addMappingValue(factIdentifier, expressionIdentifier, "test value");
    }

    @Test
    public void getDescription_initialDescriptionIsEmpty() {
        assertThat(scenario.getDescription()).isEqualTo("");
    }

    @Test
    public void getDescription_descriptionIsSetToNullValue() {
        String description = null;
        scenario.addMappingValue(FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION, description);
        
        assertThat(scenario.getDescription()).isEqualTo("");
    }
    
    @Test
    public void getDescription_descriptionIsSetToNonNullValue() {
        String description = "Test Description";
        scenario.addMappingValue(FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION, description);
        
        assertThat(scenario.getDescription()).isEqualTo(description);
    }

    @Test
    public void setDescription_nullValue() {
        Scenario scenarioWithDescriptionNull = simulation.addData();

        scenarioWithDescriptionNull.setDescription(null);
        
        assertThat(scenarioWithDescriptionNull.getDescription()).isEqualTo("");
    }

    @Test
    public void getDescription3() {
        Scenario scenarioWithDescriptionNull = simulation.addData();

        scenarioWithDescriptionNull.setDescription("Test Description");
        
        assertThat(scenarioWithDescriptionNull.getDescription()).isEqualTo("Test Description");
    }
    
    @Test
    public void addOrUpdateMappingValue() {
        Object value2 = "Test 2";
        FactMappingValue factMappingValue = scenario.addMappingValue(factIdentifier, expressionIdentifier, "Test 1");

        FactMappingValue factMappingValue1 = scenario.addOrUpdateMappingValue(factIdentifier, expressionIdentifier, value2);

        assertThat(factMappingValue1).isEqualTo(factMappingValue);
        assertThat(factMappingValue1.getRawValue()).isEqualTo(value2);
    }
}