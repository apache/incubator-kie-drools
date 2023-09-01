/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.base.facttemplates;

import java.io.Externalizable;
import java.util.Collection;

import org.drools.base.definitions.InternalKnowledgePackage;


public interface FactTemplate
    extends
    Externalizable {

    InternalKnowledgePackage getPackage();

    /**
     * The name of the template may be the fully qualified
     * class name, or an alias.
     * @return
     */
    String getName();

    /**
     * templates may have 1 or more slots. A slot is a named
     * pattern with a specific type of value.
     * @return
     */
    int getNumberOfFields();

    Collection<String> getFieldNames();

    /**
     * Return the slot with the String name
     * @return
     */
    FieldTemplate getFieldTemplate(String name);

    /**
     * Get the pattern index with the given name
     * @param name
     * @return
     */
    int getFieldTemplateIndex(String name);

    Fact createFact();
}
