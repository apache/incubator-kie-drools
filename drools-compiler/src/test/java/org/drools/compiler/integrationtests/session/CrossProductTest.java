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

package org.drools.compiler.integrationtests.session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.SpecialString;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

public class CrossProductTest extends CommonTestMethodBase {

    @Test
    public void testCrossProductRemovingIdentityEquals() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_CrossProductRemovingIdentityEquals.drl"));
        final KieSession session = createKnowledgeSession(kbase);

        final List list1 = new ArrayList();
        session.setGlobal("list1", list1);

        final SpecialString first42 = new SpecialString("42");
        final SpecialString second43 = new SpecialString("43");
        final SpecialString world = new SpecialString("World");
        session.insert(world);
        session.insert(first42);
        session.insert(second43);

        session.fireAllRules();

        assertEquals(6, list1.size());

        final List list2 = Arrays.asList("42:43", "43:42", "World:42", "42:World", "World:43", "43:World");
        Collections.sort(list1);
        Collections.sort(list2);
        assertEquals(list2, list1);
    }

}
