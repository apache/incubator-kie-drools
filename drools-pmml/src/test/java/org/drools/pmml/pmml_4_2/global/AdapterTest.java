/*
 * Copyright 2011 JBoss Inc
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

package org.drools.pmml.pmml_4_2.global;

import org.drools.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.drools.pmml.pmml_4_2.PMML4Helper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.internal.io.ResourceFactory;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class AdapterTest extends DroolsAbstractPMMLTest {



    @Test
    public void testCustomInputAdapter() {
        String source = PMML4Helper.pmmlDefaultPackageName().replace( ".", File.separator ) + File.separator + "mock_cold_adapter.xml";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write( ResourceFactory.newClassPathResource( source ).setResourceType( ResourceType.PMML ) );
        Results res = ks.newKieBuilder( kfs ).buildAll().getResults();
        if ( res.hasMessages( Message.Level.ERROR ) ) {
            System.out.println( res.getMessages( Message.Level.ERROR ) );
        }
        assertEquals( 0, res.getMessages( Message.Level.ERROR ).size() );

        KieBase kieBase = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).getKieBase();

        FactType ft = kieBase.getFactType( "test", "MyAdapter" );
        assertTrue( ft != null );
        assertTrue( ft.getFactClass().isInterface() );

        FactType fto = kieBase.getFactType( "test", "MyOutAdapter" );
        assertTrue( fto != null );

        assertTrue( fto.getFactClass().isInterface() );
    }



}