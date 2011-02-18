/*
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

package org.drools.process.core.datatype;

import java.io.Externalizable;

/**
 * Abstract representation of a datatype.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface DataType extends Externalizable {

    /**
     * Returns true if the given value is a valid value of this data type.
     */
    boolean verifyDataType(Object value);
    
    String writeValue(Object value);
    
    Object readValue(String value);
    
    /**
     * Returns the corresponding Java type of this datatype
     */
    String getStringType();

}
