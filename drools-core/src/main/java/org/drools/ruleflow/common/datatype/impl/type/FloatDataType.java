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

import org.drools.ruleflow.common.datatype.IDataType;

/**
 * Representation of a float datatype.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public final class FloatDataType
    implements
    IDataType {

    private static final long serialVersionUID = 3257008760991069747L;

    public boolean verifyDataType(final Object value) {
        if ( value instanceof Float ) {
            return true;
        } else if ( value == null ) {
            return true;
        } else {
            return false;
        }
    }
}
