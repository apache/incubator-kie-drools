/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.compiler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.model.v1_1.ItemDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ItemDefinitionDependenciesGeneratedTest {

    private final Logger logger = LoggerFactory.getLogger(ItemDefinitionDependenciesGeneratedTest.class);

    private static final int NUMBER_OF_BASE_ITEM_DEFINITIONS = 4;
    private static final String ITEM_DEFINITION_NAME_BASE = "ItemDefinition";

    private static final String TEST_NS = "https://www.drools.org/";

    @Parameterized.Parameter
    public static List<ItemDefinition> itemDefinitions;

    @Parameterized.Parameters
    public static Collection<List<ItemDefinition>> generateParameters() {
        final List<ItemDefinition> baseItemDefinitions = getBaseListOfItemDefinitions();

        final Collection<List<ItemDefinition>> permutations = new ArrayList<>();
        getAllPossiblePermutations(baseItemDefinitions, new ArrayList<>(), permutations);

        return generateItemDefinitionsWithDependencies(permutations, permutations);
    }

    private static void getAllPossiblePermutations(final List<ItemDefinition> itemDefinitions,
                                                   final List<ItemDefinition> head,
                                                   final Collection<List<ItemDefinition>> result) {
        if (itemDefinitions.size() == 1) {
            final List<ItemDefinition> resultList = new ArrayList<>(head);
            resultList.addAll(itemDefinitions);
            result.add(resultList);
        } else {
            for (ItemDefinition itemDefinition : itemDefinitions) {
                final List<ItemDefinition> newHead = new ArrayList<>(head);
                newHead.add(itemDefinition);
                final List<ItemDefinition> possibleDependencies =
                        itemDefinitions.stream().filter(item -> !newHead.contains(item)).collect(Collectors.toList());
                getAllPossiblePermutations(possibleDependencies, newHead, result);
            }
        }
    }

    private static Collection<List<ItemDefinition>> generateItemDefinitionsWithDependencies(final Collection<List<ItemDefinition>> itemDefinitionPermutations,
                                                                                            final Collection<List<ItemDefinition>> dependenciesPermutations) {
        final Collection<List<ItemDefinition>> result = new HashSet<>();
        itemDefinitionPermutations.forEach(itemDefinitions -> {
            // An ItemDefinition could have 1 or more transitive deps, so generate a test for these cases
            // E.g. maxDependencyLevel = 2, then a first processed itemDefinition gets 2 level of dependencies dependencies and the
            // processing gets to next itemDefinition which gets 2 levels of dependencies if possible, processing moves to next, etc.
            // so if maxDependencyLevel = 2, then
            //     ItemDefinition1 -> Dependency1 -> Dependency2
            //     ItemDefinition2 -> Dependency3 -> Dependency4
            for (int maxDependencyLevel = 1; maxDependencyLevel < NUMBER_OF_BASE_ITEM_DEFINITIONS; maxDependencyLevel++) {
                // Dependencies could be added not just right from the first ItemDefinition. So this
                // loop specifies from which ItemDefinition should be the deps added
                // (e.g. if j == 2, deps are added to third ItemDefinition at start)
                for (int addDependenciesFromItemIndex = 0; addDependenciesFromItemIndex < NUMBER_OF_BASE_ITEM_DEFINITIONS; addDependenciesFromItemIndex++) {
                    for (List<ItemDefinition> dependencies : dependenciesPermutations) {
                        result.add(generateItemDefinitionsWithDependencies(itemDefinitions, dependencies, maxDependencyLevel, addDependenciesFromItemIndex));
                    }
                }
            }
        });
        return result;
    }

    private static List<ItemDefinition> generateItemDefinitionsWithDependencies(final List<ItemDefinition> itemDefinitions,
                                                                                final List<ItemDefinition> dependencies,
                                                                                final int maxDependencyLevel,
                                                                                final int startWithItemIndex) {

        // Original ItemDefinition ordering must be preserved, so this head tail result split trick does the thing.
        final List<ItemDefinition> resultTail = new ArrayList<>();
        final Set<String> usedNamesSet = new HashSet<>();
        for (int i = startWithItemIndex; i < itemDefinitions.size(); i++) {
            resultTail.addAll(createItemDefinitionWithDeps(itemDefinitions.get(i), dependencies, maxDependencyLevel, usedNamesSet));
        }

        final List<ItemDefinition> resultHead = new ArrayList<>();
        for (int i = 0; i < startWithItemIndex; i++) {
            resultHead.addAll(createItemDefinitionWithDeps(itemDefinitions.get(i), dependencies, maxDependencyLevel, usedNamesSet));
        }
        resultHead.addAll(resultTail);
        return resultHead;
    }

    private static List<ItemDefinition> createItemDefinitionWithDeps(final ItemDefinition itemDefinitionTemplate,
                                                                     final List<ItemDefinition> dependencies,
                                                                     final int maxDependencyLevel,
                                                                     final Set<String> usedNames) {
        final List<ItemDefinition> result = new ArrayList<>();
        // New ItemDefinition is created, so the original one stays untouched.
        final ItemDefinition it = new ItemDefinition();
        it.setName(itemDefinitionTemplate.getName());
        final List<ItemDefinition> possibleDependencies =
                dependencies.stream().filter(item -> !item.getName().equals(it.getName())).collect(Collectors.toList());
        final List<ItemDefinition> addedDeps =
                addDepsToItemDefinition(it, possibleDependencies, maxDependencyLevel, usedNames);
        result.add(it);
        result.addAll(addedDeps);
        return result;
    }

    private static List<ItemDefinition> addDepsToItemDefinition(final ItemDefinition itemDefinition,
                                                final List<ItemDefinition> dependencies,
                                                final int maxDependencyLevel,
                                                final Set<String> usedNames) {
        final List<ItemDefinition> addedDependencies = new ArrayList<>();
        // For going to deeper dependency levels, we must remember the last added dependency
        ItemDefinition itemToWhichAddDependency = itemDefinition;
        for (ItemDefinition dependency : dependencies) {
            if (!usedNames.contains(dependency.getName())) {
                itemToWhichAddDependency = createAndAddDependency(itemToWhichAddDependency, dependency);
                addedDependencies.add(itemToWhichAddDependency);
                usedNames.add(dependency.getName());
                if (addedDependencies.size() == maxDependencyLevel) {
                    return addedDependencies;
                }
            }
        }
        return addedDependencies;
    }

    private static ItemDefinition createAndAddDependency(final ItemDefinition itemDefinition, final ItemDefinition dependency) {
        ItemDefinition newDependency = new ItemDefinition();
        newDependency.setName("_" + itemDefinition.getName() + "-" + dependency.getName());
        newDependency.setTypeRef(new QName(TEST_NS, dependency.getName()));
        itemDefinition.getItemComponent().add(newDependency);
        return newDependency;
    }

    private static List<ItemDefinition> getBaseListOfItemDefinitions() {
        final List<ItemDefinition> itemDefinitions = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_BASE_ITEM_DEFINITIONS; i++) {
            final ItemDefinition it = new ItemDefinition();
            it.setName(ITEM_DEFINITION_NAME_BASE + i);
            itemDefinitions.add(it);
        }
        return itemDefinitions;
    }

    @Test
    public void testOrdering() {
        logger.info("Item definitions:");
        itemDefinitions.forEach(itemDefinition -> logger.info(itemDefinition.getName()));
        final List<ItemDefinition> orderedList = new ItemDefinitionDependenciesSorter(TEST_NS).sort(itemDefinitions);

        for (ItemDefinition itemDefinition : itemDefinitions) {
            assertOrdering(itemDefinition, orderedList);
        }
    }

    private void assertOrdering(final ItemDefinition itemDefinition, final List<ItemDefinition> orderedList) {
        for (ItemDefinition dependency : itemDefinition.getItemComponent()) {
            assertTrue("Index of " + itemDefinition.getName() + " > " + dependency.getName(),
                       orderedList.indexOf(itemDefinition) < orderedList.indexOf(dependency));
            if (dependency.getItemComponent() != null && !dependency.getItemComponent().isEmpty()) {
                assertOrdering(dependency, orderedList);
            }
        }
    }
}
