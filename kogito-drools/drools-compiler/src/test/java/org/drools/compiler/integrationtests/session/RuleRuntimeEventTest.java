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

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.mockito.ArgumentCaptor;

public class RuleRuntimeEventTest extends CommonTestMethodBase {

    @Test
    public void testEventModel() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_EventModel.drl"));
        final KieSession wm = createKnowledgeSession(kbase);

        final RuleRuntimeEventListener wmel = mock(RuleRuntimeEventListener.class);
        wm.addEventListener(wmel);

        final Cheese stilton = new Cheese("stilton", 15);

        final FactHandle stiltonHandle = wm.insert(stilton);

        final ArgumentCaptor<ObjectInsertedEvent> oic = ArgumentCaptor.forClass(org.kie.api.event.rule.ObjectInsertedEvent.class);
        verify(wmel).objectInserted(oic.capture());
        assertSame(stiltonHandle, oic.getValue().getFactHandle());

        wm.update(stiltonHandle, stilton);
        final ArgumentCaptor<org.kie.api.event.rule.ObjectUpdatedEvent> ouc = ArgumentCaptor.forClass(org.kie.api.event.rule.ObjectUpdatedEvent.class);
        verify(wmel).objectUpdated(ouc.capture());
        assertSame(stiltonHandle, ouc.getValue().getFactHandle());

        wm.delete(stiltonHandle);
        final ArgumentCaptor<ObjectDeletedEvent> orc = ArgumentCaptor.forClass(ObjectDeletedEvent.class);
        verify(wmel).objectDeleted(orc.capture());
        assertSame(stiltonHandle, orc.getValue().getFactHandle());

    }

}
