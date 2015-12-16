/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.drools.core.reteoo;

import org.drools.core.spi.Tuple;

public interface RightTuple extends Tuple {

    RightTuple getHandlePrevious();
    RightTuple getHandleNext();

    RightTupleSink getTupleSink();

    LeftTuple getBlocked();
    void setBlocked( LeftTuple leftTuple );
    void addBlocked( LeftTuple leftTuple );
    void removeBlocked( LeftTuple leftTuple );

    LeftTuple getTempBlocked();
    void setTempBlocked( LeftTuple tempBlocked );

    RightTuple getTempNextRightTuple();
    void setTempNextRightTuple( RightTuple tempNextRightTuple );

    TupleMemory getTempRightTupleMemory();
    void setTempRightTupleMemory( TupleMemory tempRightTupleMemory );

    RightTuple getStagedPrevious();
    RightTuple getStagedNext();
}