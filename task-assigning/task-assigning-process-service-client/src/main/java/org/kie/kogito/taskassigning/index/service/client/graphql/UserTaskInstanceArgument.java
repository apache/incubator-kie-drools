/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.taskassigning.index.service.client.graphql;

import org.kie.kogito.taskassigning.index.service.client.graphql.date.DateArgument;
import org.kie.kogito.taskassigning.index.service.client.graphql.string.StringArgument;

public class UserTaskInstanceArgument extends ArgumentContainer {

    public static final String TYPE_ID = "UserTaskInstanceArgument";

    public enum Field {
        DESCRIPTION("description", StringArgument.class),
        NAME("name", StringArgument.class),
        PRIORITY("priority", StringArgument.class),
        PROCESS_ID("processId", StringArgument.class),
        ACTUAL_OWNER("actualOwner", StringArgument.class),
        COMPLETED("completed", DateArgument.class),
        STARTED("started", DateArgument.class),
        REFERENCE_NAME("referenceName", StringArgument.class),
        LAST_UPDATE("lastUpdate", DateArgument.class),
        STATE("state", StringArgument.class);

        /*
         * The following fields and the corresponding arguments, etc., will be added on-demand if needed.
         * Since current graphql client implementation might be changed in favor of the future data-index-client
         * Quarkus extension.
         * 
         * ID("id", IdArgument.class),
         * PROCESS_INSTANCE_ID("processInstanceId", IdArgument.class),
         * POTENTIAL_USERS("potentialUsers", StringArrayArgument.class)
         * POTENTIAL_GROUPS("potentialGroups", StringArrayArgument.class)
         * EXCLUDED_USERS("excludedUsers", StringArrayArgument.class)
         * ADMIN_GROUPS("adminGroups", StringArrayArgument.class)
         * ADMIN_USERS("adminUsers", StringArrayArgument.class)
         * 
         * AND("and", UserTaskInstanceArgument[].class)
         * OR("or", UserTaskInstanceArgument[].class)
         * NOT("not", UserTaskInstanceArgument[].class);
         */

        private final String name;
        private final Class<? extends Argument> baseType;

        Field(String name, Class<? extends Argument> baseType) {
            this.name = name;
            this.baseType = baseType;
        }

        public String getName() {
            return name;
        }

        public Class<? extends Argument> getBaseType() {
            return baseType;
        }
    }

    @Override
    public String getTypeId() {
        return TYPE_ID;
    }
}
