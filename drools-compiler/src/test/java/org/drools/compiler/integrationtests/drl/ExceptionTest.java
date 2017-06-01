/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests.drl;

import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class ExceptionTest extends CommonTestMethodBase {

    @Test
    public void testReturnValueException() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_ReturnValueException.drl"));
        final KieSession ksession = kbase.newKieSession();

        final Cheese brie = new Cheese("brie", 12);
        try {
            ksession.insert(brie);
            ksession.fireAllRules();
            fail("Should throw an Exception from the ReturnValue");
        } catch (final Exception e) {
            Throwable root = e;
            while (root.getCause() != null) {
                root = root.getCause();
            }
            root.getMessage().contains("this should throw an exception");
        }
    }

}
