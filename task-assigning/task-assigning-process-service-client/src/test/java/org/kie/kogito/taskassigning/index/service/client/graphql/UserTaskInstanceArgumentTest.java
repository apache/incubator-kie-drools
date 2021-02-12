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

import static org.kie.kogito.taskassigning.TestUtil.parseZonedDateTime;
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

class UserTaskInstanceArgumentTest extends AbstractArgumentContainerTest<UserTaskInstanceArgument> {

    private static final StringArgument DESCRIPTION_ARGUMENT = ArgumentFactory.newStringEqual("descriptionValue");
    private static final StringArgument NAME_ARGUMENT = ArgumentFactory.newStringEqual("nameValue");
    private static final StringArgument PRIORITY_ARGUMENT = ArgumentFactory.newStringEqual("priorityValue");
    private static final StringArgument PROCESS_ID_ARGUMENT = ArgumentFactory.newStringEqual("processIdValue");
    private static final StringArgument ACTUAL_OWNER_ARGUMENT = ArgumentFactory.newStringEqual("actualOwnerValue");
    private static final DateArgument COMPLETED_ARGUMENT = ArgumentFactory.newDateEqual(parseZonedDateTime("2020-12-01T07:54:56.883Z"));
    private static final DateArgument STARTED_ARGUMENT = ArgumentFactory.newDateEqual(parseZonedDateTime("2020-12-02T07:54:56.883Z"));
    private static final StringArgument REFERENCE_NAME_ARGUMENT = ArgumentFactory.newStringEqual("referenceNameValue");
    private static final DateArgument LAST_UPDATE_ARGUMENT = ArgumentFactory.newDateEqual(parseZonedDateTime("2020-12-03T07:54:56.883Z"));
    private static final StringArgument STATE_ARGUMENT = ArgumentFactory.newStringEqual("stateValue");

    @Override
    protected UserTaskInstanceArgument createArgumentContainer() {
        return new UserTaskInstanceArgument();
    }

    @Override
    protected void setUpArguments(UserTaskInstanceArgument argumentContainer) {
        addArgument(DESCRIPTION.getName(), DESCRIPTION_ARGUMENT);
        addArgument(NAME.getName(), NAME_ARGUMENT);
        addArgument(PRIORITY.getName(), PRIORITY_ARGUMENT);
        addArgument(PROCESS_ID.getName(), PROCESS_ID_ARGUMENT);
        addArgument(ACTUAL_OWNER.getName(), ACTUAL_OWNER_ARGUMENT);
        addArgument(COMPLETED.getName(), COMPLETED_ARGUMENT);
        addArgument(STARTED.getName(), STARTED_ARGUMENT);
        addArgument(REFERENCE_NAME.getName(), REFERENCE_NAME_ARGUMENT);
        addArgument(LAST_UPDATE.getName(), LAST_UPDATE_ARGUMENT);
        addArgument(STATE.getName(), STATE_ARGUMENT);
    }

    @Override
    protected String expectedType() {
        return "UserTaskInstanceArgument";
    }
}
