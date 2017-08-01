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

package org.jbpm.test.jobexec;

import org.jbpm.executor.impl.wih.AsyncWorkItemHandlerCmdCallback;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

public class UserCommandCallback extends AsyncWorkItemHandlerCmdCallback {

    @Override
    public void onCommandDone(CommandContext ctx, ExecutionResults results) {
        System.out.println("[INFO] Command done");
        signal(ctx, results.getData("UserOut"));
    }

    @Override
    public void onCommandError(CommandContext ctx, Throwable exception) {
        System.out.println("[INFO] Command error");
        signal(ctx, null);
    }

    private void signal(CommandContext ctx, Object user) {
        RuntimeManager manager = getRuntimeManager(ctx);
        RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get((Long) ctx.getData("processInstanceId")));

        try {
            engine.getKieSession().signalEvent("Continue", user);
        } finally {
            manager.disposeRuntimeEngine(engine);
        }
    }

}
