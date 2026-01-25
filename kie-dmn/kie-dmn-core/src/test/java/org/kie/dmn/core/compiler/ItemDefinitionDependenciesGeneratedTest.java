/*
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
package org.kie.dmn.core.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.DMNVersion;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.v1_6.TItemDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemDefinitionDependenciesGeneratedTest {

    private final Logger logger = LoggerFactory.getLogger(ItemDefinitionDependenciesGeneratedTest.class);

    private static final int NUMBER_OF_BASE_ITEM_DEFINITIONS = 5;
    private static final int LEVELS_OF_DEPENDENCIES = 3;
    private static final String ITEM_DEFINITION_NAME_BASE = "ItemDefinition";
    private static final String TEST_NS = "https://www.drools.org/";
    public static List<ItemDefinition> itemDefinitions;

    public static Collection<List<ItemDefinition>> generateParameters() {
        final List<ItemDefinition> baseItemDefinitions = getBaseListOfItemDefinitions(1);

        final Collection<List<ItemDefinition>> permutations = new ArrayList<>();
        getPermutations(baseItemDefinitions, new ArrayList<>(), permutations);

        return generateItemDefinitionsWithDependencies(permutations);
    }

    private static void getPermutations(final List<ItemDefinition> itemDefinitions,
                                        final List<ItemDefinition> head,
                                        final Collection<List<ItemDefinition>> result) {
        if (itemDefinitions.size() == 1) {
            final List<ItemDefinition> resultList = new ArrayList<>(head);
            resultList.addAll(itemDefinitions);
            result.add(resultList);
        } else {
            for (final ItemDefinition itemDefinition : itemDefinitions) {
                final List<ItemDefinition> newHead = new ArrayList<>(head);
                newHead.add(itemDefinition);
                final List<ItemDefinition> possibleDependencies =
                        itemDefinitions.stream().filter(item -> !newHead.contains(item)).collect(Collectors.toList());
                getPermutations(possibleDependencies, newHead, result);
            }
        }
    }

    private static Collection<List<ItemDefinition>> generateItemDefinitionsWithDependencies(final Collection<List<ItemDefinition>> itemDefinitionPermutations) {
        final Collection<List<ItemDefinition>> result = new ArrayList<>();
        itemDefinitionPermutations.forEach(itemDefinitions -> {
            // An ItemDefinition could have 1 or more deps, so generate a test for these cases
            // E.g. numberOfDependencies = 2, then a first processed itemDefinition gets 2 dependencies and the
            // processing gets to next itemDefinition which gets 2 dependencies if possible, processing moves to next, etc.
            for (int numberOfDependencies = 1; numberOfDependencies < NUMBER_OF_BASE_ITEM_DEFINITIONS; numberOfDependencies++) {
                // Dependencies could be added not just right from the first ItemDefinition. So this
                // loop specifies from which ItemDefinition should be the deps added
                // (e.g. if j == 2, deps are added to third ItemDefinition at start)
                for (int addDependenciesFromItemIndex = 0; addDependenciesFromItemIndex < NUMBER_OF_BASE_ITEM_DEFINITIONS; addDependenciesFromItemIndex++) {
                    result.add(generateItemDefinitionsWithDependencies(itemDefinitions,
                                                                       getBaseListOfItemDefinitions(10),
                                                                       numberOfDependencies,
                                                                       addDependenciesFromItemIndex,
                                                                       LEVELS_OF_DEPENDENCIES));
                }
            }
        });
        return result;
    }

    private static List<ItemDefinition> generateItemDefinitionsWithDependencies(final List<ItemDefinition> itemDefinitions,
                                                                                final List<ItemDefinition> dependencies,
                                                                                final int maxNumberOfDepsPerItemDefinition,
                                                                                final int startWithItemIndex,
                                                                                final int levelsOfDependencies) {

        // Original ItemDefinition ordering must be preserved, so this head tail result split trick does the thing.
        final List<ItemDefinition> resultTail = new ArrayList<>();
        final Set<String> usedNames = new HashSet<>();
        for (int i = startWithItemIndex; i < itemDefinitions.size(); i++) {
            resultTail.add(createItemDefinitionWithDeps(itemDefinitions.get(i), dependencies, maxNumberOfDepsPerItemDefinition, usedNames));
        }

        final List<ItemDefinition> resultHead = new ArrayList<>();
        for (int i = 0; i < startWithItemIndex; i++) {
            resultHead.add(createItemDefinitionWithDeps(itemDefinitions.get(i), dependencies, maxNumberOfDepsPerItemDefinition, usedNames));
        }
        resultHead.addAll(resultTail);
        if (levelsOfDependencies > 1) {
            resultHead.addAll(generateItemDefinitionsWithDependencies(dependencies,
                                                                      getBaseListOfItemDefinitions(levelsOfDependencies * 100),
                                                                      maxNumberOfDepsPerItemDefinition,
                                                                      startWithItemIndex,
                                                                      levelsOfDependencies - 1));
        } else {
            resultHead.addAll(dependencies);
        }
        return resultHead;
    }

    private static ItemDefinition createItemDefinitionWithDeps(final ItemDefinition itemDefinitionTemplate,
                                                                     final List<ItemDefinition> dependencies,
                                                                     final int maxNumberOfDepsPerItemDefinition,
                                                                     final Set<String> usedNames) {
        // New ItemDefinition is created, so the original one stays untouched.
        final ItemDefinition it = new TItemDefinition();
        it.setName(itemDefinitionTemplate.getName());
        final List<ItemDefinition> possibleDependencies =
                dependencies.stream().filter(item -> !item.getName().equals(it.getName())).collect(Collectors.toList());
        addDepsToItemDefinition(it, possibleDependencies, maxNumberOfDepsPerItemDefinition, usedNames);
        return it;
    }

    private static void addDepsToItemDefinition(final ItemDefinition itemDefinition,
                                                final List<ItemDefinition> dependencies,
                                                final int numberOfDeps,
                                                final Set<String> usedNames) {
        int addedDepsCount = 0;
        for (final ItemDefinition dependency : dependencies) {
            if (!usedNames.contains(dependency.getName())) {
                createAndAddDependency(itemDefinition, dependency);
                usedNames.add(dependency.getName());
                addedDepsCount++;
                if (addedDepsCount == numberOfDeps) {
                    return;
                }
            }
        }
    }

    private static void createAndAddDependency(final ItemDefinition itemDefinition, final ItemDefinition dependency) {
        final ItemDefinition newDependency = new TItemDefinition();
        newDependency.setName("_" + itemDefinition.getName() + "-" + dependency.getName());
        newDependency.setTypeRef(new QName(TEST_NS, dependency.getName()));
        itemDefinition.getItemComponent().add(newDependency);
    }

    private static List<ItemDefinition> getBaseListOfItemDefinitions(final int nameIndexFrom) {
        final List<ItemDefinition> itemDefinitions = new ArrayList<>();
        for (int i = nameIndexFrom; i < NUMBER_OF_BASE_ITEM_DEFINITIONS + nameIndexFrom; i++) {
            final ItemDefinition it = new TItemDefinition();
            it.setName(ITEM_DEFINITION_NAME_BASE + i);
            itemDefinitions.add(it);
        }
        return itemDefinitions;
    }

    private List<ItemDefinition> orderingStrategy(final List<ItemDefinition> ins) {
        return new ItemDefinitionDependenciesSorter(TEST_NS).sort(ins, DMNVersion.getLatest());
    }

    @MethodSource("generateParameters")
    @ParameterizedTest
    public void ordering(List<ItemDefinition> itemDefinitions) {
        initItemDefinitionDependenciesGeneratedTest(itemDefinitions);
        logger.trace("Item definitions:");
        itemDefinitions.forEach(itemDefinition -> {
            logger.trace(itemDefinition.getName());
            itemDefinition.getItemComponent().forEach(dependency -> logger.trace(dependency.getName()));
        });
        final List<ItemDefinition> orderedList = orderingStrategy(itemDefinitions);

        for (final ItemDefinition itemDefinition : itemDefinitions) {
            assertOrdering(itemDefinition, orderedList);
        }
    }

    private void assertOrdering(final ItemDefinition itemDefinition, final List<ItemDefinition> orderedList) {
        for (final ItemDefinition dependency : itemDefinition.getItemComponent()) {
            final String dependencyName = dependency.getTypeRef().getLocalPart();
            final int indexOfDependency = indexOfItemDefinitionByName(dependencyName, orderedList);
            assertThat(indexOfDependency > -1).as("Cannot find dependency " + dependencyName + " in the ordered list!").isTrue();
            assertThat(orderedList.indexOf(itemDefinition) > indexOfDependency).as("Index of " + itemDefinition.getName() + " < " + dependency.getTypeRef().getLocalPart()).isTrue();
            if (dependency.getItemComponent() != null && !dependency.getItemComponent().isEmpty()) {
                assertOrdering(dependency, orderedList);
            }
        }
    }

    private int indexOfItemDefinitionByName(final String name, final List<ItemDefinition> itemDefinitions) {
        int index = 0;
        for (final ItemDefinition itemDefinition : itemDefinitions) {
            if (itemDefinition.getName().equals(name)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    public void initItemDefinitionDependenciesGeneratedTest(List<ItemDefinition> itemDefinitions) {
        this.itemDefinitions = itemDefinitions;
    }
}
