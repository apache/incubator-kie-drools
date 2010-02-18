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

import java.io.Serializable;

/**
 * This provides "field" access to getters on a given class.
 * Implementations are generated into byte code (using a switchtable) 
 * when a new class is encountered.
 * @deprecated use ClassFieldExtractor instead
 * @author Michael Neale
 * @author "Jeff Brown" <brown_j@ociweb.com>
 */
public interface FieldAccessor
    extends
    Serializable {

    /**
     * Returns the "field" corresponding to the order in which it is in the object (class).
     * 
     * @param obj The object for the field to be extracted from.
     * @param idx The index of the "field". Refer to FieldAccessorMap to get the mapping
     * of the names of the "fields" to the index value to use for fast lookup.
     * 
     * @return Appropriate return type. Primitives are boxed to the corresponding type.
     */
    public Object getFieldByIndex(Object obj,
                                  int idx);

}