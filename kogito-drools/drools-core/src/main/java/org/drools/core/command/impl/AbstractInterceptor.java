/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.command.impl;

import org.drools.core.command.CommandService;
import org.drools.core.command.Interceptor;
import org.kie.api.command.Command;
import org.kie.internal.command.Context;

public abstract class AbstractInterceptor implements Interceptor {

    private CommandService next;

    public Context getContext() {
        return next.getContext();
    }

    public void setNext(CommandService commandService) {
        this.next = commandService;
    }

    public CommandService getNext() {
        return next;
    }

    protected <T> T executeNext(Command<T> command) {
        return next.execute(command);
    }

}
