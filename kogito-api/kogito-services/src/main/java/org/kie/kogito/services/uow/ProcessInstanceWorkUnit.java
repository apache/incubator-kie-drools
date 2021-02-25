/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.services.uow;

import java.util.function.Consumer;

import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.uow.WorkUnit;

public class ProcessInstanceWorkUnit<T> implements WorkUnit<ProcessInstance<T>> {

    private ProcessInstance<T> data;
    private Consumer<Object> action;
    private Consumer<Object> compensation;

    public ProcessInstanceWorkUnit(ProcessInstance<T> data, Consumer<Object> action) {
        this.data = data;
        this.action = action;
    }

    public ProcessInstanceWorkUnit(ProcessInstance<T> data, Consumer<Object> action, Consumer<Object> compensation) {
        this.data = data;
        this.action = action;
        this.compensation = compensation;
    }

    @Override
    public ProcessInstance<T> data() {
        return data;
    }

    @Override
    public void perform() {
        action.accept(data());
    }

    @Override
    public void abort() {
        if (compensation != null) {
            compensation.accept(data());
        }
    }

    @Override
    public Integer priority() {
        return 10;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((data == null) ? 0 : data.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProcessInstanceWorkUnit<T> other = (ProcessInstanceWorkUnit) obj;
        if (data == null) {
            if (other.data != null)
                return false;
        } else if (!data.equals(other.data))
            return false;
        return true;
    }

}
