/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.testscenarios.backend.populators;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.drools.core.base.TypeResolver;
import org.drools.workbench.models.testscenarios.shared.FactAssignmentField;
import org.drools.workbench.models.testscenarios.shared.Field;

public class FactAssignmentFieldPopulator
        extends FieldPopulator {

    private final Object fact;
    private final Collection<FieldPopulator> subFieldPopulators = new ArrayList<FieldPopulator>();

    public FactAssignmentFieldPopulator(Object factObject,
                                        FactAssignmentField field,
                                        TypeResolver resolver)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
        super(factObject,
                field.getName());
        this.fact = resolver.resolveType(resolver.getFullTypeName(field.getFact().getType())).newInstance();

        initSubFieldPopulators(field,
                resolver);
    }

    private void initSubFieldPopulators(FactAssignmentField field,
                                        TypeResolver resolver)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        FieldPopulatorFactory fieldPopulatorFactory = new FieldPopulatorFactory(fact,
                resolver);
        for (Field subField : field.getFact().getFieldData()) {
            try {
                subFieldPopulators.add(fieldPopulatorFactory.getFieldPopulator(subField));
            } catch (IllegalArgumentException e) {
                // This should never happen, but I don't trust myself or the legacy test scenarios we have.
                // If the field value is null then it is safe to ignore it.
            }
        }
    }

    @Override
    public void populate(Map<String, Object> populatedData) {
        populateField(fact,
                populatedData);
        for (FieldPopulator fieldPopulator : subFieldPopulators) {
            fieldPopulator.populate(populatedData);
        }
    }
}
