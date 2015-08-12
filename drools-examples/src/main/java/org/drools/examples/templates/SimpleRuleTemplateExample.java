/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.examples.templates;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.ArrayList;
import java.util.List;

/**
 * This shows off a very simple rule template where the data provider is a spreadsheet.
 */
public class SimpleRuleTemplateExample {

    public static void main(String[] args) {
        SimpleRuleTemplateExample launcher = new SimpleRuleTemplateExample();
        launcher.executeExample();
    }

    private void executeExample() {

        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        KieSession ksession = kc.newKieSession( "TemplatesKS" );

        //now create some test data
        ksession.insert( new Cheese( "stilton",
                               42 ) );
        ksession.insert( new Person( "michael",
                               "stilton",
                               42 ) );
        final List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        System.out.println(list);

        ksession.dispose();
    }
}
