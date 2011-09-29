package org.drools.marshalling;

import static org.drools.marshalling.util.MarshallingDBUtil.initializeTestDb;
import static org.drools.persistence.util.PersistenceUtil.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.Table;

import org.drools.marshalling.util.MarshalledData;
import org.drools.marshalling.util.MarshallingEntityManagerFactory;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.util.PersistenceUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class CompareMarshalledDataTest {

    private static Logger logger = LoggerFactory.getLogger(CompareMarshalledDataTest.class);
    private HashMap<String, Object> testContext;
    private EntityManagerFactory emf;
    
    @Before
    public void setup() { 
       testContext = PersistenceUtil.setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME); 
       emf = (EntityManagerFactory) testContext.get(ENTITY_MANAGER_FACTORY);
    }
    
    @After
    public void tearDown() { 
       PersistenceUtil.tearDown(testContext); 
    }
    
    
    public void retrieveMarshalledDataObjects() {
        EntityManager em = emf.createEntityManager();
        List<Object> resultList = em.createQuery("From MarshalledData").getResultList();
        Iterator<Object> iter = resultList.iterator();
        while(iter.hasNext()) { 
          MarshalledData marshalledData = (MarshalledData) iter.next();
          marshalledData.toString();
        }
    }
    
    @Test
    @Ignore
    public void createBaseDatabase() { 
        Properties dsProps = PersistenceUtil.getDatasourceProperties();
        String driverClass = dsProps.getProperty("driverClassName");
        if ( ! driverClass.startsWith("org.h2")) {
            return;
        }
        
        String jdbcUrl = initializeTestDb(dsProps, CompareMarshalledDataTest.class);
        String baseJdbcUrl = jdbcUrl.replace("testData", "baseData");

        // Setup the datasource
        PoolingDataSource ds1 = setupPoolingDataSource();
        ds1.getDriverProperties().setProperty("url", jdbcUrl);
        ds1.init();

        // Setup persistence
        Properties overrideProperties = new Properties();
        overrideProperties.setProperty("hibernate.connection.url", jdbcUrl);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(DROOLS_PERSISTENCE_UNIT_NAME, overrideProperties);
        
        EntityManager em = emf.createEntityManager();
        
        Query query = em.createQuery("From " + SessionInfo.class.getSimpleName());
        List sessionInfoList = query.getResultList();
        logger.info("" + sessionInfoList.size());
        
        
    }
  

}
