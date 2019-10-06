/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.dmn;

import java.io.InputStreamReader;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNRuntime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DMNKogitoTest {

    @Test
    public void testBasic() {
        DMNRuntime dmnRuntime = DMNKogito.createGenericDMNRuntime(new InputStreamReader(DMNKogitoTest.class.getResourceAsStream("TrafficViolation.dmn")));
        assertEquals(dmnRuntime.getModels().size(), 1);
    }
}
