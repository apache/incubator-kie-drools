/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.process.core.datatype.impl.type;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.jbpm.process.core.datatype.DataType;

/**
 * Representation of a string datatype.
 */
public class StringDataType implements DataType {

    private static final long serialVersionUID = 510l;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public boolean verifyDataType(final Object value) {
        if ( value instanceof String ) {
            return true;
        } else if ( value == null ) {
            return true;
        } else {
            return false;
        }
    }

    public Object readValue(String value) {
        return value;
    }

    public String writeValue(Object value) {
        return (String) value;
    }

    public String getStringType() {
        return "String";
    }
}
