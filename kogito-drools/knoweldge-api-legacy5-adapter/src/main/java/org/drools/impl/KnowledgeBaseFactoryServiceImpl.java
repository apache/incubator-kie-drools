package org.drools.impl;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactoryService;
import org.drools.impl.adapters.EnvironmentAdapter;
import org.drools.impl.adapters.KnowledgeBaseAdapter;
import org.drools.impl.adapters.KnowledgeBaseConfigurationAdapter;
import org.drools.impl.adapters.KnowledgeSessionConfigurationAdapter;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;

import java.util.Properties;

public class KnowledgeBaseFactoryServiceImpl implements KnowledgeBaseFactoryService {

    private final org.drools.core.impl.KnowledgeBaseFactoryServiceImpl delegate = new org.drools.core.impl.KnowledgeBaseFactoryServiceImpl();

    public KnowledgeBaseConfiguration newKnowledgeBaseConfiguration() {
        return new KnowledgeBaseConfigurationAdapter(delegate.newKnowledgeBaseConfiguration());
    }

    public KnowledgeBaseConfiguration newKnowledgeBaseConfiguration(Properties properties, ClassLoader... classLoader) {
        return new KnowledgeBaseConfigurationAdapter(delegate.newKnowledgeBaseConfiguration(properties, classLoader));
    }

    public KnowledgeSessionConfiguration newKnowledgeSessionConfiguration() {
        return new KnowledgeSessionConfigurationAdapter(delegate.newKnowledgeSessionConfiguration());
    }

    public KnowledgeSessionConfiguration newKnowledgeSessionConfiguration(Properties properties) {
        return new KnowledgeSessionConfigurationAdapter(delegate.newKnowledgeSessionConfiguration(properties));
    }

    public KnowledgeBase newKnowledgeBase() {
        return new KnowledgeBaseAdapter(delegate.newKnowledgeBase());
    }

    public KnowledgeBase newKnowledgeBase(String kbaseId) {
        return new KnowledgeBaseAdapter(delegate.newKnowledgeBase(kbaseId));
    }

    public KnowledgeBase newKnowledgeBase(KnowledgeBaseConfiguration conf) {
        return new KnowledgeBaseAdapter(delegate.newKnowledgeBase(((KnowledgeBaseConfigurationAdapter)conf).getDelegate()));
    }

    public KnowledgeBase newKnowledgeBase(String kbaseId, KnowledgeBaseConfiguration conf) {
        return new KnowledgeBaseAdapter(delegate.newKnowledgeBase(kbaseId, ((KnowledgeBaseConfigurationAdapter)conf).getDelegate()));
    }

    public Environment newEnvironment() {
        return new EnvironmentAdapter(delegate.newEnvironment());
    }
}
