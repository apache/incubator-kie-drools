/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.impact.analysis.model.right;

public class ModifiedProperty {
    protected final String property;
    protected final Object value;

    public ModifiedProperty( String property ) {
        this(property, null);
    }

    public ModifiedProperty( String property, Object value ) {
        this.property = property;
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ModifiedProperty{" +
                "property='" + property + '\'' +
                ", value=" + value +
                '}';
    }
}
