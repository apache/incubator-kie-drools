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
package org.drools.commands.runtime;

import org.drools.commands.SingleSessionCommandService;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutableRunner;

public class DestroySessionCommand extends DisposeCommand {

    private ExecutableRunner runner;

    public DestroySessionCommand() {

    }

    public DestroySessionCommand(ExecutableRunner runner ) {
        this.runner = runner;
    }

    public Void execute(Context context) {
        if (runner != null && runner instanceof SingleSessionCommandService) {
           ((SingleSessionCommandService) runner).destroy();
        }
        super.execute(context);
        return null;
    }

    public String toString() {
        return "Destroy session command";
    }
}
