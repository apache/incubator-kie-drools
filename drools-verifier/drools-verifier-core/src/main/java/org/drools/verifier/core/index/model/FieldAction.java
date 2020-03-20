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
package org.drools.verifier.core.index.model;

import java.util.ArrayList;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.keys.Key;
import org.drools.verifier.core.index.keys.Values;
import org.drools.verifier.core.index.matchers.FieldMatchers;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.util.PortablePreconditions;
import org.kie.soup.project.datamodel.oracle.DataType;

public class FieldAction
        extends Action {

    private static final KeyDefinition FIELD = KeyDefinition.newKeyDefinition().withId("field").build();
    private static final KeyDefinition FACT_TYPE__FIELD_NAME = KeyDefinition.newKeyDefinition().withId("factType.fieldName").build();

    private final Field field;
    private final DataType.DataTypes dataType;

    public FieldAction(final Field field,
                       final Column column,
                       final DataType.DataTypes dataType,
                       final Values values,
                       final AnalyzerConfiguration configuration) {
        super(column,
              ActionSuperType.FIELD_ACTION,
              values,
              configuration);

        this.field = PortablePreconditions.checkNotNull("field",
                                                        field);
        this.dataType = PortablePreconditions.checkNotNull("dataType",
                                                           dataType);
    }

    public static FieldMatchers field() {
        return new FieldMatchers(FIELD);
    }

    public Field getField() {
        return field;
    }

    public DataType.DataTypes getDataType() {
        return dataType;
    }

    public static KeyDefinition[] keyDefinitions() {
        final ArrayList<KeyDefinition> keyDefinitions = new ArrayList<>();
        for (final KeyDefinition key : Action.keyDefinitions()) {
            keyDefinitions.add(key);
        }

        keyDefinitions.add(FIELD);
        keyDefinitions.add(FACT_TYPE__FIELD_NAME);

        return keyDefinitions.toArray(new KeyDefinition[keyDefinitions.size()]);
    }

    @Override
    public Key[] keys() {
        final ArrayList<Key> keys = new ArrayList<>();

        for (final Key key : super.keys()) {
            keys.add(key);
        }

        keys.add(new Key(FIELD,
                         field));
        keys.add(new Key(FACT_TYPE__FIELD_NAME,
                         field.getFactType() + "." + field.getName()));

        return keys.toArray(new Key[keys.size()]);
    }
}
