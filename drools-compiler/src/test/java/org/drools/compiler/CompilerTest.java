/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler;

import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.fail;

public class CompilerTest {

    @Test
    public void test() throws Exception {
        String drl =
                "rule R when\n" +
                "    $s: String()" +
                "then\n" +
                "end";

        try {
            new KieHelper().addContent( drl, ResourceType.DRL ).build();
            fail("trying to build without drools-mvel on classpath should throw an exception");
        } catch (RuntimeException e) {
            // expected
        }
    }
}
