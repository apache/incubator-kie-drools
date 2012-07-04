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

package org.drools.reteoo;

import org.drools.base.DefaultKnowledgeHelperFactory;
import org.drools.base.FieldFactory;
import org.drools.base.FieldDataFactory;
import org.drools.base.KnowledgeHelperFactory;
import org.drools.common.AgendaFactory;
import org.drools.common.DefaultAgendaFactory;
import org.drools.core.util.TripleFactory;
import org.drools.core.util.TripleFactoryImpl;
import org.drools.reteoo.builder.DefaultNodeFactory;
import org.drools.reteoo.builder.NodeFactory;
import org.drools.rule.LogicTransformer;
import org.drools.spi.FactHandleFactory;

public class ReteooComponentFactory {

    private FactHandleFactory handleFactory = new ReteooFactHandleFactory();


    public FactHandleFactory getFactHandleFactoryService() {
         return handleFactory;
    }

    public void setHandleFactoryProvider( FactHandleFactory provider ) {
        handleFactory = provider;
    }

    public void setDefaultHandleFactoryProvider() {
        handleFactory = new ReteooFactHandleFactory();
    }

    public static FactHandleFactory getDefaultHandleFactoryProvider() {
        return new ReteooFactHandleFactory();
    }




    private NodeFactory nodeFactory = new DefaultNodeFactory();

    public NodeFactory getNodeFactoryService() {
        return nodeFactory;
    }

    public void setNodeFactoryProvider( NodeFactory provider ) {
        nodeFactory = provider;
    }

    public void setDefaultNodeFactoryProvider() {
        nodeFactory = new DefaultNodeFactory();
    }

    public static DefaultNodeFactory getDefaultNodeFactoryProvider() {
        return new DefaultNodeFactory();
    }


    private RuleBuilderFactory ruleBuilderFactory = new ReteooRuleBuilderFactory();

    public RuleBuilderFactory getRuleBuilderFactory() {
        return ruleBuilderFactory;
    }

    public void setRuleBuilderProvider( RuleBuilderFactory provider ) {
        ruleBuilderFactory = provider;
    }

    public void setDefaultRuleBuilderProvider() {
        ruleBuilderFactory = new ReteooRuleBuilderFactory();
    }

    public static RuleBuilderFactory getDefaultRuleBuilderFactory() {
        return new ReteooRuleBuilderFactory();
    }





    private AgendaFactory agendaFactory = new DefaultAgendaFactory();

    public AgendaFactory getAgendaFactory() {
         return agendaFactory;
    }

    public void setAgendaFactory( AgendaFactory provider ) {
        agendaFactory = provider;
    }

    public void setDefaultAgendaFactory() {
        agendaFactory = new DefaultAgendaFactory();
    }

    public static AgendaFactory getDefaultAgendaFactory() {
        return new DefaultAgendaFactory();
    }



    private FieldDataFactory fieldFactory = FieldFactory.getInstance();

    public FieldDataFactory getFieldFactory() {
        return fieldFactory;
    }

    public void setFieldDataFactory( FieldDataFactory provider ) {
        fieldFactory = provider;
    }

    public void setDefaultFieldFactory() {
        fieldFactory = FieldFactory.getInstance();
    }

    public static FieldDataFactory getDefaultFieldFactory() {
        return FieldFactory.getInstance();
    }



    private TripleFactory tripleFactory = new TripleFactoryImpl();

    public TripleFactory getTripleFactory() {
         return tripleFactory;
    }

    public void setTripleFactory( TripleFactory provider ) {
        tripleFactory = provider;
    }

    public void setDefaultTripleFactory() {
        tripleFactory = new TripleFactoryImpl();
    }

    public static TripleFactory getDefaultTripleFactory() {
        return new TripleFactoryImpl();
    }




    private KnowledgeHelperFactory knowledgeHelperFactory = new DefaultKnowledgeHelperFactory();

    public KnowledgeHelperFactory getKnowledgeHelperFactory() {
         return knowledgeHelperFactory;
    }

    public void setKnowledgeHelperFactory( KnowledgeHelperFactory provider ) {
        knowledgeHelperFactory = provider;
    }

    public void setDefaultKnowledgeHelperFactory() {
        knowledgeHelperFactory = new DefaultKnowledgeHelperFactory();
    }

    public static KnowledgeHelperFactory getDefaultKnowledgeHelperFactory() {
        return new DefaultKnowledgeHelperFactory();
    }




    private LogicTransformer logicTransformer = LogicTransformer.getInstance();

    public LogicTransformer getLogicTransformer() {
        return logicTransformer;
    }

    public void setLogicTransformer( LogicTransformer provider ) {
        logicTransformer = provider;
    }

    public void setDefaultLogicTransformer() {
        logicTransformer = LogicTransformer.getInstance();
    }

    public static LogicTransformer getDefaultLogicTransformer() {
        return LogicTransformer.getInstance();
    }



}
