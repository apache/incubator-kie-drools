package org.drools.agent;

import java.util.List;

import org.drools.RuleBase;

public class DirectoryScanner extends PackageProvider {

    private List dir;

    void configure(List configList) {
        this.dir = configList;
        //now check to see whats in them dir...
        if (configList.size() > 1) {
            throw new IllegalArgumentException("You can only monitor one directory at a time this way.");
        }
        
    }

    void updateRuleBase(RuleBase rb, boolean removeExistingPackages) {
        
    }

}
