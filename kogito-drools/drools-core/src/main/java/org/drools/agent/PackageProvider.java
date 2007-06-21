package org.drools.agent;

import java.util.List;

import org.drools.RuleBase;

/**
 * All sources of packages must implement this. 
 * @author Michael Neale
 *
 */
public abstract class PackageProvider {

    /**
     * Perform the scan, adding in any packages changed to the rulebase.
     * If there was an error reading the packages, this will not fail, it will 
     * just do nothing (as there may be a temporary IO issue). 
     */
    abstract void updateRuleBase(RuleBase rb, boolean removeExistingPackages);
    
    
    /**
     * This will be passed a list of configuration strings which were extracted from the config property.
     */
    abstract void configure(List configList);
    
    

    
}
