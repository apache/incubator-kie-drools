/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.examples.traits;


import org.drools.core.io.impl.ClassPathResource;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.api.io.ResourceType;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.Collection;


public class TraitExample {


    private static StatefulKnowledgeSession getSession( String drl ) {

        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add( new ClassPathResource( "org/drools/examples/traits/" + drl ), ResourceType.DRL );

        if ( kBuilder.hasErrors() ) {
            System.err.println( kBuilder.getErrors().toString() );
        }
        
        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addKnowledgePackages( kBuilder.getKnowledgePackages() );

        return kBase.newStatefulKnowledgeSession();

    }

    public static void run( String demo ) {

        StatefulKnowledgeSession kSession = getSession( demo );

        kSession.fireAllRules();


        Collection c =  kSession.getObjects();
        System.out.println( "------------------------- " + c.size() + " ----------------------" );
        for ( Object o : c ) {
            System.out.println( " \t --- " + o );
        }
        System.out.println( "-----------------------------------------------------------------" );

        kSession.dispose();

    }


    public static void main( String[] args ) {

        run( "noTraits.drl" );

        run( "traitsDon.drl" );

        run( "multipleTraits.drl" );

        run( "traitsMixins.drl" );

        run( "traitsShed.drl" );


    }










}
