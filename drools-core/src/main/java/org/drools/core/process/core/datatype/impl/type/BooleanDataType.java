/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.process.core.datatype.impl.type;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.process.core.datatype.DataType;

/**
 * Representation of a boolean datatype.
 */
public final class BooleanDataType
    implements
    DataType {

    private static final long serialVersionUID = 510l;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public boolean verifyDataType(final Object value) {
        if ( value instanceof Boolean ) {
            return true;
        }
        return false;
    }

    public Object readValue(String value) {
        return new Boolean(value);
    }

    public String writeValue(Object value) {
        return (Boolean) value ? "true" : "false";
    }

    public String getStringType() {
        return "Boolean";
    }
}
