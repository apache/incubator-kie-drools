/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.incubation.processes.services;

import org.junit.jupiter.api.Test;
import org.kie.kogito.incubation.common.*;
import org.kie.kogito.incubation.processes.LocalProcessId;
import org.kie.kogito.incubation.processes.ProcessInstanceId;
import org.kie.kogito.incubation.processes.TaskId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProcessServiceTypeTest {
    public static class MyDataContext implements DataContext, DefaultCastable {
        int someParam;
    }

    @Test
    public void straightThroughProcesses() {

        // let's just make the compiler happy
        StraightThroughProcessService svc = new StraightThroughProcessService() {
            @Override
            public DataContext evaluate(Id id, DataContext ctx) {
                return ctx;
            }
        };
        MapDataContext ctx = MapDataContext.create(); // suppose there is a Map-like structure
                                                      // (it could be even just Map)
        LocalProcessId someProcessId = new LocalProcessId("some.process");

        // set a context using a Map-like interface
        ctx.set("someParam", 1);

        // evaluate the process
        DataContext result =
                svc.evaluate(someProcessId, ctx);

        // bind the data in the result to a typed bean
        MyDataContext mdc = result.as(MyDataContext.class);

        MapLikeDataContext map = mdc.as(MapLikeDataContext.class);

        assertEquals(1, mdc.someParam); // get the typed value from the POJO
        assertEquals(1, map.get("someParam")); // get the object value from map-like

        assertEquals("/processes/some.process",
                someProcessId.toLocalId().asLocalUri().path());

    }

    @Test
    public void statefulProcesses() {
        StatefulProcessService svc = new StatefulProcessService() {

            @Override
            public ExtendedDataContext signal(LocalId processId, DataContext dataContext) {
                return ExtendedDataContext.ofData(EmptyDataContext.Instance);
            }

            @Override
            public ExtendedDataContext create(LocalId processId, DataContext dataContext) {
                return ExtendedDataContext.ofData(EmptyDataContext.Instance);
            }

            @Override
            public ExtendedDataContext update(LocalId processId, DataContext dataContext) {
                return ExtendedDataContext.ofData(EmptyDataContext.Instance);
            }

            @Override
            public ExtendedDataContext abort(LocalId processId) {
                return ExtendedDataContext.ofData(EmptyDataContext.Instance);
            }

            @Override
            public ExtendedDataContext get(LocalId processId) {
                return ExtendedDataContext.ofData(EmptyDataContext.Instance);
            }
        };

        MapDataContext ctx = MapDataContext.create(); // suppose there is a Map-like structure
        // (it could be even just Map)
        LocalProcessId someProcessId = new LocalProcessId("some.process");

        // set a context using a Map-like interface
        ctx.set("someParam", 1);

        // evaluate the process
        DataContext result =
                svc.create(someProcessId, ctx);

        ProcessInstanceId processInstanceId = someProcessId.instances().get("some.instance.id");

        assertEquals("/processes/some.process/instances/some.instance.id",
                processInstanceId.toLocalId().asLocalUri().path());

        TaskId taskId = processInstanceId.tasks().get("some.task.id");

        assertEquals("/processes/some.process/instances/some.instance.id/tasks/some.task.id",
                taskId.toLocalId().asLocalUri().path());

    }
}
