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
package org.kie.hacep.util;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;

import static org.junit.Assert.*;

public class GavUtilsTest {

    @Test
    public void getSplittedGavTest() {
        String[] parts = GAVUtils.getSplittedGav("org.kie:fake:1.0.0.Snapshot");
        assertEquals("org.kie", parts[0]);
        assertEquals("fake", parts[1]);
        assertEquals("1.0.0.Snapshot", parts[2]);
    }

    @Test
    public void getReleaseIdTest() {
        KieServices sv = KieServices.get();
        ReleaseId releaseID = GAVUtils.getReleaseID("org.kie:fake:1.0.0.Snapshot", sv);
        assertEquals("org.kie", releaseID.getGroupId());
        assertEquals("fake", releaseID.getArtifactId());
        assertEquals("1.0.0.Snapshot", releaseID.getVersion());
    }
}
