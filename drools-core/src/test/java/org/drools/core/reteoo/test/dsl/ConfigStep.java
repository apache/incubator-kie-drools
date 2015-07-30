/*
 * Copyright 2015 JBoss Inc
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

package org.drools.core.reteoo.test.dsl;

import static org.drools.core.reteoo.test.ReteDslTestEngine.BUILD_CONTEXT;
import static org.drools.core.reteoo.test.ReteDslTestEngine.WORKING_MEMORY;

import java.util.List;
import java.util.Map;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.reteoo.builder.BuildContext;

/**
 * <p>
 * A step in the setup of a nodeTestCase, it allows any configuration parameters
 * to be passed. </br> Note that the RuleBase and Working Memory are recreated
 * and changed in the context, so the configuration step should be the first one
 * in the setup of your nodeTestCase or you may face inconsistent behavior.
 * </p>
 * <b>Usage:</b>
 * 
 * <pre>
 * Setup
 *     Config:
 *         drools.phreakEnabled, true;
 *     ObjectTypeNode:
 *         otnLeft1, org.drools.Person;
 *     LeftInputAdapterNode:
 *         lian0, otnLeft1;
 *     ObjectTypeNode:
 *         otnRight1, org.drools.Person;
 *     Binding:
 *          p1, 0, org.drools.Person, age;
 *     JoinNode:
 *         join1, lian0, otnRight1;
 *         age, !=, p1;
 *     Facts:
 *         new org.drools.Person('darth', 35), new org.drools.Person('bobba', 36),
 *</pre>
 * 
 */
public class ConfigStep implements Step {

    public void execute(Map<String, Object> context, List<String[]> args) {

        RuleBaseConfiguration conf = new RuleBaseConfiguration();

        for (String[] configOption : args) {
            conf.setProperty(configOption[0], configOption[1]);
        }

        KnowledgeBaseImpl rbase = new KnowledgeBaseImpl("ID", conf);
        BuildContext buildContext = new BuildContext(rbase, rbase
                .getReteooBuilder().getIdGenerator());

        InternalWorkingMemory wm = (InternalWorkingMemory) rbase
                .newStatefulSession(true);

        // Overwrite values now taking into account the configuration options.
        context.put(BUILD_CONTEXT, buildContext);
        context.put(WORKING_MEMORY, wm);

    }

}
