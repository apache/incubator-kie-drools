/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

/**
 * Shared global status
 */
public class GlobalStatus {

    private static volatile boolean nodeReady = false;
    private static volatile boolean nodeLive = true;
    private static volatile boolean canBecomeLeader = true;

    private GlobalStatus() {
    }

    public static boolean isNodeReady() {
        return nodeReady;
    }

    public static void setNodeReady(boolean nodeReady) {
        GlobalStatus.nodeReady = nodeReady;
    }

    public static boolean isNodeLive() {
        return nodeLive;
    }

    public static void setNodeLive(boolean nodeLive) {
        GlobalStatus.nodeLive = nodeLive;
    }

    public static boolean canBecomeLeader() {
        return canBecomeLeader;
    }

    public static void setCanBecomeLeader(boolean canBecomeLeader) {
        GlobalStatus.canBecomeLeader = canBecomeLeader;
    }
}