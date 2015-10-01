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

package org.kie.internal.persistence.jpa;

import org.kie.api.KieBase;
import org.kie.api.persistence.jpa.KieStoreServices;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * <p>
 * Long term out of the box persistence of runtime state with JPA is possible with Drools and jBPM.
 * You will need to configure a JPA entity manager (e.g. using hibernate) and have a JTA transaction
 * manager (for development/testing purposes we recommend Bitronix as it's simple to setup and works
 * embedded, but for production the use of JBoss Transactions is recommended).
 * </p>
 *
 * <pre>
 * Environment env = KnowledgeBaseFactory.newEnvironment();
 * env.set( EnvironmentName.ENTITY_MANAGER_FACTORY, Persistence.createEntityManagerFactory( "emf-name" ) );
 * env.set( EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager() );
 *
 * StatefulKnowledgeSession ksession = JPAKnowledgeService.newKieSession( kbase, null, env ); // KnowledgeSessionConfiguration may be null, and a default will be used
 * int sessionId = ksession.getId();
 *
 * ksession.insert( data1 );
 * ksession.insert( data2 );
 * ksession.startProcess( "process1" );
 * </pre>
 *
 * <p>
 * To use a JPA the Environment must be set with both the EntityManagerFactory and the TransactionManager. If rollback occurs the ksession state is also rolled back, so you
 * can continue to use it after a rollback. To load a previous persisted StatefulKnowledgeSession you'll need the id, as shown below:
 * </p>
 *
 * <pre>
 * StatefulKnowledgeSession ksession = JPAKnowledgeService.loadKieSession( sessionId, kbase, null, env );
 * </pre>
 *
 * <p>
 * If you do not define any transaction boundaries, each command (i.e. each invocation of a method of
 * the session) will be executed inside its own transaction.  You can define the transaction boundaries
 * yourself using a JTA UserTransaction.
 * </p>
 *
 * <pre>
 * UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
 * ut.begin();
 * ksession.insert( data1 );
 * ksession.insert( data2 );
 * ksession.startProcess( "process1" );
 * ut.commit();
 * </pre>
 *
 * <p>
 * To enable persistence the following classes must be added to your persistence.xml, as in the example below:
 * </p>
 *
 * <pre>
 * &lt;persistence-unit name="org.jbpm.persistence.jpa" transaction-type="JTA"&gt;
 *    &lt;provider&gt;org.hibernate.ejb.HibernatePersistence&lt;/provider&gt;
 *    &lt;jta-data-source&gt;jdbc/BitronixJTADataSource&lt;/jta-data-source&gt;
 *    &lt;class&gt;org.jbpm.persistence.processinstance.ProcessInstanceInfo&lt;/class&gt;
 *    &lt;class&gt;org.kie.api.persistence.info.ProcessInstanceInfo&lt;/class&gt;
 *    &lt;class&gt;org.kie.api.persistence.info.WorkItemInfo&lt;/class&gt;
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
 * Bitronix also provides a simple embedded JNDI service, ideal for testing, to use it add a jndi.properties file at the root of your classpath and add the following line to it:
 * </p>
 *
 * <pre>
 * java.naming.factory.initial=bitronix.tm.jndi.BitronixInitialContextFactory
 * </pre>
 */
public class JPAKnowledgeService {
    private static KieStoreServices provider;

    public static StatefulKnowledgeSession newStatefulKnowledgeSession(KieBase kbase,
                                                                       KieSessionConfiguration configuration,
                                                                       Environment environment) {
        return (StatefulKnowledgeSession)getJPAKnowledgeServiceProvider().newKieSession(kbase,
                configuration,
                environment);
    }

    /**
     * Deprecated use {@link #loadStatefulKnowledgeSession(Long, KieBase, KieSessionConfiguration, Environment)} instead
     */
    @Deprecated
    public static StatefulKnowledgeSession loadStatefulKnowledgeSession(int id,
                                                                        KieBase kbase,
                                                                        KieSessionConfiguration configuration,
                                                                        Environment environment) {
        return (StatefulKnowledgeSession)getJPAKnowledgeServiceProvider().loadKieSession(id,
                kbase,
                configuration,
                environment);
    }

    public static StatefulKnowledgeSession loadStatefulKnowledgeSession(Long id,
            KieBase kbase,
            KieSessionConfiguration configuration,
            Environment environment) {
		return (StatefulKnowledgeSession)getJPAKnowledgeServiceProvider().loadKieSession(id,
		kbase,
		configuration,
		environment);
	}

    private static synchronized void setJPAKnowledgeServiceProvider(KieStoreServices provider) {
        JPAKnowledgeService.provider = provider;
    }

    private static synchronized KieStoreServices getJPAKnowledgeServiceProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }

    @SuppressWarnings("unchecked")
    private static void loadProvider() {
        String className = System.getProperty( "org.kie.store.services.class",  "org.drools.persistence.jpa.KnowledgeStoreServiceImpl" );
        try {
            Class<KieStoreServices> cls = (Class<KieStoreServices>) Class.forName( className );
            setJPAKnowledgeServiceProvider( cls.newInstance() );
        } catch ( Exception e ) {
            throw new RuntimeException( "Provider " + className + " could not be set.", e );
        }
    }

}
