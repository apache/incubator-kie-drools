/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package com.myspace.demo;

import java.util.List;

import org.drools.core.WorkItemNotFoundException;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.impl.Sig;

public class $Type$Resource {

    Process<$Type$> process;

    @POST
    @Path("/{id}/$signalPath$")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public $Type$Output signal(@PathParam("id") final String id, final $signalType$ data) {
        return UnitOfWorkExecutor.executeInUnitOfWork(application.unitOfWorkManager(), () -> {
            return process.instances().findById(id).map(pi -> {
                pi.send(Sig.of("$signalName$", data));
                return pi.checkError().variables().toOutput();
            }).orElseThrow(() -> new NotFoundException());
        });
    }
}
