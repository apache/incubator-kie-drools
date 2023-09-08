/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.decisiontable;

import org.drools.template.parser.DecisionTableParseException;
import org.junit.Test;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;

import static org.kie.api.io.ResourceType.DTABLE;
import static org.kie.internal.builder.DecisionTableInputType.CSV;
import static org.kie.internal.builder.DecisionTableInputType.XLS;
import static org.kie.internal.io.ResourceFactory.newClassPathResource;

public class EmptyHeaderTest {

    @Test(expected = DecisionTableParseException.class)
    public void testEmptyConditionInXLS() {
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(XLS);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory .newKnowledgeBuilder();
        
        kbuilder.add(newClassPathResource("emptyCondition.drl.xls", getClass()), DTABLE, dtconf);
    }

    @Test(expected = DecisionTableParseException.class)
    public void testEmptyActionInCSV() {
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(CSV);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        
        kbuilder.add(newClassPathResource("emptyAction.drl.csv", getClass()), DTABLE, dtconf);
    }
}
