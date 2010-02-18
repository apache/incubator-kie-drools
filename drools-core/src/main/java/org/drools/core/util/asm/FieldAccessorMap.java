package org.drools.core.util.asm;

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

import java.util.Map;

/**
 * This class holds a dynamically generated instance of a FieldAccessor, 
 * and a map of the field names to index numbers that are used to access the fields. 
 * @deprecated use ClassFiledExtractor instead.
 * @author Michael Neale
 */
public class FieldAccessorMap {

    private final FieldAccessor accessor;
    private final Map           nameMap;

    /**
     * @param accessor
     * @param fieldAccessMethods Will be used to calculate the "field name"
     * which is really like bean property names.
     */
    FieldAccessorMap(final FieldAccessor accessor,
                     final Map nameMap) {
        this.accessor = accessor;
        this.nameMap = nameMap;
    }

    /**
     * @return A map of field names, to their index value, for use by the accessor. 
     */
    public Map getFieldNameMap() {
        return this.nameMap;
    }

    /**
     * @return The field index accessor itself.
     */
    public FieldAccessor getFieldAccessor() {
        return this.accessor;
    }

    public int getIndex(final String name) {
        return ((Integer) this.nameMap.get( name )).intValue();
    }
}