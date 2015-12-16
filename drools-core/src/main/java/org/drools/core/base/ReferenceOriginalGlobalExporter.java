/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.base;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.WorkingMemory;
import org.drools.core.spi.GlobalExporter;
import org.drools.core.spi.GlobalResolver;

/**
 * This implementation does nothing other than pass by reference the original GlobalResolver as used in the StatelessSession.
 * Care should be taken if you use this strategy, as later executes may change those globals. The GlobalResolver of the StatelessSession
 * may also not be serialisable friendly.
 */
public class ReferenceOriginalGlobalExporter implements GlobalExporter {
    public GlobalResolver export(WorkingMemory workingMemory) {
        return workingMemory.getGlobalResolver();
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

}
