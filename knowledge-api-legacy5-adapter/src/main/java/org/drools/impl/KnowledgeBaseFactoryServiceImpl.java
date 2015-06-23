/*
 * Copyright 2015 JBoss Inc
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

package org.drools.impl;

import java.util.Properties;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactoryService;
import org.drools.base.DefaultConsequenceExceptionHandler;
import org.drools.conf.ConsequenceExceptionHandlerOption;
import org.drools.impl.adapters.EnvironmentAdapter;
import org.drools.impl.adapters.KnowledgeBaseAdapter;
import org.drools.impl.adapters.KnowledgeBaseConfigurationAdapter;
import org.drools.impl.adapters.KnowledgeSessionConfigurationAdapter;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.kie.api.KieBaseConfiguration;
import org.kie.internal.builder.conf.RuleEngineOption;

public class KnowledgeBaseFactoryServiceImpl implements KnowledgeBaseFactoryService {

    private final org.drools.core.impl.KnowledgeBaseFactoryServiceImpl delegate = new org.drools.core.impl.KnowledgeBaseFactoryServiceImpl();

    public KnowledgeBaseConfiguration newKnowledgeBaseConfiguration() {
        return new KnowledgeBaseConfigurationAdapter(newConfiguration());
    }

    private KieBaseConfiguration newConfiguration() {
        KieBaseConfiguration conf = delegate.newKnowledgeBaseConfiguration();
        conf.setProperty(ConsequenceExceptionHandlerOption.PROPERTY_NAME, DefaultConsequenceExceptionHandler.class.getCanonicalName());
        return conf;
    }

    public KnowledgeBaseConfiguration newKnowledgeBaseConfiguration(Properties properties, ClassLoader... classLoader) {
        return new KnowledgeBaseConfigurationAdapter(newConfiguration(properties, classLoader));
    }

    private KieBaseConfiguration newConfiguration(Properties properties, ClassLoader... classLoader) {
        KieBaseConfiguration conf = delegate.newKnowledgeBaseConfiguration(properties, classLoader);
        conf.setProperty(ConsequenceExceptionHandlerOption.PROPERTY_NAME, DefaultConsequenceExceptionHandler.class.getCanonicalName());
        return conf;
    }

    public KnowledgeSessionConfiguration newKnowledgeSessionConfiguration() {
        return new KnowledgeSessionConfigurationAdapter(delegate.newKnowledgeSessionConfiguration());
    }

    public KnowledgeSessionConfiguration newKnowledgeSessionConfiguration(Properties properties) {
        return new KnowledgeSessionConfigurationAdapter(delegate.newKnowledgeSessionConfiguration(properties));
    }

    public KnowledgeBase newKnowledgeBase() {
        return newKnowledgeBase(newKnowledgeBaseConfiguration());
    }

    public KnowledgeBase newKnowledgeBase(String kbaseId) {
        return newKnowledgeBase(kbaseId, newKnowledgeBaseConfiguration());
    }

    public KnowledgeBase newKnowledgeBase(KnowledgeBaseConfiguration conf) {
        KieBaseConfiguration kieBaseConf = ((KnowledgeBaseConfigurationAdapter) conf).getDelegate();
        kieBaseConf.setOption(RuleEngineOption.RETEOO);
        kieBaseConf.setProperty(ConsequenceExceptionHandlerOption.PROPERTY_NAME, DefaultConsequenceExceptionHandler.class.getCanonicalName());
        return new KnowledgeBaseAdapter(delegate.newKnowledgeBase(kieBaseConf));
    }

    public KnowledgeBase newKnowledgeBase(String kbaseId, KnowledgeBaseConfiguration conf) {
        KieBaseConfiguration kieBaseConf = ((KnowledgeBaseConfigurationAdapter) conf).getDelegate();
        kieBaseConf.setOption(RuleEngineOption.RETEOO);
        kieBaseConf.setProperty(ConsequenceExceptionHandlerOption.PROPERTY_NAME, DefaultConsequenceExceptionHandler.class.getCanonicalName());
        return new KnowledgeBaseAdapter(delegate.newKnowledgeBase(kbaseId, kieBaseConf));
    }

    public Environment newEnvironment() {
        return new EnvironmentAdapter(delegate.newEnvironment());
    }
}
