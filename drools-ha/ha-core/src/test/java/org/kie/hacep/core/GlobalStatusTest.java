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
package org.kie.hacep.core;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class GlobalStatusTest {

    @Test
    public void becomeLeaderTest() {
        assertTrue(GlobalStatus.canBecomeLeader());
        GlobalStatus.setCanBecomeLeader(false);
        assertFalse(GlobalStatus.canBecomeLeader());
    }

    @Test
    public void nodeLiveTest() {
        if(GlobalStatus.isNodeLive()) {
            assertTrue(GlobalStatus.isNodeLive());
            GlobalStatus.setNodeLive(false);
            assertFalse(GlobalStatus.isNodeLive());
        }else{
            assertFalse(GlobalStatus.isNodeLive());
            GlobalStatus.setNodeLive(true);
            assertTrue(GlobalStatus.isNodeLive());
        }
    }

    @Test
    public void nodeReadyTest() {
        if(GlobalStatus.isNodeReady()){
            assertTrue(GlobalStatus.isNodeReady());
            GlobalStatus.setNodeReady(false);
            assertFalse(GlobalStatus.isNodeReady());
        }else{
            assertFalse(GlobalStatus.isNodeReady());
            GlobalStatus.setNodeReady(true);
            assertTrue(GlobalStatus.isNodeReady());
        }
    }
}
