/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.solver.testutil;

import java.util.concurrent.ThreadFactory;

public class MockThreadFactory implements ThreadFactory {

    private static boolean called;

    public static boolean hasBeenCalled() {
        return called;
    }

    public MockThreadFactory() {
        called = false;
    }

    @Override
    public Thread newThread(Runnable r) {
        called = true;
        Thread newThread = new Thread(r, "testing thread");
        newThread.setDaemon(false);
        return newThread;
    }
}