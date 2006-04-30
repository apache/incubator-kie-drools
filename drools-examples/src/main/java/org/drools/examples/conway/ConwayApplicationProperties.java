package org.drools.examples.conway;

import java.util.ResourceBundle;

/**
 * A utility class for retrieving application properties
 * 
 * @author <a href="mailto:brown_j@ociweb.com">Jeff Brown</a>
 */
public class ConwayApplicationProperties {
    private static final ConwayApplicationProperties ourInstance = new ConwayApplicationProperties();

    public static ConwayApplicationProperties getInstance() {
        return ConwayApplicationProperties.ourInstance;
    }

    private final ResourceBundle resources;

    private ConwayApplicationProperties() {
        this.resources = ResourceBundle.getBundle( "conway.conway" );
    }

    public static String getProperty(final String propertyName) {
        return ConwayApplicationProperties.ourInstance.resources.getString( propertyName );
    }
}
