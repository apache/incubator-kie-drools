/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.commands.impl;

import org.drools.commands.ChainableRunner;
import org.drools.commands.fluent.PseudoClockRunner;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.Executable;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.RequestContext;

public abstract class AbstractInterceptor extends PseudoClockRunner implements ChainableRunner {

    private ExecutableRunner next;

    public void setNext(ExecutableRunner runner) {
        this.next = runner;
    }

    public ExecutableRunner getNext() {
        return next;
    }

    protected void executeNext( Executable executable, Context ctx ) {
        next.execute(executable, ctx);
    }
}
