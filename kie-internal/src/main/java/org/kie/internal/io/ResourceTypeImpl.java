package org.kie.internal.io;

import java.util.Properties;

import org.kie.api.io.ResourceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceTypeImpl {
    public static final String                    KIE_RESOURCE_CONF_CLASS = "kie.resource.conf.class";
    
    private static final Logger                    logger                  = LoggerFactory.getLogger( ResourceTypeImpl.class );

    public static Properties toProperties(ResourceConfiguration conf) {
        Properties prop = conf.toProperties();
        prop.setProperty( KIE_RESOURCE_CONF_CLASS, conf.getClass().getName() );
        return prop;
    }

    public static ResourceConfiguration fromProperties(Properties prop) {
        String className = prop.getProperty( KIE_RESOURCE_CONF_CLASS );
        try {
            // not sure how to get the proper classloader here, but the resource configurations
            // should be accessible from the current classloader
            ResourceConfiguration conf = (ResourceConfiguration) Class.forName( className ).newInstance();
            conf.fromProperties( prop );
            return conf;
        } catch ( Exception e ) {
            logger.error( "Error loading resource configuration from properties", e );
        }
        return null;
    }

    

}
