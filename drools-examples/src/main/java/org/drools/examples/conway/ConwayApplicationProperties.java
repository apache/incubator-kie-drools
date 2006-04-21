package org.drools.examples.conway;

import java.util.ResourceBundle;

/**
 * A utility class for retrieving application properties
 * 
 * @author <a href="mailto:brown_j@ociweb.com">Jeff Brown</a>
 */
public class ConwayApplicationProperties
{
    private static ConwayApplicationProperties ourInstance = new ConwayApplicationProperties( );

    public static ConwayApplicationProperties getInstance()
    {
        return ourInstance;
    }

    private final ResourceBundle resources;

    private ConwayApplicationProperties()
    {
        resources = ResourceBundle.getBundle( "conway" );
    }

    public static String getProperty(String propertyName)
    {
        return ourInstance.resources.getString( propertyName );
    }
}
