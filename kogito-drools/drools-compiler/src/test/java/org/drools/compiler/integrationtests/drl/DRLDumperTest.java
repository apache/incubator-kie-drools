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

import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.compiler.DrlParser;
import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.compiler.lang.DrlDumper;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.io.impl.InputStreamResource;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.conf.LanguageLevelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DRLDumperTest extends CommonTestMethodBase {

    private static Logger logger = LoggerFactory.getLogger( DRLDumperTest.class );

    @Test
    public void testDumpers() throws Exception {
        final DrlParser parser = new DrlParser(LanguageLevelOption.DRL5);
        final Resource resource = new InputStreamResource(getClass().getResourceAsStream("test_Dumpers.drl"));
        final PackageDescr pkg = parser.parse(resource);

        if (parser.hasErrors()) {
            for (final DroolsError error : parser.getErrors()) {
                logger.warn(error.toString());
            }
            fail(parser.getErrors().toString());
        }

        KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase(pkg));
        KieSession ksession = kbase.newKieSession();

        List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Cheese brie = new Cheese("brie", 12);
        ksession.insert(brie);

        ksession.fireAllRules();

        assertEquals(3, list.size());
        assertEquals("3 1", list.get(0));
        assertEquals("MAIN", list.get(1));
        assertEquals("1 1", list.get(2));

        final DrlDumper drlDumper = new DrlDumper();
        final String drlResult = drlDumper.dump(pkg);

        System.out.println(drlResult);

        kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(drlResult));
        ksession = kbase.newKieSession();

        list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.insert(brie);

        ksession.fireAllRules();

        assertEquals(3, list.size());
        assertEquals("3 1", list.get(0));
        assertEquals("MAIN", list.get(1));
        assertEquals("1 1", list.get(2));
    }

}
