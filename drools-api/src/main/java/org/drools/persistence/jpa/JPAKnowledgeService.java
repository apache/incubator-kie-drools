/**
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

package org.drools.persistence.jpa;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactoryService;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.util.ServiceRegistryImpl;

/**
 * <p>
 * Long term out of the box persistence with JPA is possible with Drools. You will need to have JTA installed, for development purposes we recommend Bitronix as it's simple
 * to setup and works embedded, but for production use JBoss Transactions is recommended.
 * </p>
 * 
 * <pre>
 * Environment env = KnowledgeBaseFactory.newEnvironment();
 * env.set( EnvironmentName.ENTITY_MANAGER_FACTORY, Persistence.createEntityManagerFactory( "emf-name" ) );
 * env.set( EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager() );
 *          
 * StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env ); // KnowledgeSessionConfiguration may be null, and a default will be used
 * int sessionId = ksession.getId();
 * 
 * UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
 * ut.begin();
 * ksession.insert( data1 );
 * ksession.insert( data2 );
 * ksession.startProcess( "process1" );
 * ut.commit();
 * </pre>
 * 
 * <p>
 * To use a JPA the Environment must be set with both the EntityManagerFactory and the TransactionManager. If rollback occurs the ksession state is also rolled back, so you 
 * can continue to use it after a rollback. To load a previous persisted StatefulKnowledgeSession you'll need the id, as shown below:
 * </p>
 * 
 * <pre>
 * StatefulKnowledgeSession ksession = JPAKnowledgeService.loadStatefulKnowledgeSession( sessionId, kbase, null, env ); 
 * </pre>
 * 
 * <p>
 * To enable persistence the following classes must be added to your persistence.xml, as in the example below:
 * </p>
 * 
 * <pre>
 * &lt;persistence-unit name="org.drools.persistence.jpa" transaction-type="JTA"&gt;
 *    &lt;provider&gt;org.hibernate.ejb.HibernatePersistence&lt;/provider&gt;
 *    &lt;jta-data-source&gt;jdbc/BitronixJTADataSource&lt;/jta-data-source&gt;       
 *    &lt;class&gt;org.drools.persistence.session.SessionInfo&lt;/class&gt;
 *    &lt;class&gt;org.drools.persistence.processinstance.ProcessInstanceInfo&lt;/class&gt;
 *    &lt;class&gt;org.drools.persistence.processinstance.ProcessInstanceEventInfo&lt;/class&gt;
 *    &lt;class&gt;org.drools.persistence.processinstance.WorkItemInfo&lt;/class&gt;
 *    &lt;properties&gt;
 *          &lt;property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/&gt;	        
 *          &lt;property name="hibernate.max_fetch_depth" value="3"/&gt;
 *          &lt;property name="hibernate.hbm2ddl.auto" value="update" /&gt;
 *          &lt;property name="hibernate.show_sql" value="true" /&gt;
 *          &lt;property name="hibernate.transaction.manager_lookup_class" value="org.hibernate.transaction.BTMTransactionManagerLookup" /&gt;
 *    &lt;/properties&gt;
 * &lt;/persistence-unit&gt;
 * </pre>
 * 
 * <p>
 * The jdbc JTA data source would need to be previously bound, Bitronix provides a number of ways of doing this and it's docs shoud be contacted for more details, however
 * for quick start help here is the programmatic approach:
 * </p>
 * <pre>
 * PoolingDataSource ds = new PoolingDataSource();
 * ds.setUniqueName( "jdbc/BitronixJTADataSource" );
 * ds.setClassName( "org.h2.jdbcx.JdbcDataSource" );
 * ds.setMaxPoolSize( 3 );
 * ds.setAllowLocalTransactions( true );
 * ds.getDriverProperties().put( "user", "sa" );
 * ds.getDriverProperties().put( "password", "sasa" );
 * ds.getDriverProperties().put( "URL", "jdbc:h2:mem:mydb" );
 * ds.init();
 * </pre>
 * 
 * <p>
 * Bitronix also provides a simple embedded JNDI service, ideal for testing, to use it add a jndi.properties file to your META-INF and add the following line to it:
 * </p>
 * 
 * <pre>
 * java.naming.factory.initial=bitronix.tm.jndi.BitronixInitialContextFactory
 * </pre>
 */
public class JPAKnowledgeService {
    private static KnowledgeStoreService provider;

    public static StatefulKnowledgeSession newStatefulKnowledgeSession(KnowledgeBase kbase,
                                                                       KnowledgeSessionConfiguration configuration,
                                                                       Environment environment) {
        return getJPAKnowledgeServiceProvider().newStatefulKnowledgeSession( kbase,
                                                                             configuration,
                                                                             environment );
    }

    public static StatefulKnowledgeSession loadStatefulKnowledgeSession(long id,
                                                                        KnowledgeBase kbase,
                                                                        KnowledgeSessionConfiguration configuration,
                                                                        Environment environment) {
        return getJPAKnowledgeServiceProvider().loadStatefulKnowledgeSession( id,
                                                                              kbase,
                                                                              configuration,
                                                                              environment );
    }

    private static synchronized void setJPAKnowledgeServiceProvider(KnowledgeStoreService provider) {
        JPAKnowledgeService.provider = provider;
    }

    private static synchronized KnowledgeStoreService getJPAKnowledgeServiceProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }

    @SuppressWarnings("unchecked")
    private static void loadProvider() {
        try {
            // we didn't find anything in properties so lets try and us reflection
            Class<KnowledgeStoreService> cls = (Class<KnowledgeStoreService>) Class.forName( "org.drools.persistence.jpa.impl.KnowledgeStoreServiceImpl" );
            setJPAKnowledgeServiceProvider( cls.newInstance() );
        } catch ( Exception e ) {
            throw new RuntimeException( "Provider org.drools.persistence.jpa.impl.JPAKnowledgeStoreServiceImpl could not be set.",
                                        e );
        }
    }

}
