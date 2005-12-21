package org.drools.natural.ruledoc;

import java.util.Properties;

public class TableState extends ParseState
{

    private String name;
    private Properties data;
    private boolean inKey;
    
    
    public TableState(String name) {
        this.name = name;
        data = new Properties();
    }
    
    
    
    void parseChunk(String text)
    {
        
        
        

    }

}
