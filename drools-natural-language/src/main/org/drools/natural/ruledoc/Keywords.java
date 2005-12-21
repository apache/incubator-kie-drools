package org.drools.natural.ruledoc;

import java.io.IOException;
import java.util.Properties;

import org.drools.natural.NaturalLanguageException;

/** provides all the keywords */
public class Keywords
{
    
    public static Keywords instance;
    private Properties props;
    
    private Keywords(Properties p) {
        this.props = p;        
    }
    
    public static Keywords getInstance() {
        if (instance == null) {
            
            Properties props = new Properties();
            try {
                props.load(Keywords.class.getResourceAsStream("keywords.properties"));
            } catch (IOException e) {
                throw new NaturalLanguageException("Unable to load the keywords configuration properties.");
            }
            instance = new Keywords(props);
        }
        return instance;
    }
    
    /** 
     * Helper method to get a keyword
     */
    public static String getKeyword(String key) {
        return getInstance().props.getProperty(key);
    }
    
    
    

}
