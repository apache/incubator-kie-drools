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

package org.drools.core.reteoo;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.spi.PropagationContext;

public interface RightTupleSink extends Sink {

    void retractRightTuple(final RightTuple rightTuple,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory);
    
    void modifyRightTuple(final RightTuple rightTuple,
                          final PropagationContext context,
                          final InternalWorkingMemory workingMemory);

    ObjectTypeNode.Id getRightInputOtnId();
}
