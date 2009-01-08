/*
 * Copyright 2008 Red Hat
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
 *
 */
package org.drools.examples.broker;

import java.io.IOException;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.examples.broker.events.Event;
import org.drools.examples.broker.events.EventReceiver;
import org.drools.examples.broker.model.Company;
import org.drools.examples.broker.model.CompanyRegistry;
import org.drools.examples.broker.model.StockTick;
import org.drools.examples.broker.ui.BrokerWindow;
import org.drools.io.ResourceFactory;
import org.drools.rule.EntryPoint;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

/**
 * The broker application
 *  
 * @author etirelli
 */
public class Broker implements EventReceiver {
    private static final String RULES_FILE = "/broker.drl";
    
    private BrokerWindow window;
    private CompanyRegistry companies;
    private StatefulKnowledgeSession session;
    private WorkingMemoryEntryPoint tickStream;

    public Broker(BrokerWindow window,
                  CompanyRegistry companies) {
        super();
        this.window = window;
        this.companies = companies;
        this.session = createSession();
        this.tickStream = this.session.getWorkingMemoryEntryPoint( "StockTick stream" );
    }

    @SuppressWarnings("unchecked")
    public void receive(Event<?> event) {
        StockTick tick = ((Event<StockTick>)event).getObject();
        Company company = this.companies.getCompany( tick.getSymbol() );
        this.tickStream.insert( tick );
        this.session.fireAllRules();
        window.updateCompany( company.getSymbol() );
    }
    
    private StatefulKnowledgeSession createSession() {
        KnowledgeBase kbase = loadRuleBase();
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        for( Company company : this.companies.getCompanies() ) {
            session.insert( company );
        }
        return session;
    }

    private KnowledgeBase loadRuleBase() {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        try {
            builder.add( ResourceFactory.newInputStreamResource( Broker.class.getResourceAsStream( RULES_FILE ) ),
                         ResourceType.DRL);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( builder.getKnowledgePackages() );
        return kbase;
    }
    
    
}
