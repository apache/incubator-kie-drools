package org.jbpm.persistence.util;

import static org.jbpm.persistence.util.PersistenceUtil.getBitronixProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Assert;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class PersistenceUtil {

    protected static final String BITRONIX_PROPERTIES_PROJECT_RELATIVE_PATH = "/bitronix.properties";
    
    public static Properties getBitronixProperties(Object thisObject) {
        Properties props = new Properties();
        String propertiesNotFound = "Unable to load bitronix properties [" + BITRONIX_PROPERTIES_PROJECT_RELATIVE_PATH + "]";
        
        InputStream propsInputStream = thisObject.getClass().getResourceAsStream(BITRONIX_PROPERTIES_PROJECT_RELATIVE_PATH);
        Assert.assertNotNull(propertiesNotFound, propsInputStream);
        try { 
            props.load(propsInputStream);
        }
        catch(IOException ioe) { 
            Assert.fail(propertiesNotFound + ": " + ioe.getMessage() );
            ioe.printStackTrace();
        }
        
        return props;
    }
    
    public static PoolingDataSource setupPoolingDataSource(Properties btmProps) {
        PoolingDataSource pds = new PoolingDataSource();
        
        // The name must match what's in the persistence.xml!
        pds.setUniqueName( "jdbc/testDS1" );
        
        pds.setClassName( btmProps.getProperty("className") );
        pds.setMaxPoolSize( Integer.parseInt(btmProps.getProperty("maxPoolSize")) );
        pds.setAllowLocalTransactions( Boolean.parseBoolean(btmProps.getProperty("allowLocalTransactions")));
        for( String propertyName : new String [] { "user", "password" } ) { 
            pds.getDriverProperties().put( propertyName, btmProps.getProperty(propertyName));
        }
        
        if( pds.getClassName().startsWith("org.h2") ) { 
            String propertyName = "URL";
            pds.getDriverProperties().put( propertyName, btmProps.getProperty(propertyName));
            
        }
        if( pds.getClassName().startsWith("org.postgresql") ) { 
            for( String propertyName : new String [] { "serverName", "portNumber", "databaseName" } ) { 
	            pds.getDriverProperties().put( propertyName, btmProps.getProperty(propertyName));
	        }
        }
            
        return pds;
    }
    
}
