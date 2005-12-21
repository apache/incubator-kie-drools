package org.drools.natural.ruledoc;

import java.util.Properties;

public class DictionaryHelper
{
    
    private Properties props;
    
    public DictionaryHelper(Properties properties) {
        props = properties;
    }
    
    public String getItem(String key) {
        return props.getProperty(key);
    }
    
    public String getFunctions() {
        return props.getProperty("functions");
    }
    
    public String getImports() {
        if (props.containsKey("import")) return props.getProperty("import");        
        return props.getProperty("imports");
    }
}
