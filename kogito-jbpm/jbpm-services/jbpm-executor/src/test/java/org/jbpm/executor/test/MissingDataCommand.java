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

package org.jbpm.executor.test;

import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.runtime.process.WorkItem;


public class MissingDataCommand implements Command {

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        Integer amount = getAmount(ctx);
        if (amount == null) {
            throw new IllegalArgumentException("Missing amount data");
        }
        
        if (amount < 200) {
            throw new IllegalArgumentException("Ammount is too low");
        }
        return new ExecutionResults();
    }

    protected Integer getAmount(CommandContext ctx) {
        Integer amount = (Integer) ctx.getData("amount");
        
        if (amount == null) {
            WorkItem workItem = (WorkItem) ctx.getData("workItem");
            if (workItem != null) {
                amount = (Integer) workItem.getParameter("amount");
            }
        }
        
        return amount;
    }
}
