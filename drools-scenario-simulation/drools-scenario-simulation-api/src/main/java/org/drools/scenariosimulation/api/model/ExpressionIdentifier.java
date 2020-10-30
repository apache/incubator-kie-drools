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
package org.drools.scenariosimulation.api.model;

import java.util.Objects;

/**
 * Identify an expression. It is defined by a name and a type
 */
public class ExpressionIdentifier {

    private String name;
    private FactMappingType type;

    public enum NAME {
        Description,
        Expected,
        Given,
        Index,
        Other;
    }

    public static final ExpressionIdentifier INDEX = create(NAME.Index.name(), FactMappingType.OTHER);
    public static final ExpressionIdentifier DESCRIPTION = create(NAME.Description.name(), FactMappingType.OTHER);

    public ExpressionIdentifier() {
    }

    public ExpressionIdentifier(String name, FactMappingType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public FactMappingType getType() {
        return type;
    }

    public static ExpressionIdentifier create(String name, FactMappingType type) {
        return new ExpressionIdentifier(name, type);
    }

    @Override
    public String toString() {
        return "ExpressionIdentifier{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExpressionIdentifier that = (ExpressionIdentifier) o;
        return Objects.equals(name, that.name) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
