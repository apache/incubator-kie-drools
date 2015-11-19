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

package org.drools.core.phreak;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.TupleSets;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;
import org.drools.core.reteoo.ReactiveFromNode;
import org.drools.core.reteoo.ReactiveFromNode.ReactiveFromMemory;

public class PhreakReactiveFromNode extends PhreakFromNode {
    public void doNode(ReactiveFromNode fromNode,
                       ReactiveFromMemory fm,
                       LeftTupleSink sink,
                       InternalWorkingMemory wm,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples,
                       TupleSets<LeftTuple> stagedLeftTuples) {

        super.doNode(fromNode, fm, sink, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);
        trgLeftTuples.addAll(fm.getStagedLeftTuples().takeAll());
    }
}
