/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.metric.phreak;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.TupleSets;
import org.drools.core.phreak.PhreakFromNode;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.metric.util.MetricLogUtils;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSink;

public class PhreakFromNodeMetric extends PhreakFromNode {

    @Override
    public void doNode(FromNode fromNode,
                       FromMemory fm,
                       LeftTupleSink sink,
                       InternalWorkingMemory wm,
                       TupleSets<LeftTuple> srcLeftTuples,
                       TupleSets<LeftTuple> trgLeftTuples,
                       TupleSets<LeftTuple> stagedLeftTuples) {

        try {
            MetricLogUtils.getInstance().startMetrics(fromNode);

            super.doNode(fromNode, fm, sink, wm, srcLeftTuples, trgLeftTuples, stagedLeftTuples);

        } finally {
            MetricLogUtils.getInstance().logAndEndMetrics();
        }
    }
}
