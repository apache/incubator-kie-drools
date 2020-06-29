/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.phreak;

import org.drools.core.phreak.metric.PhreakAccumulateNodeMetric;
import org.drools.core.phreak.metric.PhreakAsyncReceiveNodeMetric;
import org.drools.core.phreak.metric.PhreakAsyncSendNodeMetric;
import org.drools.core.phreak.metric.PhreakBranchNodeMetric;
import org.drools.core.phreak.metric.PhreakEvalNodeMetric;
import org.drools.core.phreak.metric.PhreakExistsNodeMetric;
import org.drools.core.phreak.metric.PhreakFromNodeMetric;
import org.drools.core.phreak.metric.PhreakJoinNodeMetric;
import org.drools.core.phreak.metric.PhreakNotNodeMetric;
import org.drools.core.phreak.metric.PhreakQueryNodeMetric;
import org.drools.core.phreak.metric.PhreakQueryTerminalNodeMetric;
import org.drools.core.phreak.metric.PhreakReactiveFromNodeMetric;
import org.drools.core.phreak.metric.PhreakTimerNodeMetric;
import org.drools.core.util.PerfLogUtils;

public class PhreakNetworkNodeFactory {

    private static final PhreakNetworkNodeFactory INSTANCE = new PhreakNetworkNodeFactory();

    static PhreakNetworkNodeFactory getInstance() {
        return INSTANCE;
    }

    private PhreakNetworkNodeFactory() {}

    PhreakJoinNode createPhreakJoinNode() {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new PhreakJoinNodeMetric();
        } else {
            return new PhreakJoinNode();
        }
    }

    PhreakEvalNode createPhreakEvalNode() {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new PhreakEvalNodeMetric();
        } else {
            return new PhreakEvalNode();
        }
    }

    PhreakFromNode createPhreakFromNode() {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new PhreakFromNodeMetric();
        } else {
            return new PhreakFromNode();
        }
    }

    PhreakReactiveFromNode createPhreakReactiveFromNode() {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new PhreakReactiveFromNodeMetric();
        } else {
            return new PhreakReactiveFromNode();
        }
    }

    PhreakNotNode createPhreakNotNode() {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new PhreakNotNodeMetric();
        } else {
            return new PhreakNotNode();
        }
    }

    PhreakExistsNode createPhreakExistsNode() {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new PhreakExistsNodeMetric();
        } else {
            return new PhreakExistsNode();
        }
    }

    PhreakAccumulateNode createPhreakAccumulateNode() {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new PhreakAccumulateNodeMetric();
        } else {
            return new PhreakAccumulateNode();
        }
    }

    PhreakBranchNode createPhreakBranchNode() {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new PhreakBranchNodeMetric();
        } else {
            return new PhreakBranchNode();
        }
    }

    PhreakQueryNode createPhreakQueryNode() {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new PhreakQueryNodeMetric();
        } else {
            return new PhreakQueryNode();
        }
    }

    PhreakTimerNode createPhreakTimerNode() {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new PhreakTimerNodeMetric();
        } else {
            return new PhreakTimerNode();
        }
    }

    PhreakAsyncSendNode createPhreakAsyncSendNode() {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new PhreakAsyncSendNodeMetric();
        } else {
            return new PhreakAsyncSendNode();
        }
    }

    PhreakAsyncReceiveNode createPhreakAsyncReceiveNode() {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new PhreakAsyncReceiveNodeMetric();
        } else {
            return new PhreakAsyncReceiveNode();
        }
    }

    PhreakRuleTerminalNode createPhreakRuleTerminalNode() {
        return new PhreakRuleTerminalNode(); // TerminalNode is not BaseNode so cannot use PerfLogUtils
    }

    PhreakQueryTerminalNode createPhreakQueryTerminalNode() {
        if (PerfLogUtils.getInstance().isEnabled()) {
            return new PhreakQueryTerminalNodeMetric();
        } else {
            return new PhreakQueryTerminalNode();
        }
    }
}
