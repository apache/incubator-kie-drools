/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
import java.util.UUID;

public class FactCountCommand extends AbstractCommand implements VisitableCommand,
                                                                            Serializable {

    private String entryPoint;

    public FactCountCommand() { /*For serialization*/}

    public FactCountCommand(String entryPoint) {
        super(UUID.randomUUID().toString());
        this.entryPoint = entryPoint;
    }

    public String getEntryPoint() {
        return entryPoint;
    }

    @Override
    public void accept(VisitorCommand visitor) { visitor.visit(this); }

    @Override
    public boolean isPermittedForReplicas() { return false; }

    @Override
    public String toString() {
        return "Fact count of " + getId() + " from entry-point " + getEntryPoint();
    }

}

