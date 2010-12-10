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
import java.util.List;

import org.drools.process.core.TypeObject;
import org.drools.process.core.datatype.DataType;

/**
 * Representation of a list datatype.
 * All elements in the list must have the same datatype.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class ListDataType extends ObjectDataType implements TypeObject {

    private static final long serialVersionUID = 510l;

    private DataType dataType;
    
    public ListDataType() {
    	setClassName("java.util.List");
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        dataType    = (DataType)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(dataType);
    }
    
    public ListDataType(DataType dataType) {
    	setType(dataType);
    }

    public void setType(final DataType dataType) {
        this.dataType = dataType;
    }

    public DataType getType() {
        return this.dataType;
    }

    public boolean verifyDataType(final Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof List) {
            for (Object o: (List<?>) value) {
                if (dataType != null && !dataType.verifyDataType(o)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
