/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.openapi;

import org.junit.Test;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;

public class DMNOASTest {

    @Test
    public void test() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("NSEW.dmn",
                                                                                       this.getClass(),
                                                                                       "myOrder.dmn");
        DMNOASGeneratorFactory.generator(runtime.getModels()).build();
        System.err.println("end.");
    }
}
