/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.metric.phreak;

import org.drools.core.phreak.PhreakAccumulateNode;
import org.drools.core.phreak.PhreakAsyncReceiveNode;
import org.drools.core.phreak.PhreakAsyncSendNode;
import org.drools.core.phreak.PhreakBranchNode;
import org.drools.core.phreak.PhreakEvalNode;
import org.drools.core.phreak.PhreakExistsNode;
import org.drools.core.phreak.PhreakFromNode;
import org.drools.core.phreak.PhreakGroupByNode;
import org.drools.core.phreak.PhreakJoinNode;
import org.drools.core.phreak.PhreakNetworkNodeFactory;
import org.drools.core.phreak.PhreakNotNode;
import org.drools.core.phreak.PhreakQueryNode;
import org.drools.core.phreak.PhreakQueryTerminalNode;
import org.drools.core.phreak.PhreakReactiveFromNode;
import org.drools.core.phreak.PhreakRuleTerminalNode;
import org.drools.core.phreak.PhreakTimerNode;
import org.drools.metric.util.MetricLogUtils;

public class MetricPhreakNetworkNodeFactoryImpl implements PhreakNetworkNodeFactory {

    @Override
    public PhreakJoinNode createPhreakJoinNode() {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new PhreakJoinNodeMetric();
        } else {
            return new PhreakJoinNode();
        }
    }

    @Override
    public PhreakEvalNode createPhreakEvalNode() {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new PhreakEvalNodeMetric();
        } else {
            return new PhreakEvalNode();
        }
    }

    @Override
    public PhreakFromNode createPhreakFromNode() {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new PhreakFromNodeMetric();
        } else {
            return new PhreakFromNode();
        }
    }

    @Override
    public PhreakReactiveFromNode createPhreakReactiveFromNode() {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new PhreakReactiveFromNodeMetric();
        } else {
            return new PhreakReactiveFromNode();
        }
    }

    @Override
    public PhreakNotNode createPhreakNotNode() {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new PhreakNotNodeMetric();
        } else {
            return new PhreakNotNode();
        }
    }

    @Override
    public PhreakExistsNode createPhreakExistsNode() {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new PhreakExistsNodeMetric();
        } else {
            return new PhreakExistsNode();
        }
    }

    @Override
    public PhreakAccumulateNode createPhreakAccumulateNode() {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new PhreakAccumulateNodeMetric();
        } else {
            return new PhreakAccumulateNode();
        }
    }

    @Override
    public PhreakGroupByNode createPhreakGroupByNode() {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new PhreakGroupByNodeMetric();
        } else {
            return new PhreakGroupByNode();
        }
    }

    @Override
    public PhreakBranchNode createPhreakBranchNode() {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new PhreakBranchNodeMetric();
        } else {
            return new PhreakBranchNode();
        }
    }

    @Override
    public PhreakQueryNode createPhreakQueryNode() {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new PhreakQueryNodeMetric();
        } else {
            return new PhreakQueryNode();
        }
    }

    @Override
    public PhreakTimerNode createPhreakTimerNode() {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new PhreakTimerNodeMetric();
        } else {
            return new PhreakTimerNode();
        }
    }

    @Override
    public PhreakAsyncSendNode createPhreakAsyncSendNode() {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new PhreakAsyncSendNodeMetric();
        } else {
            return new PhreakAsyncSendNode();
        }
    }

    @Override
    public PhreakAsyncReceiveNode createPhreakAsyncReceiveNode() {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new PhreakAsyncReceiveNodeMetric();
        } else {
            return new PhreakAsyncReceiveNode();
        }
    }

    @Override
    public PhreakRuleTerminalNode createPhreakRuleTerminalNode() {
        return new PhreakRuleTerminalNode(); // TerminalNode is not BaseNode so cannot use PerfLogUtils
    }

    @Override
    public PhreakQueryTerminalNode createPhreakQueryTerminalNode() {
        if (MetricLogUtils.getInstance().isEnabled()) {
            return new PhreakQueryTerminalNodeMetric();
        } else {
            return new PhreakQueryTerminalNode();
        }
    }
}
