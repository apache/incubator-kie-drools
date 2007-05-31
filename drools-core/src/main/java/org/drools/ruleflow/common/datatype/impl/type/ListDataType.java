package org.drools.ruleflow.common.datatype.impl.type;

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

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.drools.ruleflow.common.datatype.DataType;

/**
 * Representation of a list datatype.
 * All elements in the list must have the same datatype.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class ListDataType
    implements
    DataType,
    Serializable {

    private static final long serialVersionUID = 3689069551774415161L;

    private DataType         dataType;

    public void setDataType(final DataType dataType) {
        this.dataType = dataType;
    }

    public DataType getDataType() {
        return this.dataType;
    }

    public boolean verifyDataType(final Object value) {
        if ( value == null ) {
            return true;
        }
        if ( value instanceof List ) {
            for ( final Iterator it = ((List) value).iterator(); it.hasNext(); ) {
                if ( !this.dataType.verifyDataType( it.next() ) ) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
