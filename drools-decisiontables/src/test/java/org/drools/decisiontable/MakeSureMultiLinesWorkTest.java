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
package org.drools.decisiontable;

import java.io.InputStream;

import com.sample.FactData;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@Ignore
public class MakeSureMultiLinesWorkTest {

    @Test
    public void makeSureMultiLinesWork() {

        KieHelper kieHelper = new KieHelper();
        // do not modify this XLS file using OpenOffice or LibreOffice or the external link gets corrupted and the test fails!
        InputStream dtableIs = this.getClass().getResourceAsStream("MultiLinesInAction.xls");
        kieHelper.addResource(ResourceFactory.newInputStreamResource(dtableIs),
                              ResourceType.DTABLE);
        KieBase kbase = kieHelper.build();
        assertNotNull(kbase);
    }
}
