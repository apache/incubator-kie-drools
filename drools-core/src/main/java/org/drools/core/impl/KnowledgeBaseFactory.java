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

package org.drools.core.impl;

import java.util.Properties;
import java.util.UUID;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfiguration;
import org.drools.core.SessionConfigurationImpl;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.runtime.KieSessionConfiguration;

/**
 * <p>
 * This factory will create and return a KnowledgeBase instance, an optional KnowledgeBaseConfiguration
 * can be provided. The KnowledgeBaseConfiguration is also itself created from this factory.
 * </p>
 * <pre>
 * KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
 * </pre>
 *
 * <p>
 * Create sequential KnowledgeBase using the given ClassLoader.
 * </p>
 * <pre>
 * Properties properties = new Properties();
 * properties.setOption( SequentialOption.YES );
 * KnowledgeBaseConfiguration kbConf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(properties, myClassLoader);
 * KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(kbConf);
 * </pre>
 *
 * <p>
 * The above could also have used the supported property
 * </p>
 * <pre>
 * properties.setProperty( "org.kie.sequential", "true");
 * </pre>
 *
 * @see KnowledgeBase
 */
public class KnowledgeBaseFactory  {

    /**
     * Create a new KnowledgeBase using the default KnowledgeBaseConfiguration
     * @return
     *     The KnowledgeBase
     */
    public static InternalKnowledgeBase newKnowledgeBase() {
        return newKnowledgeBase( UUID.randomUUID().toString() );
    }

    /**
     * Create a new KnowledgeBase using the default KnowledgeBaseConfiguration and
     * the given KnowledgeBase ID.
     *
     * @param kbaseId
     *     A string Identifier for the knowledge base. Specially useful when enabling
     *     JMX monitoring and management, as that ID will be used to compose the
     *     JMX ObjectName for all related MBeans. The application must ensure all kbase
     *     IDs are unique.
     * @return
     *     The KnowledgeBase
     */
    public static InternalKnowledgeBase newKnowledgeBase(String kbaseId) {
        return newKnowledgeBase( kbaseId, null );
    }

    /**
     * Create a new KnowledgeBase using the given KnowledgeBaseConfiguration
     * @return
     *     The KnowledgeBase
     */
    public static InternalKnowledgeBase newKnowledgeBase(KieBaseConfiguration conf) {
        return newKnowledgeBase( UUID.randomUUID().toString(), (RuleBaseConfiguration) conf );
    }

    /**
     * Create a new KnowledgeBase using the given KnowledgeBaseConfiguration and
     * the given KnowledgeBase ID.
     *
     * @param kbaseId
     *     A string Identifier for the knowledge base. Specially useful when enabling
     *     JMX monitoring and management, as that ID will be used to compose the
     *     JMX ObjectName for all related MBeans. The application must ensure all kbase
     *     IDs are unique.
     * @return
     *     The KnowledgeBase
     */
    public static InternalKnowledgeBase newKnowledgeBase(String kbaseId,
                                                         KieBaseConfiguration conf) {
        return new KnowledgeBaseImpl( kbaseId, (RuleBaseConfiguration) conf);
    }

    /**
     * Create a KnowledgeBaseConfiguration on which properties can be set.
     * @return
     *     The KnowledgeBaseConfiguration.
     */
    public static KieBaseConfiguration newKnowledgeBaseConfiguration() {
        return new RuleBaseConfiguration();
    }

    /**
     * Create a KnowledgeBaseConfiguration on which properties can be set. Use
     * the given properties file and ClassLoader - either of which can be null.
     * @return
     *     The KnowledgeBaseConfiguration.
     */
    public static KieBaseConfiguration newKnowledgeBaseConfiguration(Properties properties,
                                                                     ClassLoader... classLoaders) {
        return new RuleBaseConfiguration(properties, classLoaders);
    }

    /**
     * Create a KnowledgeSessionConfiguration on which properties can be set.
     * @return
     *     The KnowledgeSessionConfiguration.
     */
    public static KieSessionConfiguration newKnowledgeSessionConfiguration() {
        return SessionConfiguration.newInstance();
    }

    /**
     * Create a KnowledgeSessionConfiguration on which properties can be set.
     * @return
     *     The KnowledgeSessionConfiguration.
     */
    public static KieSessionConfiguration newKnowledgeSessionConfiguration(Properties properties) {
        return new SessionConfigurationImpl(properties);
    }

}
