/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.models.drooled.ast;

/**
 * Class representing data needed to declare type.
 * For the moment being, only one field is managed, whose name - by default -is <b>"value"</b>
 */
public class AbstractKiePMMLDrooledConstraint {

    private final String identifier;
    private final Object left;
    private final String operator;
    private final Object right;

    public AbstractKiePMMLDrooledConstraint(String identifier, Object left, String operator, Object right) {
        this.identifier = identifier;
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
}
