/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.shared.services.impl.commands;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;

public class CompositeCommand implements ExecutableCommand<Object[]> {

    private static final long serialVersionUID = 6368668523569392532L;
    private ExecutableCommand<?>[] commands;   

    public CompositeCommand(ExecutableCommand<?>... commands) {
        super();
        this.commands = commands;
    }

    @Override
    public Object[] execute(Context context) {
        Object[] result = new Object[commands.length];
        
        int counter = 0;
        
        for (ExecutableCommand<?> command : commands) {
            result[counter++] = command.execute(context);
        }
        
        return result;
    }

}
