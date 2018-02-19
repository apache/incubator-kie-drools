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

    private static final int NUMBER_OF_BASE_ITEM_DEFINITIONS = 10;
    private static final String ITEM_DEFINITION_NAME_BASE = "ItemDefinition";

    private static final String TEST_NS = "https://www.drools.org/";

    @Parameterized.Parameter
    public static List<ItemDefinition> itemDefinitions;

    @Parameterized.Parameters
    public static Collection<List<ItemDefinition>> generateItemDefinitionCollections() {
        final ItemDefinition[] baseItemDefinitions = getBaseListOfItemDefinitions();

        final Collection<List<ItemDefinition>> result = new ArrayList<>();

        // Iterate base definitions, each iteration defining new first item
        for (int i = 0; i < baseItemDefinitions.length; i++) {
            final List<ItemDefinition> itemDefinitions = getItemDefinitionsFromIndex(baseItemDefinitions, i);
            for (int j = 1; j < NUMBER_OF_BASE_ITEM_DEFINITIONS; j++) {
                result.add(getItemDefinitionsWithDeps(itemDefinitions, j));
            }
        }

        return result;
    }

    private static List<ItemDefinition> getItemDefinitionsFromIndex(final ItemDefinition[] itemDefinitions, final int beginIndex) {
        final List<ItemDefinition> result = new ArrayList<>();
        for (int i = beginIndex; i < itemDefinitions.length; i++) {
            result.add(itemDefinitions[i]);
        }

        for (int i = 0; i < beginIndex; i++) {
            result.add(itemDefinitions[i]);
        }
        return result;
    }

    private static List<ItemDefinition> getItemDefinitionsWithDeps(final List<ItemDefinition> itemDefinitions,
                                                                   final int maxNumberOfDepsPerItemDefinition) {
        final List<ItemDefinition> result = new ArrayList<>();
        final Set<String> usedNamesSet = new HashSet<>();
        for (ItemDefinition itemDefinition : itemDefinitions) {
            // New ItemDefinition is created, so the original one stays untouched.
            final ItemDefinition it = new ItemDefinition();
            it.setName(itemDefinition.getName());
            final List<ItemDefinition> possibleDependencies =
                    itemDefinitions.stream().filter(item -> !item.getName().equals(it.getName())).collect(Collectors.toList());
            final List<ItemDefinition> addedDeps =
                    addDepsToItemDefinition(it, possibleDependencies, maxNumberOfDepsPerItemDefinition, usedNamesSet);
            result.add(it);
            result.addAll(addedDeps);
        }
        return result;
    }

    private static List<ItemDefinition> addDepsToItemDefinition(final ItemDefinition itemDefinition,
                                                final List<ItemDefinition> possibleDependencies,
                                                final int numberOfDeps,
                                                final Set<String> alreadyUsedDependencyNames) {
        final List<ItemDefinition> addedDependencies = new ArrayList<>();
        for (ItemDefinition dependency : possibleDependencies) {
            if (!alreadyUsedDependencyNames.contains(dependency.getName())) {
                addedDependencies.add(createAndAddDependency(itemDefinition, dependency));
                alreadyUsedDependencyNames.add(dependency.getName());
                if (addedDependencies.size() == numberOfDeps) {
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

    private static ItemDefinition[] getBaseListOfItemDefinitions() {
        final ItemDefinition[] itemDefinitions = new ItemDefinition[NUMBER_OF_BASE_ITEM_DEFINITIONS];
        for (int i = 0; i < NUMBER_OF_BASE_ITEM_DEFINITIONS; i++) {
            final ItemDefinition it = new ItemDefinition();
            it.setName(ITEM_DEFINITION_NAME_BASE + i);
            itemDefinitions[i] = it;
        }
        return itemDefinitions;
    }
    
    private List<ItemDefinition> orderingStrategy(List<ItemDefinition> ins) {
        return new ItemDefinitionDependenciesSorter(TEST_NS).sort(ins);
    }

    @Test
    public void testOrdering() {
        logger.info("Item definitions:");
        itemDefinitions.forEach(itemDefinition -> logger.info(itemDefinition.getName()));
        List<ItemDefinition> orderedList = orderingStrategy(itemDefinitions);

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
