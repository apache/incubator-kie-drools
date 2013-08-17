/*
 * Copyright 2011 Red Hat Inc.
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
package org.drools.persistence.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Properties;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.drools.core.base.MapGlobalResolver;
import org.drools.core.impl.EnvironmentFactory;
import org.infinispan.AdvancedCache;
import org.infinispan.manager.DefaultCacheManager;
import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;

public class PersistenceUtil {

    private static Logger logger = LoggerFactory.getLogger( PersistenceUtil.class );

    private static boolean TEST_MARSHALLING = true;
    
    // Persistence and data source constants
    public static final String DROOLS_PERSISTENCE_UNIT_NAME = "drools-configured-cache";
    public static final String JBPM_PERSISTENCE_UNIT_NAME = "jbpm-configured-cache";
        
    protected static final String DATASOURCE_PROPERTIES = "/datasource.properties";
    
    // Setup and marshalling setup constants
    public static String DATASOURCE = "org.droolsjbpm.persistence.datasource";

    /**
     * @see #setupWithPoolingDataSource(String, String, boolean)
     * @param persistenceUnitName The name of the persistence unit to be used.
     * @return test context
     */
    public static HashMap<String, Object> setupWithPoolingDataSource(String persistenceUnitName) {
        return setupWithPoolingDataSource(persistenceUnitName, true);
    }
    
    /**
     * @see #setupWithPoolingDataSource(String, String, boolean)
     * @param persistenceUnitName The name of the persistence unit to be used.
     * @return test context
     */
    public static HashMap<String, Object> setupWithPoolingDataSource(String persistenceUnitName, boolean testMarshalling) {
        HashMap<String, Object> context = new HashMap<String, Object>();

        // set the right jdbc url
        Properties dsProps = getDatasourceProperties();
        Object testMarshallingProperty = dsProps.get("testMarshalling"); 
        if( "true".equals(testMarshallingProperty) ) { 
            TEST_MARSHALLING = true;
           if( !testMarshalling ) { 
               TEST_MARSHALLING = false;
           }
        } 
        else { 
            TEST_MARSHALLING = false;
        }

        if( TEST_MARSHALLING ) {
            Class<?> testClass = null;
            StackTraceElement [] ste = Thread.currentThread().getStackTrace();
            int i = 1;
            do { 
                try {
                    testClass = Class.forName(ste[i++].getClassName());
                } catch (ClassNotFoundException e) {
                    // do nothing.. 
                }
            } while ( PersistenceUtil.class.equals(testClass) && i < ste.length );
            assertNotNull("Unable to resolve test class!", testClass);
        }

        // Setup persistence
        try {
	    	DefaultCacheManager cm = new DefaultCacheManager("infinispan.xml");
	    	AdvancedCache<?, ?> ac = cm.getCache("jbpm-configured-cache").getAdvancedCache();
	    	if (TEST_MARSHALLING) {
	        	try {
	        		
	        		UserTransaction ut = (UserTransaction) ac.getTransactionManager().getTransaction();
	        		context.put(EnvironmentName.TRANSACTION, ut);
	        		//cm.start();
	        	} catch (SystemException e) {
	        		//TODO
	        	}
	    		context.put(EnvironmentName.TRANSACTION_SYNCHRONIZATION_REGISTRY, TransactionManagerServices.getTransactionSynchronizationRegistry());

	        }
	    	context.put(EnvironmentName.ENTITY_MANAGER_FACTORY, cm);
        } catch (IOException e) {
        	//TODO
        }

        return context;
    }

    /**
     * Please use {@link #cleanUp(HashMap)} because tearDown() ends up conflicting with Junit methods at times. 
     * @see {@link PersistenceUtil#cleanUp(HashMap)}
     */
    @Deprecated
    public static void tearDown(HashMap<String, Object> context) {
       cleanUp(context);     
    }
    
    /**
     * This method should be called in the @After method of a test to clean up
     * the persistence unit and datasource.
     * 
     * @param context
     *            A HashMap generated by
     *            {@link org.kie.api.persistence.util.PersistenceUtil setupWithPoolingDataSource(String)}
     * 
     */
    public static void cleanUp(HashMap<String, Object> context) {
        if (context != null) {
            
            BitronixTransactionManager txm = TransactionManagerServices.getTransactionManager();
            if( txm != null ) { 
                txm.shutdown();
            }
            
            Object cmObject = context.remove(EnvironmentName.ENTITY_MANAGER_FACTORY);
            if (cmObject != null) {
                try {
                	DefaultCacheManager cm = (DefaultCacheManager) cmObject;
                    //cm.stop();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
        
    }
    
    /**
     * This reads in the (maven filtered) datasource properties from the test
     * resource directory.
     * 
     * @return Properties containing the datasource properties.
     */
    public static Properties getDatasourceProperties() { 
        String propertiesNotFoundMessage = "Unable to load datasource properties [" + DATASOURCE_PROPERTIES + "]";

        // Central place to set additional H2 properties
        System.setProperty("h2.lobInDatabase", "true");
        
        InputStream propsInputStream = PersistenceUtil.class.getResourceAsStream(DATASOURCE_PROPERTIES);
        assertNotNull(propertiesNotFoundMessage, propsInputStream);
        Properties props = new Properties();
        if (propsInputStream != null) {
            try {
                props.load(propsInputStream);
            } catch (IOException ioe) {
                logger.warn("Unable to find properties, using default H2 properties: " + ioe.getMessage());
                ioe.printStackTrace();
            }
        }
        return props;
    }

    /**
     * Reflection method when doing ugly hacks in tests.
     * 
     * @param fieldname
     *            The name of the field to be retrieved.
     * @param source
     *            The object containing the field to be retrieved.
     * @return The value (object instance) stored in the field requested from
     *         the given source object.
     */
    public static Object getValueOfField(String fieldname, Object source) {
        String sourceClassName = source.getClass().getSimpleName();
    
        Field field = null;
        try {
            field = source.getClass().getDeclaredField(fieldname);
            field.setAccessible(true);
        } catch (SecurityException e) {
            fail("Unable to retrieve " + fieldname + " field from " + sourceClassName + ": " + e.getCause());
        } catch (NoSuchFieldException e) {
            fail("Unable to retrieve " + fieldname + " field from " + sourceClassName + ": " + e.getCause());
        }
    
        assertNotNull("." + fieldname + " field is null!?!", field);
        Object fieldValue = null;
        try {
            fieldValue = field.get(source);
        } catch (IllegalArgumentException e) {
            fail("Unable to retrieve value of " + fieldname + " from " + sourceClassName + ": " + e.getCause());
        } catch (IllegalAccessException e) {
            fail("Unable to retrieve value of " + fieldname + " from " + sourceClassName + ": " + e.getCause());
        }
        return fieldValue;
    }

    public static Environment createEnvironment(HashMap<String, Object> context) { 
        Environment env = EnvironmentFactory.newEnvironment();
        
        UserTransaction ut = (UserTransaction) context.get(EnvironmentName.TRANSACTION);
        if( ut != null ) { 
            env.set( EnvironmentName.TRANSACTION, ut);
        }
        
        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY, context.get(EnvironmentName.ENTITY_MANAGER_FACTORY) );
        TransactionManager tm = TransactionManagerServices.getTransactionManager();
        env.set( EnvironmentName.TRANSACTION_MANAGER, tm );
        env.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );
        
        return env;
    }
    
   public static StatefulKnowledgeSession createKnowledgeSessionFromKBase(KieBase kbase, HashMap<String, Object> context) {
       KieSessionConfiguration ksconf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
       StatefulKnowledgeSession knowledgeSession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, ksconf, createEnvironment(context));
       return knowledgeSession;
   }
   
}
