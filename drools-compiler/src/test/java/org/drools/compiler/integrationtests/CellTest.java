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

package org.drools.compiler.integrationtests;

import org.drools.compiler.Cell;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.marshalling.impl.ClassObjectMarshallingStrategyAcceptor;
import org.drools.core.marshalling.impl.IdentityPlaceholderResolverStrategy;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;

public class CellTest extends CommonTestMethodBase {

    @Test
    public void testCell() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("evalmodify.drl"));

        final Environment env = EnvironmentFactory.newEnvironment();
        env.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES,
                new ObjectMarshallingStrategy[]{new IdentityPlaceholderResolverStrategy(ClassObjectMarshallingStrategyAcceptor.DEFAULT)});
        KieSession session = createKieSession(kbase, null, env);

        final Cell cell1 = new Cell(9);
        final Cell cell = new Cell(0);

        session.insert(cell1);
        session.insert(cell);

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);

        session.fireAllRules();
        assertEquals(9, cell.getValue());
    }

}
