/*
 * Created on 26/05/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.drools.testing.core.configuration;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mshaw
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ApplicationProperties {

	/**
     * A reference to the AppProperties Singleton object.
     */
    private static ApplicationProperties s_AppProperties;
    private Properties m_Properties;
    private String m_FileName = "/org/drools/testing/core/resources/rtl/rtl.properties";
    public final static String SYSTEM_PROPERTY = "rtl.properties.file";
    public final static String ENVIRONMENT_PROPERTY = "environment";
    public final static String DELIMETER = "_";
    
    static Log log = LogFactory.getLog(ApplicationProperties.class);


    /**
     * Create a new properties instance.
     *
     * Retrieves the properties as a RESOURCE.
     */
    private ApplicationProperties() {

        String methodName = "ApplicationProperties()";

        BufferedInputStream bis = null;
        m_Properties = new Properties();

        log.info("AppProperties - using property file: " + m_FileName);

        // Load the properties
        try {
            bis = new BufferedInputStream(ApplicationProperties.class.getResourceAsStream( m_FileName ));
            m_Properties.load(bis);
        }
        catch (Exception ex) {
            log.error(ex);
        }
        finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            }
            catch (IOException ex) { /* Cannot do anything here */}
            
        }
    }

    /**
     * load properties from the file system
     *
     */
    private ApplicationProperties(String fileName) {

        String methodName = "ApplicationProperties()";

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        m_Properties = new Properties();

        log.info("AppProperties - using property file: " + fileName);

        // Load the properties
        try {
            fis = new FileInputStream(fileName);
            bis = new BufferedInputStream(ApplicationProperties.class.getResourceAsStream( m_FileName ));
            m_Properties.load(bis);
        }
        catch (Exception ex) {
            log.error(ex);
        }
        finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            }
            catch (IOException ex) { /* Cannot do anything here */}
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (IOException ex) { /* Cannot do anything here */}
        }
    }
    
    /**
     * Returns a String associated with the property specified by
     * propertyName.
     *
     * @param propertyName   a string representing the property
     * @return   the associated value
     */
    public String getProperty(String propertyName) throws PropertyNotFoundException {

            String result = m_Properties.getProperty(propertyName);

            if (result == null) {
                throw new PropertyNotFoundException("Could not find property " + propertyName + " in file " +
                    m_FileName);
            }

            return result;

    }

    /**
     * Returns a String associated with the property specified by
     * propertyName.
     *
     * @param propertyName   a string representing the property
     * @return   the associated value
     */
    public String getEnvironmentSpecificProperty(String propertyName) throws PropertyNotFoundException {
        String environment = System.getProperty(ENVIRONMENT_PROPERTY);
        if (environment == null) {
            throw new IllegalStateException("The System parameter: " + ENVIRONMENT_PROPERTY + " must be set");
        }
        String result = m_Properties.getProperty(environment + DELIMETER + propertyName);

        if (result == null) {
            throw new PropertyNotFoundException("Could not find environement specific property " + environment +
                                                DELIMETER + propertyName + " in file " + m_FileName);
        }

        return result;
    }

    /**
     * Returns an array of Strings associated with the property specified by
     * propertyName. The method is used for entries that potentially have
     * multiple values separated by a particular character or string.
     *
     * Typically this will be used for space separated words:
     *    String[] entries =
     *       AppProperties.getInstance().getPropertyEntries("PropertyName", " ");
     *
     * @param    propertyName    the name of the property
     * @param    separatorString the string that separates each entry
     */
    public String[] getPropertyEntries(String propertyName, String separatorString) throws PropertyNotFoundException {
        String prop = m_Properties.getProperty(propertyName);

        if (prop == null) {
            throw new PropertyNotFoundException("Could not find property " + propertyName + " in file " + m_FileName);
        }

        StringTokenizer tokeniser = new StringTokenizer(prop, separatorString);
        String[] result = new String[tokeniser.countTokens()];
        int i = 0;

        while (tokeniser.hasMoreElements()) {
            result[i++] = tokeniser.nextToken();
        }

        return result;
    }

    /**
     * Returns an Iterator of property names
     *
     * @return all the property names
     */
    public Iterator getPropertyNames() {
        return m_Properties.keySet().iterator();
    }

    /**
     *   method used to reload the property list at runtime
     *
     *
     */
    public void loadProprtyFile(String fileName) {
        String methodName = "loadProprtyFile(String fileName)";
        if (fileName == null) {
            log.error("File name is null");
        }
        else {
            s_AppProperties = new ApplicationProperties(fileName);
        }
    }

    /**
     *   A string representaion of the properties in the Properties Table
     */

    public String propertiesToString() {
        if (m_Properties == null) {
            return ("Properties have not been initialised");
        }
        else {
            String props = "";
            Iterator propertyKeys = getPropertyNames();
            while (propertyKeys.hasNext()) {
                String currentKey = (String) propertyKeys.next();
                props += currentKey + "=" + m_Properties.get(currentKey) + "\n";
            }

            return props;
        }
    }

    /**
     * Returns the instance of the AppProperties class
     *
     * @return the singleton instance
     */
    public static ApplicationProperties getInstance() {
        if (s_AppProperties == null) {
            s_AppProperties = new ApplicationProperties();
        }

        return s_AppProperties;
    }

    public String getFileName() {
        return m_FileName;
    }
    
    public static void flushProperties() {
    	String methodName = "flushProperties()";
    	s_AppProperties = null;	
    }
    
    /**
     * Get a property but if it does not exist return the default value supplied in the 
     * method call.
     * @param property
     * @param defaultValue
     * @return
     */
	public String getPropertyWithDefault(String property, String defaultValue)
	{
		try {
			return getProperty(property);
		} catch (PropertyNotFoundException e) {
			return defaultValue;
		} 
	}    
    
    /**
     * Get a property and return it as an int. Throws a property not found exception
     * if the property exists but cannot be formatted.
     * @param property
     * @return
     * @throws PropertyNotFoundException
     */
    public int getPropertyAsInt(String property) throws PropertyNotFoundException
    {
    	try {
			return new Integer(getProperty(property)).intValue();
		} catch (NumberFormatException e) {
			throw new PropertyNotFoundException("Property was found but could not converted to an int");
		} 
    }
    
    /**
     * Get a property and return it as an int. Returns the default value if the
     * property cannot be found or formatted as an int.
     * @param property
     * @param defaultValue
     * @return
     */
	public int getPropertyAsIntWithDefault(String property, int defaultValue)
	{
		try {
			return new Integer(getProperty(property)).intValue();
		} catch (Exception e) {
			return defaultValue;
		} 
	}
}
