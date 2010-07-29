/**
 * Copyright 2005 JBoss Inc
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

package org.drools.process.core.datatype.impl.type;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.process.core.datatype.DataType;

/**
 * Representation of an undefined datatype.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public final class UndefinedDataType implements DataType {

    private static final long serialVersionUID = 510l;
    private static UndefinedDataType instance;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public static UndefinedDataType getInstance() {
        if (instance == null) {
            instance = new UndefinedDataType();
        }
        return instance;
    }

    public boolean verifyDataType(final Object value) {
        if (value == null) {
            return true;
        }
        return false;
    }

	public Object readValue(String value) {
		throw new IllegalArgumentException("Undefined datatype");
	}

	public String writeValue(Object value) {
		throw new IllegalArgumentException("Undefined datatype");
	}

	public String getStringType() {
		return "Object";
	}
}
