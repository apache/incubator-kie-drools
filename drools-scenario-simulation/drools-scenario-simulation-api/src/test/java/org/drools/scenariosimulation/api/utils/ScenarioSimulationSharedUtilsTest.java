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