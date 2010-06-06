package org.drools.builder.conf.impl;

import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;

public class DecisionTableConfigurationImpl implements DecisionTableConfiguration {
    
    private DecisionTableInputType inputType;
    
    private String worksheetName;
    
    public DecisionTableConfigurationImpl() {
        
    }
    
    public void setInputType(DecisionTableInputType inputType) {
        this.inputType = inputType;
    }    
    
    public DecisionTableInputType getInputType() {
        return this.inputType;
    }

    public void setWorksheetName(String worksheetName) {
        this.worksheetName = worksheetName;
    }

    public String getWorksheetName() {
        return this.worksheetName;
    }
    
}
