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
package org.kie.hacep;

import org.junit.Test;

import static org.junit.Assert.*;

public class EnvConfigTest {

    @Test
    public void defaultConfigEnvTest() {
        EnvConfig config = EnvConfig.getDefaultEnvConfig();
        assertEquals("default", config.getNamespace());
        assertEquals("control", config.getControlTopicName());
        assertEquals("events", config.getEventsTopicName());
        assertEquals("kiesessioninfos", config.getKieSessionInfosTopicName());
        assertEquals("snapshot", config.getSnapshotTopicName());
        assertEquals(PollUnit.MILLISECOND, config.getPollUnit());
        assertEquals(PollUnit.SECOND, config.getPollSnapshotUnit());
        assertTrue(1 == config.getPollSnapshotTimeout());
    }

    @Test(expected = IllegalArgumentException.class)
    public void pollTimeUnitExceptionTest() {
        EnvConfig.anEnvConfig().withPollTimeUnit(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void pollSnapshotTimeUnitExceptionTest() {
        EnvConfig.anEnvConfig().withPollSnapshotTimeUnit(null);
    }

    @Test
    public void pollSnapshotTimeUnitMillisTest() {
        EnvConfig config = EnvConfig.anEnvConfig().withPollSnapshotTimeUnit(EnvConfig.MILLISEC);
        assertEquals(PollUnit.MILLISECOND,
                     config.getPollSnapshotUnit());
    }
}
