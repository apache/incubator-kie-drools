/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.time.impl;

import org.drools.core.time.JobHandle;

public abstract class AbstractJobHandle implements JobHandle {

    private JobHandle previous;
    private JobHandle next;

    @Override
    public JobHandle getPrevious() {
        return previous;
    }

    @Override
    public void setPrevious(JobHandle previous) {
        this.previous = previous;
    }

    @Override
    public void nullPrevNext() {
        previous = null;
        next = null;
    }

    @Override
    public void setNext(JobHandle next) {
        this.next = next;
    }

    @Override
    public JobHandle getNext() {
        return next;
    }
}
