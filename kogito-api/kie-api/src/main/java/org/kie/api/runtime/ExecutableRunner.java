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

package org.kie.api.runtime;

import org.kie.api.command.Command;

public interface ExecutableRunner<C extends Context> extends CommandExecutor {
    C execute( Executable executable );

    C execute( Executable executable, C ctx );

    <T> T execute( Command<T> command, Context ctx );

    C createContext();

    static ExecutableRunner<RequestContext> create() {
        try {
            return (ExecutableRunner) Class.forName( "org.drools.core.fluent.impl.PseudoClockRunner" ).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to instance ExecutableRunner", e);
        }
    }

    static ExecutableRunner<RequestContext> create(long startTime) {
        try {
            return (ExecutableRunner) Class.forName( "org.drools.core.fluent.impl.PseudoClockRunner" ).getConstructor( long.class ).newInstance(startTime);
        } catch (Exception e) {
            throw new RuntimeException("Unable to instance ExecutableRunner", e);
        }
    }
}
