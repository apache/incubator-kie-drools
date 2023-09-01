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
package org.drools.verifier.core.index.model;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.util.PortablePreconditions;

public class Field
        extends FieldBase {

    private final ObjectField objectField;
    private final Conditions conditions = new Conditions();
    private final Actions actions = new Actions();

    public Field(final ObjectField objectField,
                 final String factType,
                 final String fieldType,
                 final String name,
                 final AnalyzerConfiguration configuration) {
        super(factType,
              fieldType,
              name,
              configuration);
        this.objectField = PortablePreconditions.checkNotNull("objectField",
                                                              objectField);
    }

    public ObjectField getObjectField() {
        return objectField;
    }

    public Conditions getConditions() {
        return conditions;
    }

    public Actions getActions() {
        return actions;
    }

    public void remove(final Column column) {
        this.conditions.remove(column);
        this.actions.remove(column);
    }
}
