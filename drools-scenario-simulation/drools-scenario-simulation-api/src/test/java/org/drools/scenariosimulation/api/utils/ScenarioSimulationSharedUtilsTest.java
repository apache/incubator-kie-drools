package org.drools.scenariosimulation.api.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ScenarioSimulationSharedUtilsTest {

    List<String> listValues = Arrays.asList(List.class.getCanonicalName(),
                                            ArrayList.class.getCanonicalName(),
                                            LinkedList.class.getCanonicalName());

    List<String> mapValues = Arrays.asList(Map.class.getCanonicalName(),
                                           HashMap.class.getCanonicalName(),
                                           LinkedHashMap.class.getCanonicalName(),
                                           TreeMap.class.getCanonicalName());

    @Test
    public void isCollectionOrMap() {
        assertThat(listValues).allMatch(ScenarioSimulationSharedUtils::isCollectionOrMap);
        assertThat(mapValues).allMatch(ScenarioSimulationSharedUtils::isCollectionOrMap);
        assertThat(ScenarioSimulationSharedUtils.isCollectionOrMap(Collection.class.getCanonicalName())).isTrue();
    }

    @Test
    public void isCollection() {
        assertThat(listValues).allMatch(ScenarioSimulationSharedUtils::isCollection);
        assertThat(mapValues).noneMatch(ScenarioSimulationSharedUtils::isCollection);
        assertThat(ScenarioSimulationSharedUtils.isCollectionOrMap(Collection.class.getCanonicalName())).isTrue();
    }

    @Test
    public void isList() {
        assertThat(listValues).allMatch(ScenarioSimulationSharedUtils::isList);
        assertThat(mapValues).noneMatch(ScenarioSimulationSharedUtils::isList);
        assertThat(ScenarioSimulationSharedUtils.isList(Collection.class.getCanonicalName())).isFalse();
    }

    @Test
    public void isMap() {
        assertThat(mapValues).allMatch(ScenarioSimulationSharedUtils::isMap);
    }

    @Test
    public void isEnumCanonicalName() {
        assertThat(ScenarioSimulationSharedUtils.isEnumCanonicalName(Enum.class.getCanonicalName())).isTrue();
        assertThat(ScenarioSimulationSharedUtils.isEnumCanonicalName(Enum.class.getSimpleName())).isFalse();
    }
}