package org.drools.builder;

public interface DecisionTableConfiguration extends ResourceConfiguration {
    
    void setInputType(DecisionTableInputType inputType);
    
    DecisionTableInputType getInputType();    
    
    void setWorksheetName(String name);
    
    String getWorksheetName();       
}
