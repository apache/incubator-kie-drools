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
import org.drools.core.phreak.PhreakAsyncSendNode;
import org.drools.core.reteoo.AsyncSendNode;
import org.drools.core.reteoo.AsyncSendNode.AsyncSendMemory;
import org.drools.metric.util.MetricLogUtils;
import org.drools.core.reteoo.LeftTuple;

public class PhreakAsyncSendNodeMetric extends PhreakAsyncSendNode {

    @Override
    public void doNode(AsyncSendNode node,
                       AsyncSendMemory memory,
                       InternalWorkingMemory wm,
                       TupleSets<LeftTuple> srcLeftTuples) {

        try {
            MetricLogUtils.getInstance().startMetrics(node);

            super.doNode(node, memory, wm, srcLeftTuples);

        } finally {
            MetricLogUtils.getInstance().logAndEndMetrics();
        }
    }
}
