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

import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstanceArgument.Field.ACTUAL_OWNER;
import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstanceArgument.Field.COMPLETED;
import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstanceArgument.Field.DESCRIPTION;
import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstanceArgument.Field.LAST_UPDATE;
import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstanceArgument.Field.NAME;
import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstanceArgument.Field.PRIORITY;
import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstanceArgument.Field.PROCESS_ID;
import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstanceArgument.Field.REFERENCE_NAME;
import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstanceArgument.Field.STARTED;
import static org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstanceArgument.Field.STATE;

class UserTaskInstanceOrderByTest extends AbstractArgumentContainerTest<UserTaskInstanceOrderBy> {

    @Override
    protected UserTaskInstanceOrderBy createArgumentContainer() {
        return new UserTaskInstanceOrderBy();
    }

    @Override
    protected void setUpArguments(UserTaskInstanceOrderBy argumentContainer) {
        addArgument(STATE.getName(), OrderBy.ASC);
        addArgument(ACTUAL_OWNER.getName(), OrderBy.ASC);
        addArgument(DESCRIPTION.getName(), OrderBy.ASC);
        addArgument(NAME.getName(), OrderBy.ASC);
        addArgument(PRIORITY.getName(), OrderBy.ASC);
        addArgument(PROCESS_ID.getName(), OrderBy.ASC);
        addArgument(COMPLETED.getName(), OrderBy.ASC);
        addArgument(STARTED.getName(), OrderBy.ASC);
        addArgument(REFERENCE_NAME.getName(), OrderBy.ASC);
        addArgument(LAST_UPDATE.getName(), OrderBy.ASC);
    }

    @Override
    protected String expectedType() {
        return "UserTaskInstanceOrderBy";
    }
}
