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
package org.drools.core.phreak;

public class PhreakNetworkNodeFactoryImpl implements PhreakNetworkNodeFactory {

    @Override
    public PhreakJoinNode createPhreakJoinNode() {
        return new PhreakJoinNode();
    }

    @Override
    public PhreakEvalNode createPhreakEvalNode() {
        return new PhreakEvalNode();
    }

    @Override
    public PhreakFromNode createPhreakFromNode() {
        return new PhreakFromNode();
    }

    @Override
    public PhreakReactiveFromNode createPhreakReactiveFromNode() {
        return new PhreakReactiveFromNode();
    }

    @Override
    public PhreakNotNode createPhreakNotNode() {
        return new PhreakNotNode();
    }

    @Override
    public PhreakExistsNode createPhreakExistsNode() {
        return new PhreakExistsNode();
    }

    @Override
    public PhreakAccumulateNode createPhreakAccumulateNode() {
        return new PhreakAccumulateNode();
    }

    @Override
    public PhreakGroupByNode createPhreakGroupByNode() {
        return new PhreakGroupByNode();
    }

    @Override
    public PhreakBranchNode createPhreakBranchNode() {
        return new PhreakBranchNode();
    }

    @Override
    public PhreakQueryNode createPhreakQueryNode() {
        return new PhreakQueryNode();
    }

    @Override
    public PhreakTimerNode createPhreakTimerNode() {
        return new PhreakTimerNode();
    }

    @Override
    public PhreakAsyncSendNode createPhreakAsyncSendNode() {
        return new PhreakAsyncSendNode();
    }

    @Override
    public PhreakAsyncReceiveNode createPhreakAsyncReceiveNode() {

        return new PhreakAsyncReceiveNode();
    }

    @Override
    public PhreakRuleTerminalNode createPhreakRuleTerminalNode() {
        return new PhreakRuleTerminalNode();
    }

    @Override
    public PhreakQueryTerminalNode createPhreakQueryTerminalNode() {
        return new PhreakQueryTerminalNode();
    }
}
