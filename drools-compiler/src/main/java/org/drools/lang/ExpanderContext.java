package org.drools.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.drools.lang.dsl.DefaultExpander;

/**
 * Create instances of appropriate expanders based on name, and prior configuration of expanders.
 * 
 * Expanders can be added to this programmatically, or they can be loaded from the classpath.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class ExpanderContext implements Serializable {

	private static final long serialVersionUID = 1806461802987228880L;
	private Map expanders;
    
    private static final ExpanderContext INSTANCE = new ExpanderContext();
    
	public static ExpanderContext getInstance() {
	    return INSTANCE;
    }
    
    private ExpanderContext() {
        expanders = new HashMap();
    }
    
    /** 
     * This registers an expander against a name for DRL files to use when compiling.
     * The name appears as "use expander name" in DRL source.
     * 
     * This can be called again to "refresh" an expander.
     */
    public void registerExpander(Expander expander, String name) {
        if (expanders.containsKey(name)) {
            expanders.remove(name);
        }
        expanders.put(name, expander);
    }
    
    /** 
     * This will load up the appropriate expander.
     * The default expander is configured via a properties file. 
     */
    public Expander getExpander(String expanderName) {
    
        if (expanders.containsKey(expanderName)) return (Expander) expanders.get(expanderName);
        
        try {
            InputStream stream = this.getClass().getResourceAsStream(expanderName);
            if (stream == null) {
                URL url = new URL(expanderName);
                stream = url.openStream();
            }
            
            Properties props = new Properties();
            props.load(stream);
            stream.close();
            
            DefaultExpander expander = new DefaultExpander(props);
            registerExpander(expander, expanderName);
            return expander;
            
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to load expander with name: " + expanderName);
        }
    }
	
	
	
	
	
}
