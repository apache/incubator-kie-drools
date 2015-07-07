/*
 * Copyright 2015 JBoss Inc
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

package org.jbpm.test.jobexec;

import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.executor.api.Command;
import org.kie.internal.executor.api.CommandContext;
import org.kie.internal.executor.api.ExecutionResults;

public class UserCommandWithCallback implements Command {

    public ExecutionResults execute(CommandContext ctx) {
        System.out.println("[INFO] Command executed on executor with " + ctx.getData());

        WorkItem workItem = (WorkItem) ctx.getData("workItem");
        User user = (User) workItem.getParameter("UserIn");
        user.setName(user.getName() + " after command execution");
        ExecutionResults executionResults = new ExecutionResults();
        executionResults.setData("UserOut", user);

        String callbacks = (String) ctx.getData("callbacks");
        ctx.setData("callbacks", callbacks + ", org.jbpm.test.jobexec.UserCommandCallback");

        double item = 0;
        for (int i = 0; i < 99; i++) {
            System.out.println("[INFO] User item:" + item);
            item++;
        }
        return executionResults;
    }

}
