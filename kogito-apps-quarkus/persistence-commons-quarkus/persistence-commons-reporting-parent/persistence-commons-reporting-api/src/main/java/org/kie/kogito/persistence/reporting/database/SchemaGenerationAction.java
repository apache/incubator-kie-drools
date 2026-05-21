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

import java.util.Arrays;

/**
 * An enumerated encapsulation of JPA's Schema Generation text values
 * See https://jakarta.ee/specifications/persistence/3.0/jakarta-persistence-spec-3.0.html#a12917
 */
public enum SchemaGenerationAction {

    //No schema creation or deletion will take place.
    NONE("none"),
    //The provider will create the database artifacts on application deployment. The artifacts will remain unchanged after application redeployment.
    CREATE("create"),
    //Any artifacts in the database will be deleted, and the provider will create the database artifacts on deployment.
    DROP_AND_CREATE("drop-and-create"),
    //Any artifacts in the database will be deleted on application deployment.
    DROP("drop");

    private final String action;

    SchemaGenerationAction(final String action) {
        this.action = action;
    }

    public String getActionString() {
        return action;
    }

    public static SchemaGenerationAction fromString(final String action) {
        return Arrays
                .stream(SchemaGenerationAction
                        .values())
                .filter(s -> s.action.equalsIgnoreCase(action))
                .findFirst()
                .orElse(SchemaGenerationAction.NONE);
    }
}
