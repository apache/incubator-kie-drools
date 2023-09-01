package org.kie.api.runtime.process;

import java.util.Map;

/**
 * Top level of Case(File)Data that holds all shared information within a case.
 *
 */
public interface CaseData {

    /**
     * Returns all available case data for given case.
     * @return
     */
    Map<String, Object> getData();
    
    /**
     * Returns case data for given case registered under given name.
     * @return
     */
    Object getData(String name);
    
    
    /**
     * Add single data item into existing case file
     * (replaces already existing data that matches with input)
     * @param name
     * @param data
     */
    void add(String name, Object data);
    
    /**
     * Remove permanently given data from existing case file
     * @param name
     */
    void remove(String name);
    
    
    /**
     * Returns case definition id of this case file
     * @return
     */
    String getDefinitionId();
}
