/*
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
package org.kie.kogito.persistence.reporting.database;

import org.kie.kogito.persistence.reporting.database.sqlbuilders.Context;
import org.kie.kogito.persistence.reporting.model.Field;
import org.kie.kogito.persistence.reporting.model.JsonField;
import org.kie.kogito.persistence.reporting.model.Mapping;
import org.kie.kogito.persistence.reporting.model.MappingDefinition;
import org.kie.kogito.persistence.reporting.model.PartitionField;

public interface DatabaseManager<T, F extends Field, P extends PartitionField, J extends JsonField<T>, M extends Mapping<T, J>, D extends MappingDefinition<T, F, P, J, M>, C extends Context<T, F, P, J, M>> {

    C createContext(final D mappingDefinition);

    /**
     * Creates the database artifacts for a Mapping Definition.
     *
     * @param mappingDefinition The Mapping Definition for which database artifacts need to be created.
     */
    void createArtifacts(final D mappingDefinition);

    /**
     * Destroys the database artifacts for a Mapping Definition.
     *
     * @param mappingDefinition The Mapping Definition for which database artifacts need to be destroyed.
     */
    void destroyArtifacts(final D mappingDefinition);
}
