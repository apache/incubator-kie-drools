/*
 * Copyright 2011 JBoss Inc
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
import java.util.List;
import java.util.Map;

import org.drools.core.base.TypeResolver;
import org.drools.workbench.models.testscenarios.shared.FactData;
import org.drools.workbench.models.testscenarios.shared.Field;

abstract class FactPopulatorBase
        implements
        Populator {

    protected final Map<String, Object> populatedData;
    protected final TypeResolver typeResolver;
    protected final FactData fact;

    public FactPopulatorBase(Map<String, Object> populatedData,
                             TypeResolver typeResolver,
                             FactData fact) throws ClassNotFoundException {
        this.populatedData = populatedData;
        this.typeResolver = typeResolver;
        this.fact = fact;
    }

    public String getName() {
        return fact.getName();
    }

    protected List<FieldPopulator> getFieldPopulators(Object factObject)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {

        FieldPopulatorFactory fieldPopulatorFactory = new FieldPopulatorFactory(factObject,
                typeResolver);

        List<FieldPopulator> fieldPopulators = new ArrayList<FieldPopulator>();
        for (Field field : fact.getFieldData()) {
            try {
                fieldPopulators.add(fieldPopulatorFactory.getFieldPopulator(field));
            } catch (IllegalArgumentException e) {
                // This should never happen, but I don't trust myself or the legacy test scenarios we have.
                // If the field value is null then it is safe to ignore it.
            }
        }

        return fieldPopulators;
    }

    protected String getTypeName(TypeResolver resolver,
                                 FactData fact) throws ClassNotFoundException {

        String fullName = resolver.getFullTypeName(fact.getType());
        if (fullName.equals("java.util.List") || fullName.equals("java.util.Collection")) {
            return "java.util.ArrayList";
        } else {
            return fullName;
        }
    }
}
