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
 * Representation of an integer datatype.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class IntegerDataType
    implements
    DataType {

    private static final long serialVersionUID = 400L;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public boolean verifyDataType(final Object value) {
        if ( value instanceof Integer ) {
            return true;
        } else if ( value == null ) {
            return true;
        } else {
            return false;
        }
    }

	public Object readValue(String value) {
		return new Integer(value);
	}

	public String writeValue(Object value) {
		Integer i = (Integer) value;
		return i == null ? "" : i.toString();
	}

	public String getStringType() {
		return "Integer";
	}

}
