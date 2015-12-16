/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.models.testscenarios.backend;

import java.io.IOException;
import java.util.List;

import org.drools.compiler.compiler.DroolsParserException;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.internal.io.ResourceFactory;

import static org.junit.Assert.*;

/**
 * A class with some utilities for testing rules.
 */
public abstract class RuleUnit {

    /**
     * Return a wm ready to go based on the rules in a drl at the specified uri (in the classpath).
     */
    public KieSession getKieSession( String uri )
            throws DroolsParserException, IOException, Exception {

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem()
                .write( ResourceFactory.newClassPathResource( uri, getClass() ) )
                .writeKModuleXML( createKieProjectWithPackages( ks ).toXML() );
        KieBuilder builder = ks.newKieBuilder( kfs ).buildAll();

        List<Message> results = builder.getResults().getMessages();
        assertTrue( results.toString(), results.isEmpty() );

        KieSession ksession = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() ).newKieSession();

        return ksession;
    }

    private KieModuleModel createKieProjectWithPackages( KieServices ks ) {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel( "KBase1" )
                .setEventProcessingMode( EventProcessingOption.STREAM )
                .addPackage( "*" )
                .setDefault( true );

        kieBaseModel1.newKieSessionModel( "KSession1" )
                .setType( KieSessionModel.KieSessionType.STATEFUL )
                .setClockType( ClockTypeOption.get( "pseudo" ) )
                .setDefault( true );

        return kproj;
    }
}
