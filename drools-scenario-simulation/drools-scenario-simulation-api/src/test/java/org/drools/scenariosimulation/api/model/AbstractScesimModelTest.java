package org.drools.scenariosimulation.api.model;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        assertNotNull(retrieved);
        assertEquals(SCENARIO_DATA, retrieved.size());
        retrieved.add(new Scenario());
    }

    @Test
    public void removeDataByIndex() {
        final Scenario dataByIndex = abstractScesimModelSpy.getDataByIndex(3);
        assertEquals(SCENARIO_DATA, abstractScesimModelSpy.scesimData.size());
        assertTrue(abstractScesimModelSpy.scesimData.contains(dataByIndex));
        abstractScesimModelSpy.removeDataByIndex(3);
        assertEquals(SCENARIO_DATA - 1, abstractScesimModelSpy.scesimData.size());
        assertFalse(abstractScesimModelSpy.scesimData.contains(dataByIndex));
    }

    @Test
    public void removeData() {
        final Scenario dataByIndex = abstractScesimModelSpy.getDataByIndex(3);
        assertEquals(SCENARIO_DATA, abstractScesimModelSpy.scesimData.size());
        assertTrue(abstractScesimModelSpy.scesimData.contains(dataByIndex));
        abstractScesimModelSpy.removeData(dataByIndex);
        assertEquals(SCENARIO_DATA - 1, abstractScesimModelSpy.scesimData.size());
        assertFalse(abstractScesimModelSpy.scesimData.contains(dataByIndex));
    }

    @Test
    public void getDataByIndex() {
        final Scenario retrieved = abstractScesimModelSpy.getDataByIndex(3);
        assertNotNull(retrieved);
    }

    @Test
    public void replaceData() {
        assertEquals(SCENARIO_DATA, abstractScesimModelSpy.scesimData.size());
        final Scenario replaced = abstractScesimModelSpy.getDataByIndex(3);
        final Scenario replacement = new Scenario();
        abstractScesimModelSpy.replaceData(3, replacement);
        assertEquals(SCENARIO_DATA, abstractScesimModelSpy.scesimData.size());
        assertFalse(abstractScesimModelSpy.scesimData.contains(replaced));
        assertEquals(replacement, abstractScesimModelSpy.scesimData.get(3));
    }

    @Test
    public void cloneData() {
        assertEquals(SCENARIO_DATA, abstractScesimModelSpy.scesimData.size());
        final Scenario cloned = abstractScesimModelSpy.getDataByIndex(3);
        final Scenario clone = abstractScesimModelSpy.cloneData(3, 4);
        assertNotNull(clone);
        assertEquals(clone, abstractScesimModelSpy.scesimData.get(4));
        assertEquals(cloned.getDescription(), clone.getDescription());
    }

    @Test
    public void clear() {
        abstractScesimModelSpy.clear();
        verify(abstractScesimModelSpy, times(1)).clearDatas();
    }

    @Test
    public void clearDatas() {
        assertEquals(SCENARIO_DATA, abstractScesimModelSpy.scesimData.size());
        abstractScesimModelSpy.clearDatas();
        assertTrue(abstractScesimModelSpy.scesimData.isEmpty());
    }

    @Test
    public void resetErrors() {
        abstractScesimModelSpy.resetErrors();
        abstractScesimModelSpy.scesimData.forEach(scesimData -> verify(scesimData, times(1)).resetErrors());
    }

    @Test
    public void removeFactMappingByIndex() {
        assertEquals(FACT_MAPPINGS, abstractScesimModelSpy.scesimModelDescriptor.getFactMappings().size());
        final FactMapping factMappingByIndex = abstractScesimModelSpy.scesimModelDescriptor.getFactMappingByIndex(2);
        abstractScesimModelSpy.removeFactMappingByIndex(2);
        verify(abstractScesimModelSpy, times(1)).clearDatas(eq(factMappingByIndex));
        assertEquals(FACT_MAPPINGS - 1, abstractScesimModelSpy.scesimModelDescriptor.getFactMappings().size());
        assertFalse(abstractScesimModelSpy.scesimModelDescriptor.getFactMappings().contains(factMappingByIndex));
    }

    @Test
    public void removeFactMapping() {
        assertEquals(FACT_MAPPINGS, abstractScesimModelSpy.scesimModelDescriptor.getFactMappings().size());
        final FactMapping factMappingByIndex = abstractScesimModelSpy.scesimModelDescriptor.getFactMappingByIndex(2);
        abstractScesimModelSpy.removeFactMapping(factMappingByIndex);
        verify(abstractScesimModelSpy, times(1)).clearDatas(eq(factMappingByIndex));
        assertEquals(FACT_MAPPINGS - 1, abstractScesimModelSpy.scesimModelDescriptor.getFactMappings().size());
        assertFalse(abstractScesimModelSpy.scesimModelDescriptor.getFactMappings().contains(factMappingByIndex));
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
