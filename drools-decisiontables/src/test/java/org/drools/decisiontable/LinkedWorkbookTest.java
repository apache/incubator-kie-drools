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

package org.drools.decisiontable;

import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

public class LinkedWorkbookTest {

    @Test
    public void testBrokenExternalLinkButValueIsCached() throws Exception {
        KieHelper kieHelper = new KieHelper();
        // do not modify this XLS file using OpenOffice or LibreOffice or the external link gets corrupted and the test fails!
        InputStream dtableIs = this.getClass().getResourceAsStream("BZ967609-brokenExtLinkButValueCached.xls");
        kieHelper.addResource(ResourceFactory.newInputStreamResource(dtableIs),
                ResourceType.DTABLE);
        KieBase kbase = kieHelper.build();
        assertNotNull(kbase);
    }

}
