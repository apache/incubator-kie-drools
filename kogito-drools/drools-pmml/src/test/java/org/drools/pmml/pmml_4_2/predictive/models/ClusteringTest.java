/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.pmml.pmml_4_2.predictive.models;


import org.drools.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.junit.After;
import org.junit.Test;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClusteringTest extends DroolsAbstractPMMLTest {


    private static final boolean VERBOSE = true;
    private static final String source1 = "org/drools/pmml/pmml_4_2/test_clustering.xml";
    private static final String packageName = "org.drools.pmml.pmml_4_2";


    @After
    public void tearDown() {
        getKSession().dispose();
    }

    @Test
    public void testCenterBasedClustering() throws Exception {
        setKSession( getModelSession( source1, VERBOSE ) );
        setKbase( getKSession().getKieBase() );
        KieSession kSession = getKSession();

        kSession.fireAllRules();  //init model

        kSession.getEntryPoint( "in_Fld0" ).insert( "y" );
        kSession.getEntryPoint( "in_Fld1" ).insert( 2.0 );
        kSession.getEntryPoint( "in_Fld2" ).insert( -1.0 );

        kSession.fireAllRules();
        
        FactType mu = kSession.getKieBase().getFactType( packageName, "DistanceMembership" );
        Collection mus = kSession.getObjects( new ClassObjectFilter( mu.getFactClass()) );
        assertTrue( mus.size() > 0 );
        for ( Object x : mus ) {
            Integer ix = (Integer) mu.get( x, "index" );
            String lab = (String) mu.get( x, "label" );
            Double m = (Double) mu.get( x, "mu" );

            if ( ix == 0 ) {
                assertEquals( "Klust1", lab );
                assertEquals( 41.1, m, 0.001 );
            } else if ( ix == 1 ) {
                assertEquals( "Klust2", lab );
                assertEquals( 14704.428, m, 0.001 );
            }
        }

        checkGeneratedRules();
    }                         




}
