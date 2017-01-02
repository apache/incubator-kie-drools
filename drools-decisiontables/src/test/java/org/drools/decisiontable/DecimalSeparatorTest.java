/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.drools.decisiontable;

import java.util.Locale;

import org.junit.After;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.*;

/**
 * A class to test decimal separated value in action column with different locales.
 */

public class DecimalSeparatorTest {

    private KieSession ksession;

    public void init() {
        final KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieModuleModel kmodule = ks.newKieModuleModel();

        KieBaseModel baseModel = kmodule.newKieBaseModel( "defaultKBase" ).setDefault( true );
        baseModel.newKieSessionModel( "defaultKSession" ).setDefault( true );

        kfs.writeKModuleXML( kmodule.toXML() );
        kfs.write( ks.getResources().newClassPathResource( "decimalSeparator.xls",
                                                           this.getClass() ) ); // README when path is set then test works
        KieBuilder kieBuilder = ks.newKieBuilder( kfs ).buildAll();
        assertEquals( 0,
                      kieBuilder.getResults().getMessages( org.kie.api.builder.Message.Level.ERROR ).size() );

        ksession = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).newKieSession();
    }

    @After
    public void clear() {
        if ( ksession != null ) {
            ksession.dispose();
        }
    }

    @Test
    public void testDecimalSeparatorInFrench() {
        Locale.setDefault( Locale.FRENCH );
        init();
        ksession.insert( "Hello" );
        assertEquals( 1,
                      ksession.fireAllRules() );
    }

    @Test
    public void testDecimalSeparatorInEnglish() {
        Locale.setDefault( Locale.ENGLISH );
        init();
        ksession.insert( "Hello" );
        assertEquals( 1,
                      ksession.fireAllRules() );
    }

}
