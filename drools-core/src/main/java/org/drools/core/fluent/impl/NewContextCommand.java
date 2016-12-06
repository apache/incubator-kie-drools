/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.fluent.impl;

import org.drools.core.command.RequestContextImpl;
import org.drools.core.command.impl.ExecutableCommand;
import org.drools.core.command.impl.RegistryContext;
import org.kie.api.runtime.Context;

public class NewContextCommand<Void> implements ExecutableCommand<Void> {
    private String name;

    public NewContextCommand(String name) {
        this.name = name;
    }

    @Override
    public Void execute(Context context) {
        Context returned = ( (RegistryContext) context ).getContextManager().createContext( name );
        ((RequestContextImpl)context).setApplicationContext(returned);
        return null;
    }
}
