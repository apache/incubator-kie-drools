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

package org.kie.dmn.signavio;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignavioTest {
    public static final Logger LOG = LoggerFactory.getLogger(SignavioTest.class);
    
    @Test
    public void test() {
        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();
        
        KieModuleModel kmm = ks.newKieModuleModel();
        kmm.setConfigurationProperty("org.kie.dmn.profiles.signavio", "org.kie.dmn.signavio.KieDMNSignavioProfile");
        kfs.writeKModuleXML(kmm.toXML());
        kfs.write(ks.getResources().newClassPathResource("Test_Signavio_multiple.dmn", this.getClass()));
        
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        Results results = kieBuilder.getResults();
        LOG.info("buildAll() completed.");
        results.getMessages(Level.WARNING).forEach( e -> LOG.warn("{}", e));
        assertTrue( results.getMessages(Level.WARNING).size() == 0 );

        final KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        
        List<DMNModel> models = runtime.getModels();
        
        DMNContext context = runtime.newContext();
        context.set("persons", Arrays.asList(new String[]{"p1", "p2"}));
        
        DMNModel model0 = models.get(0);
        System.out.println("EVALUATE ALL:");
        DMNResult evaluateAll = runtime.evaluateAll(model0, context);
        System.out.println(evaluateAll);
        
        assertThat( (List<?>) evaluateAll.getContext().get( "Greeting for each Person in Persons" ), contains( "Hello p1", "Hello p2" ) );
    }
}
