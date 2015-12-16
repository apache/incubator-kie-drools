/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.decisiontable.integrationtests;

import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Basic tests for creation of a KieBase from CSV and XLS resources.
 */
public class KModuleWithDecisionTablesTest {

    @Test
    public void testNonEmptyKieBaseWithXLS() throws Exception {
       testNonEmptyKieBase("kbaseXLS");
    }
    
    @Test
    public void testNonEmptyKieBaseWithXLSX() throws Exception {
       testNonEmptyKieBase("kbaseXLSX");
    }

    @Test
    public void testNonEmptyKieBaseWithCSV() throws Exception {
       testNonEmptyKieBase("kbaseCSV");
    }

    @Test
    public void testNonEmptyKieBaseWithWrongCSV() throws Exception {
        String csv = "\"RuleSet\",\"org.jboss.qa.brms.bre.functional.expert.decisiontable\",,,\n" +
                     "\"Import\",\"org.jboss.qa.brms.domain.Message\",,,\n" +
                     ",,,,\n" +
                     "\"RuleTable TimerCalendarRule\",,,,\n" +
                     "\"NAME\",\"TIMER\",\"CALENDARS\",\"ACTION\",\"Description\"\n" +
                     ",,,,\n" +
                     ",,,\"insert(new Message(\\\"$param\\\"));\",\n" +
                     "\"Rule name\",\"Timer value\",\"Calendar value\",\"Inserted message text\",\"Note\"\n" +
                     "\"Weekend rule\",,\"weekend\",\"Weekend rule\",\n" +
                     "\"Every 100 ms weekday rule\",\"int: 0 100ms\",\"weekday\",\"Every 100 ms weekday rule\",\n" +
                     "\"Delayed Tuesday rule\",\"int: 1000 0\",\"tuesday\",\"Delayed Tuesday rule\",\n" +
                     "\"Delayed repetitive Friday rule\",\"int: 500ms 1s\",\"friday\",\"Delayed repetitive Friday rule\",\n" +
                     "\"Delayed rule\",\"int: 1s 0ms\",,\"Delayed rule\",\n" +
                     "\"Repetitive rule\",\"int: 0ms 100\",,\"Repetitive rule\",\n" +
                     "\"Repetitive delayed rule\",\"int: 1s 100ms\",,\"Repetitive delayed rule\",\n" +
                     "\"Cron rule\",\"cron: */1 * * * * ?\",,\"Cron rule\",\n";

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.csv", csv );
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertFalse(results.getMessages().isEmpty());
    }

    private void testNonEmptyKieBase(final String kieBaseName) throws Exception {
       KieServices ks = KieServices.Factory.get();
       KieContainer kContainer = ks.getKieClasspathContainer();

       KieBase kieBase = kContainer.getKieBase(kieBaseName);

       assertNotNull("KieBase not found", kieBase);
       assertEquals("Unexpected number of KiePackages in KieBase", 1, kieBase.getKiePackages().size());
    }
    
}
