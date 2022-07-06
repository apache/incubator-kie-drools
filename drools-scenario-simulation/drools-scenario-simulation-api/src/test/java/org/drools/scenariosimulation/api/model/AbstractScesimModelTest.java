package org.drools.scenariosimulation.api.model;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractScesimModelTest {

    private final static int SCENARIO_DATA = 5;
    private final static int FACT_MAPPINGS = 3;
    private AbstractScesimModel<Scenario> abstractScesimModelSpy;

    @Before
    public void init() {
        abstractScesimModelSpy = spy(new AbstractScesimModel<Scenario>() {

            @Override
            public AbstractScesimModel cloneModel() {
                return null;
            }

            @Override
            public Scenario addData(int index) {
                return null;
            }
        });
        IntStream.range(0, SCENARIO_DATA).forEach(index -> abstractScesimModelSpy.scesimData.add(getSpyScenario(index)));
        IntStream.range(0, FACT_MAPPINGS).forEach(index -> abstractScesimModelSpy.scesimModelDescriptor.getFactMappings().add(getSpyFactMapping()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getUnmodifiableData() {
        final List<Scenario> retrieved = abstractScesimModelSpy.getUnmodifiableData();
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.size()).isEqualTo(SCENARIO_DATA);
        retrieved.add(new Scenario());
    }

    @Test
    public void removeDataByIndex() {
        final Scenario dataByIndex = abstractScesimModelSpy.getDataByIndex(3);
        assertThat(abstractScesimModelSpy.scesimData.size()).isEqualTo(SCENARIO_DATA);
        assertThat(abstractScesimModelSpy.scesimData.contains(dataByIndex)).isTrue();
        abstractScesimModelSpy.removeDataByIndex(3);
        assertThat(abstractScesimModelSpy.scesimData.size()).isEqualTo(SCENARIO_DATA - 1);
        assertThat(abstractScesimModelSpy.scesimData.contains(dataByIndex)).isFalse();
    }

    @Test
    public void removeData() {
        final Scenario dataByIndex = abstractScesimModelSpy.getDataByIndex(3);
        assertThat(abstractScesimModelSpy.scesimData.size()).isEqualTo(SCENARIO_DATA);
        assertThat(abstractScesimModelSpy.scesimData.contains(dataByIndex)).isTrue();
        abstractScesimModelSpy.removeData(dataByIndex);
        assertThat(abstractScesimModelSpy.scesimData.size()).isEqualTo(SCENARIO_DATA - 1);
        assertThat(abstractScesimModelSpy.scesimData.contains(dataByIndex)).isFalse();
    }

    @Test
    public void getDataByIndex() {
        final Scenario retrieved = abstractScesimModelSpy.getDataByIndex(3);
        assertThat(retrieved).isNotNull();
    }

    @Test
    public void replaceData() {
        assertThat(abstractScesimModelSpy.scesimData.size()).isEqualTo(SCENARIO_DATA);
        final Scenario replaced = abstractScesimModelSpy.getDataByIndex(3);
        final Scenario replacement = new Scenario();
        abstractScesimModelSpy.replaceData(3, replacement);
        assertThat(abstractScesimModelSpy.scesimData.size()).isEqualTo(SCENARIO_DATA);
        assertThat(abstractScesimModelSpy.scesimData.contains(replaced)).isFalse();
        assertThat(abstractScesimModelSpy.scesimData.get(3)).isEqualTo(replacement);
    }

    @Test
    public void cloneData() {
        assertThat(abstractScesimModelSpy.scesimData.size()).isEqualTo(SCENARIO_DATA);
        final Scenario cloned = abstractScesimModelSpy.getDataByIndex(3);
        final Scenario clone = abstractScesimModelSpy.cloneData(3, 4);
        assertThat(clone).isNotNull();
        assertThat(abstractScesimModelSpy.scesimData.get(4)).isEqualTo(clone);
        assertThat(clone.getDescription()).isEqualTo(cloned.getDescription());
    }

    @Test
    public void clear() {
        abstractScesimModelSpy.clear();
        verify(abstractScesimModelSpy, times(1)).clearDatas();
    }

    @Test
    public void clearDatas() {
        assertThat(abstractScesimModelSpy.scesimData.size()).isEqualTo(SCENARIO_DATA);
        abstractScesimModelSpy.clearDatas();
        assertThat(abstractScesimModelSpy.scesimData.isEmpty()).isTrue();
    }

    @Test
    public void resetErrors() {
        abstractScesimModelSpy.resetErrors();
        abstractScesimModelSpy.scesimData.forEach(scesimData -> verify(scesimData, times(1)).resetErrors());
    }

    @Test
    public void removeFactMappingByIndex() {
        assertThat(abstractScesimModelSpy.scesimModelDescriptor.getFactMappings().size()).isEqualTo(FACT_MAPPINGS);
        final FactMapping factMappingByIndex = abstractScesimModelSpy.scesimModelDescriptor.getFactMappingByIndex(2);
        abstractScesimModelSpy.removeFactMappingByIndex(2);
        verify(abstractScesimModelSpy, times(1)).clearDatas(eq(factMappingByIndex));
        assertThat(abstractScesimModelSpy.scesimModelDescriptor.getFactMappings().size()).isEqualTo(FACT_MAPPINGS - 1);
        assertThat(abstractScesimModelSpy.scesimModelDescriptor.getFactMappings().contains(factMappingByIndex)).isFalse();
    }

    @Test
    public void removeFactMapping() {
        assertThat(abstractScesimModelSpy.scesimModelDescriptor.getFactMappings().size()).isEqualTo(FACT_MAPPINGS);
        final FactMapping factMappingByIndex = abstractScesimModelSpy.scesimModelDescriptor.getFactMappingByIndex(2);
        abstractScesimModelSpy.removeFactMapping(factMappingByIndex);
        verify(abstractScesimModelSpy, times(1)).clearDatas(eq(factMappingByIndex));
        assertThat(abstractScesimModelSpy.scesimModelDescriptor.getFactMappings().size()).isEqualTo(FACT_MAPPINGS - 1);
        assertThat(abstractScesimModelSpy.scesimModelDescriptor.getFactMappings().contains(factMappingByIndex)).isFalse();
    }

    @Test
    public void clearDatasByFactMapping() {
        final FactMapping factMappingByIndex = abstractScesimModelSpy.scesimModelDescriptor.getFactMappingByIndex(2);
        abstractScesimModelSpy.clearDatas(factMappingByIndex);
        final FactIdentifier factIdentifier = factMappingByIndex.getFactIdentifier();
        final ExpressionIdentifier expressionIdentifier = factMappingByIndex.getExpressionIdentifier();
        abstractScesimModelSpy.scesimData.forEach(scesimData ->
                                                          verify(scesimData, times(1)).removeFactMappingValueByIdentifiers(eq(factIdentifier), eq(expressionIdentifier)));
    }

    private Scenario getSpyScenario(int index) {
        Scenario toReturn = spy(new Scenario());
        toReturn.setDescription("INDEX-" + index);
        return toReturn;
    }

    private FactMapping getSpyFactMapping() {
        FactMapping toReturn = spy(new FactMapping());
        when(toReturn.getFactIdentifier()).thenReturn(mock(FactIdentifier.class));
        when(toReturn.getExpressionIdentifier()).thenReturn(mock(ExpressionIdentifier.class));
        return spy(new FactMapping());
    }
}
