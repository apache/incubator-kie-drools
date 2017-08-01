/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.functional.gateway;

import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.listener.TrackingProcessEventListener;
import org.jbpm.test.tools.TrackingListenerAssert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;

/**
 * Parallel gateway execution test. 2x parallel fork, 1x join
 */
public class ParallelGatewayTest extends JbpmTestCase {

    private static final String PARALLEL_GATEWAY = "org/jbpm/test/functional/gateway/ParallelGateway.bpmn";
    private static final String PARALLEL_GATEWAY_ID = "org.jbpm.test.functional.gateway.ParallelGateway";

    private KieSession kieSession;
    private TrackingProcessEventListener trackingListener;

    public ParallelGatewayTest() {
        super(false);
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        kieSession = createKSession(PARALLEL_GATEWAY);
        trackingListener = new TrackingProcessEventListener();
    }

    /**
     * Simple parallel gateway test.
     */
    @Test(timeout = 30000)
    public void testParallel() {
        kieSession.addEventListener(trackingListener);
        kieSession.execute((Command<?>) getCommands().newStartProcess(PARALLEL_GATEWAY_ID));

        TrackingListenerAssert.assertProcessStarted(trackingListener, PARALLEL_GATEWAY_ID);
        TrackingListenerAssert.assertTriggeredAndLeft(trackingListener, "start");

        TrackingListenerAssert.assertTriggered(trackingListener, "fork1", 1);
        TrackingListenerAssert.assertLeft(trackingListener, "fork1", 2);

        TrackingListenerAssert.assertTriggeredAndLeft(trackingListener, "script1");

        TrackingListenerAssert.assertTriggered(trackingListener, "fork2");
        TrackingListenerAssert.assertLeft(trackingListener, "fork2", 2);

        TrackingListenerAssert.assertTriggeredAndLeft(trackingListener, "script2");

        TrackingListenerAssert.assertTriggered(trackingListener, "join", 3);
        TrackingListenerAssert.assertLeft(trackingListener, "join", 1);

        TrackingListenerAssert.assertTriggered(trackingListener, "end");
        TrackingListenerAssert.assertProcessCompleted(trackingListener, PARALLEL_GATEWAY_ID);
    }

}
