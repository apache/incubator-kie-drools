/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bpms.functional.jobexec;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;

public class UserCommand implements Command {

    public ExecutionResults execute(CommandContext ctx) {
        System.out.println("[INFO] Command executed on executor with " + ctx.getData());
        
        WorkItem workItem = (WorkItem) ctx.getData("workItem");
        User user = (User) workItem.getParameter("UserIn");
        user.setName(user.getName() + " after command execution");
        ExecutionResults executionResults = new ExecutionResults();
        executionResults.setData("UserOut", user);

        double item=0;
        for(int i=0;i<99;i++){
            System.out.println("[INFO] User item:" + item);
            item++;
        }
        return executionResults;
    }

}
