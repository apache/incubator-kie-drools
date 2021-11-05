/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.tracing.typedvalue;

import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonSubTypes({
        @JsonSubTypes.Type(value = UnitValue.class, name = "UNIT"),
        @JsonSubTypes.Type(value = CollectionValue.class, name = "COLLECTION"),
        @JsonSubTypes.Type(value = StructureValue.class, name = "STRUCTURE")
})
public abstract class TypedValue extends BaseTypedValue<CollectionValue, StructureValue, UnitValue> {

    public TypedValue() {
    }

    public TypedValue(Kind kind, String type) {
        super(kind, type);
    }
}