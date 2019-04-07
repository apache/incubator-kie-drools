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

import java.util.HashMap;
import java.util.Map;

import org.drools.core.command.runtime.process.StartProcessCommand;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.listener.IterableProcessEventListener;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.jbpm.test.tools.IterableListenerAssert.*;

/**
 * Inclusive gateway tests. combination of diverging OR gateway with converging XOR gateway
 *
 * Converging OR gateway is not supported!
 *
 * Converging XOR does not behave according to documentation: bz803692
 */
public class InclusiveGatewayTest extends JbpmTestCase {

    private static final String INCLUSIVE_GATEWAY = "org/jbpm/test/functional/gateway/InclusiveGateway.bpmn";
    private static final String INCLUSIVE_GATEWAY_ID = "org.jbpm.test.functional.gateway.InclusiveGateway";

    private KieSession kieSession;
    private IterableProcessEventListener iterableListener;

    public InclusiveGatewayTest() {
        super(false);
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        kieSession = createKSession(INCLUSIVE_GATEWAY);
        iterableListener = new IterableProcessEventListener();
    }

    /**
     * Inclusive diverging gateway & exclusive converging. Two of three conditions are satisfied, XOR gateway is
     * excepted to be triggered 2x (doc. 5.6.2)
     */
    @Test(timeout = 30000)
    public void testInclusive() {
        kieSession.addEventListener(iterableListener);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", 15);

        StartProcessCommand spc = new StartProcessCommand();
        spc.setProcessId(INCLUSIVE_GATEWAY_ID);
        spc.setParameters(params);
        kieSession.execute(spc);

        assertChangedVariable(iterableListener, "x", null, 15);
        assertProcessStarted(iterableListener, INCLUSIVE_GATEWAY_ID);
        assertNextNode(iterableListener, "start");
        assertNextNode(iterableListener, "fork");
        assertNextNode(iterableListener, "script1");
        assertNextNode(iterableListener, "join");
        assertNextNode(iterableListener, "finalScript");
        assertNextNode(iterableListener, "end");
        assertLeft(iterableListener, "fork");
        assertNextNode(iterableListener, "script2");
        assertNextNode(iterableListener, "join");
        assertNextNode(iterableListener, "finalScript");
        assertNextNode(iterableListener, "end");
        assertProcessCompleted(iterableListener, INCLUSIVE_GATEWAY_ID);
    }

}
