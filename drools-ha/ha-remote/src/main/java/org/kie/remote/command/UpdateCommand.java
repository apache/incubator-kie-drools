/*
 * Copyright 2019 Red Hat
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

package org.kie.remote.command;

import java.io.Serializable;

import org.kie.remote.RemoteFactHandle;

public class UpdateCommand extends WorkingMemoryActionCommand implements VisitableCommand, Serializable {

    private Serializable object;

    public UpdateCommand() {/*For serialization*/}

    public UpdateCommand(RemoteFactHandle factHandle, Object obj, String entryPoint) {
        super(factHandle,
              entryPoint);
        this.object = (Serializable) obj;
    }

    public Object getObject() {
        return object;
    }

    @Override
    public void accept(VisitorCommand visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean isPermittedForReplicas() {
        return true;
    }

    @Override
    public String toString() {
        return "Update of " + getFactHandle() + " from entry-point " + getEntryPoint();
    }
}
