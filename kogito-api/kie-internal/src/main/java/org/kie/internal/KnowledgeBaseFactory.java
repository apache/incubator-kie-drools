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

package org.kie.internal;

import java.util.Properties;

import org.kie.api.KieBaseConfiguration;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.utils.ServiceRegistryImpl;

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
    private static KnowledgeBaseFactoryService factoryService;

    /**
     * Create a new KnowledgeBase using the default KnowledgeBaseConfiguration
     * @return
     *     The KnowledgeBase
     */
    public static KnowledgeBase newKnowledgeBase() {
        return getKnowledgeBaseFactoryService().newKnowledgeBase();
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
    public static KnowledgeBase newKnowledgeBase(String kbaseId) {
        return getKnowledgeBaseFactoryService().newKnowledgeBase(kbaseId);
    }

    /**
     * Create a new KnowledgeBase using the given KnowledgeBaseConfiguration
     * @return
     *     The KnowledgeBase
     */
    public static KnowledgeBase newKnowledgeBase(KieBaseConfiguration conf) {
        return getKnowledgeBaseFactoryService().newKnowledgeBase( conf );
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
    public static KnowledgeBase newKnowledgeBase(String kbaseId,
                                                 KieBaseConfiguration conf) {
        return getKnowledgeBaseFactoryService().newKnowledgeBase( kbaseId, conf );
    }

    /**
     * Create a KnowledgeBaseConfiguration on which properties can be set.
     * @return
     *     The KnowledgeBaseConfiguration.
     */
    public static KieBaseConfiguration newKnowledgeBaseConfiguration() {
        return getKnowledgeBaseFactoryService().newKnowledgeBaseConfiguration();
    }

    /**
     * Create a KnowledgeBaseConfiguration on which properties can be set. Use
     * the given properties file and ClassLoader - either of which can be null.
     * @return
     *     The KnowledgeBaseConfiguration.
     */
    public static KieBaseConfiguration newKnowledgeBaseConfiguration(Properties properties,
                                                                           ClassLoader... classLoaders) {
        return getKnowledgeBaseFactoryService().newKnowledgeBaseConfiguration( properties,
                                                                               classLoaders );
    }

    /**
     * Create a KnowledgeSessionConfiguration on which properties can be set.
     * @return
     *     The KnowledgeSessionConfiguration.
     */
    public static KieSessionConfiguration newKnowledgeSessionConfiguration() {
        return getKnowledgeBaseFactoryService().newKnowledgeSessionConfiguration();
    }

    /**
     * Create a KnowledgeSessionConfiguration on which properties can be set.
     * @return
     *     The KnowledgeSessionConfiguration.
     */
    public static KieSessionConfiguration newKnowledgeSessionConfiguration(Properties properties) {
        return getKnowledgeBaseFactoryService().newKnowledgeSessionConfiguration( properties );
    }

    public static Environment newEnvironment() {
        return getKnowledgeBaseFactoryService().newEnvironment();
    }

    public static synchronized void setKnowledgeBaseServiceFactory(KnowledgeBaseFactoryService serviceFactory) {
        KnowledgeBaseFactory.factoryService = serviceFactory;
    }

    private static synchronized KnowledgeBaseFactoryService getKnowledgeBaseFactoryService() {
        if ( factoryService == null ) {
            loadServiceFactory();
        }
        return factoryService;
    }

    @SuppressWarnings("unchecked")
    private static void loadServiceFactory() {
        setKnowledgeBaseServiceFactory( ServiceRegistryImpl.getInstance().get( KnowledgeBaseFactoryService.class ) );
    }
}
